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
import java.io.IOException;
import java.io.InputStream;

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
public class WorkFolder extends TemporaryFolder {

	private Class<?> resourceSearchBaseClass;
	private Class<?> currentTestClass;

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
				}
			}
		};
	}

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

	public File newResourceCopy(String targetPath, String resourceName) throws IOException {
		if ((null == targetPath) || (null == resourceName)) {
			throw new NullPointerException();
		}
		final InputStream resourceStream = openResource(resourceName);
		return newFile(targetPath, resourceStream);
	}

	public File newResourceCopy(String resourceName) throws IOException {
		return newResourceCopy(resourceName, resourceName);
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

	private void copyContents(File target, InputStream initialContents) throws IOException {
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

	private void silentClose(Closeable stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException e) {
				// Exception silently ignored
			}
		}
	}

	private InputStream openResource(String resourceName) throws IOException {
		final Class<?> searchBase = (null != resourceSearchBaseClass) ? resourceSearchBaseClass : currentTestClass;
		return searchBase.getResourceAsStream(resourceName);
	}

}
