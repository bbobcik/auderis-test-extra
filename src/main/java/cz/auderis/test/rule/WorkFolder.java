/*
 * Copyright 2015 Boleslav Bobcik - Auderis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.auderis.test.rule;

import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * Extension of {@link TemporaryFolder} that has better support for initial content preparation.
 * <p>
 * Initialization examples:
 * <pre>
 *     {@code @Rule} public WorkFolder work1 = WorkFolder.basic();
 *     {@code @Rule} public WorkFolder work2 = WorkFolder.subfolderOf(new File("/tmp/junit");
 *     {@code @Rule} public WorkFolder work3 = WorkFolder.basic().withResourceSearchBase(SomeClass.class);
 * </pre>
 * Usage examples:
 * <pre>
 *     File res = work1.newResourceCopy("testResource");
 *     File resWithPath = work1.newResourceCopy("target/dir/copy.bin", "testResource.bin");
 * </pre>
 */
public class WorkFolder extends TemporaryFolder implements WorkFolderInterface {

	private Class<?> resourceSearchBaseClass;
	private Class<?> currentTestClass;
	private FolderBasedClassLoader folderClassLoader;
	private ClassLoader originalContextClassLoader;
	private boolean contextClassLoaderInstalled;

	/**
	 * Creates a new instance of {@code WorkFolder}.
	 * @return
	 */
	public static WorkFolder basic() {
		return new WorkFolder();
	}

	public static WorkFolder subfolderOf(File parentFolder) {
		return new WorkFolder(parentFolder);
	}

	public static WorkFolder subfolderOf(String parentFolderPath) {
		final File parentFolder;
		if ((null == parentFolderPath) || (parentFolderPath.isEmpty())) {
			parentFolder = null;
		} else {
			parentFolder = new File(parentFolderPath);
		}
		return new WorkFolder(parentFolder);
	}

	protected WorkFolder() {
		super();
	}

	protected WorkFolder(File parentFolder) {
		super(parentFolder);
	}

	public WorkFolder withResourceSearchBase(Class<?> searchBase) {
		resourceSearchBaseClass = searchBase;
		return this;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		final Class<?> testClass = description.getTestClass();
		final Statement statement = super.apply(base, description);
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					currentTestClass = testClass;
					statement.evaluate();
				} finally {
					currentTestClass = null;
					if (contextClassLoaderInstalled) {
					    synchronized (WorkFolder.this) {
                            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
                            contextClassLoaderInstalled = false;
                        }
                    }
				}
			}
		};
	}

	@Override
	public File asFile() {
		return getRoot();
	}

	@Override
	public String absolutePath() {
		return getRoot().getAbsolutePath();
	}

	@Override
	public String relativePath() {
		return ".";
	}

	@Override
	public WorkFolderInterface getRootFolder() {
		return this;
	}

	@Override
	public File newFile() throws IOException {
		return super.newFile();
	}

	@Override
	public File newFile(String targetPath, CharSequence initialContents) throws IOException {
		if (null == targetPath) {
			throw new NullPointerException();
		}
		final File target = prepareTargetFile(targetPath);
		if (null != initialContents) {
			writeContents(target, initialContents);
		} else {
			target.createNewFile();
		}
		return target;
	}

	@Override
	public File newFile(String targetPath, InputStream initialContents) throws IOException {
		if (null == targetPath) {
			throw new NullPointerException();
		}
		final File target = prepareTargetFile(targetPath);
		if (null != initialContents) {
			copyContents(target, initialContents);
		} else {
			target.createNewFile();
		}
		return target;
	}

    @Override
    public File newFile(String targetPath, InitialContentsProvider contentsProvider) throws IOException {
	    final InputStream stream = (null != contentsProvider) ? contentsProvider.getContents() : null;
	    return newFile(targetPath, stream);
    }

    @Override
	public File newResourceCopy(String targetPath, String resourceName) throws IOException {
		if ((null == targetPath) || (null == resourceName)) {
			throw new NullPointerException();
		}
		final InputStream resourceStream = openResource(resourceName);
		return newFile(targetPath, resourceStream);
	}

	@Override
	public File newResourceCopy(String resourceName) throws IOException {
		final String fileName = stripPath(resourceName);
		return newResourceCopy(fileName, resourceName);
	}

	@Override
	public WorkFolderInterface subfolder(String... pathComponents) throws IOException {
		if (0 == pathComponents.length) {
			throw new IllegalArgumentException("no path components given");
		}
		int relativePathLength = computeRelativePathLength(pathComponents);
		final StringBuilder relativePathBuilder = new StringBuilder(relativePathLength);
		File dir = getRoot();
		prepareSubdirs(pathComponents, dir, relativePathBuilder);
		final WorkSubFolder folder = new WorkSubFolder(this, relativePathBuilder.toString());
		return folder;
	}

	@Override
	public void clean() throws IOException {
		recursiveDelete(getRoot());
	}

	public ClassLoader getClassLoaderForResources() {
        return getClassLoaderForResources(null);
    }

    public synchronized ClassLoader getClassLoaderForResources(ClassLoader parentClassLoader) {
        parentClassLoader = resolveParentClassLoader(parentClassLoader);
        final ClassLoader result;
        if (null == folderClassLoader) {
            folderClassLoader = new FolderBasedClassLoader(parentClassLoader);
            result = folderClassLoader;
        } else if (folderClassLoader.getParent() != parentClassLoader) {
            result = new FolderBasedClassLoader(parentClassLoader);
        } else {
            result = folderClassLoader;
        }
        return result;
    }

    public synchronized void installContextClassLoaderForResources(ClassLoader parentClassLoader) {
        final ClassLoader resourceClassLoader = getClassLoaderForResources(parentClassLoader);
        final Thread currentThread = Thread.currentThread();
        if (!contextClassLoaderInstalled) {
            originalContextClassLoader = currentThread.getContextClassLoader();
        }
        currentThread.setContextClassLoader(resourceClassLoader);
        contextClassLoaderInstalled = true;
    }

    private ClassLoader resolveParentClassLoader(ClassLoader providedParent) {
	    if (null != providedParent) {
	        return providedParent;
        }
        //
        final ClassLoader contextClassLoader;
	    if (contextClassLoaderInstalled) {
	        contextClassLoader = originalContextClassLoader;
        } else {
	        contextClassLoader = Thread.currentThread().getContextClassLoader();
        }
        if (null != contextClassLoader) {
	        return contextClassLoader;
        }
        //
        final ClassLoader resourceClassLoader;
	    if (null != resourceSearchBaseClass) {
	        resourceClassLoader = resourceSearchBaseClass.getClassLoader();
        } else {
	        resourceClassLoader = null;
        }
        if (null != resourceClassLoader) {
	        return resourceClassLoader;
        }
        //
        final ClassLoader testClassLoader;
	    if (null != currentTestClass) {
	        testClassLoader = currentTestClass.getClassLoader();
        } else {
	        testClassLoader = null;
        }
        if (null != testClassLoader) {
            return testClassLoader;
        }
	    //
        return ClassLoader.getSystemClassLoader();
    }

	private File prepareTargetFile(String targetPath) {
		final File target = new File(getRoot(), targetPath);
		// Ensure that folder structure up to the target file exists
		final File fileFolder = target.getParentFile();
		if ((null != fileFolder) && !fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		return target;
	}

	private InputStream openResource(String resourceName) throws IOException {
		final Class<?> searchBase = (null != resourceSearchBaseClass) ? resourceSearchBaseClass : currentTestClass;
		return searchBase.getResourceAsStream(resourceName);
	}

	Class<?> getResourceSearchBaseClass() {
		return (null != resourceSearchBaseClass) ? resourceSearchBaseClass : currentTestClass;
	}

	static int computeRelativePathLength(String[] pathComponents) {
		int relativePathLength = 0;
		for (String component : pathComponents) {
			if (null == component) {
				throw new IllegalArgumentException("path component is null");
			} else if (component.isEmpty()) {
				throw new IllegalArgumentException("path component is empty");
			}
			relativePathLength = relativePathLength + component.length() + 1;
		}
		return relativePathLength;
	}

	static void prepareSubdirs(String[] pathComponents, File baseDir, StringBuilder pathBuilder) {
		File dir = baseDir;
		char sep = 0;
		for (String component : pathComponents) {
			if (0 == sep) {
				sep = '/';
			} else {
				pathBuilder.append(sep);
			}
			pathBuilder.append(component);
			dir = new File(dir, component);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}

	static String stripPath(String resourceName) {
		// Get filename part from resource name
		int filenameStart = 0;
		final int lastSeparator = resourceName.lastIndexOf('/');
		if (-1 != lastSeparator) {
			filenameStart = 1 + lastSeparator;
		} else if ('/' != File.separatorChar) {
			final int lastAltSeparator = resourceName.lastIndexOf(File.separatorChar);
			if (-1 != lastAltSeparator) {
				filenameStart = 1 + lastAltSeparator;
			}
		}
		if (filenameStart >= resourceName.length()) {
			throw new IllegalArgumentException("Cannot find filename part in resource name: " + resourceName);
		}
		return (0 != filenameStart) ? resourceName.substring(filenameStart) : resourceName;
	}

    /**
     * Copies the {@code initialContents} to the given target file and closes all streams afterwards.
     */
	static void copyContents(File target, InputStream initialContents) throws IOException {
		final FileOutputStream targetStream = new FileOutputStream(target, false);
		try {
			final byte[] buffer = new byte[8192];
			int readBytes;
			while (-1 != (readBytes = initialContents.read(buffer))) {
				targetStream.write(buffer, 0, readBytes);
			}
		} finally {
			silentClose(targetStream);
			silentClose(initialContents);
		}
	}

	static void writeContents(File target, CharSequence initialContents) throws IOException {
		final FileWriter writer = new FileWriter(target, false);
		try {
			writer.append(initialContents);
		} finally {
			silentClose(writer);
		}
	}

	static void silentClose(Closeable stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException e) {
				// Exception silently ignored
			}
		}
	}

	static void recursiveDelete(File dir) {
		final Deque<File> subdirs = new LinkedList<>();
		final Deque<File> deletePending = new LinkedList<>();
		final boolean targetClear = deleteDirectoryFiles(dir, subdirs);
		if (!targetClear) {
			while (!subdirs.isEmpty()) {
				final File targetDir = subdirs.pop();
				final boolean dirClean = deleteDirectoryFiles(targetDir, subdirs);
				if (dirClean) {
					targetDir.delete();
				} else {
					deletePending.add(targetDir);
				}
			}
			for (File dirToDelete : deletePending) {
				dirToDelete.delete();
			}
		}
	}

	private static boolean deleteDirectoryFiles(File dir, Deque<File> subdirStore) {
		final File[] files = dir.listFiles();
		if ((null == files) || (0 == files.length)) {
			return true;
		}
		boolean noDirsFound = true;
		for (File file : files) {
			if (file.isDirectory()) {
				noDirsFound = false;
				subdirStore.addFirst(file);
			} else {
				file.delete();
			}
		}
		return noDirsFound;
	}

	final class FolderBasedClassLoader extends ClassLoader {
        FolderBasedClassLoader(ClassLoader parentClassLoader) {
            super(parentClassLoader);
        }

        @Override
        protected URL findResource(String resourcePath) {
            final File resourceFile = new File(getRoot(), resourcePath);
            if (!resourceFile.isFile()) {
                return null;
            }
            try {
                return resourceFile.toURI().toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }

        @Override
        protected Enumeration<URL> findResources(String resourcePath) throws IOException {
            final File resourceFile = new File(getRoot(), resourcePath);
            if (!resourceFile.isFile()) {
                return Collections.emptyEnumeration();
            }
            final URL resourceURL = resourceFile.toURI().toURL();
            return Collections.enumeration(Collections.singletonList(resourceURL));
        }
    }

}
