package cz.auderis.test.rule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
class WorkSubFolder implements WorkFolderInterface {

    private final WorkFolder rootFolder;
    private final String relativePath;
    private final File folderFile;


    WorkSubFolder(WorkFolder rootFolder, String relativePath) {
        this.rootFolder = rootFolder;
        this.relativePath = relativePath;
        this.folderFile = new File(rootFolder.asFile(), relativePath);
    }

    @Override
    public File asFile() {
        return folderFile;
    }

    @Override
    public String absolutePath() {
        return folderFile.getAbsolutePath();
    }

    @Override
    public String relativePath() {
        return relativePath;
    }

    @Override
    public WorkFolderInterface getRootFolder() {
        return rootFolder;
    }

    @Override
    public File newFile(String targetPath) throws IOException {
        if (null == targetPath) {
            throw new NullPointerException();
        }
        final File f = prepareTargetFile(targetPath);
        f.createNewFile();
        return f;
    }

    @Override
    public File newFile(String targetPath, CharSequence initialContents) throws IOException {
        if (null == targetPath) {
            throw new NullPointerException();
        }
        final File f = prepareTargetFile(targetPath);
        if (null != initialContents) {
            WorkFolder.writeContents(f, initialContents);
        } else {
            f.createNewFile();
        }
        return f;
    }

    @Override
    public File newFile(String targetPath, InputStream initialContents) throws IOException {
        if (null == targetPath) {
            throw new NullPointerException();
        }
        final File f = prepareTargetFile(targetPath);
        if (null != initialContents) {
            WorkFolder.copyContents(f, initialContents);
        } else {
            f.createNewFile();
        }
        return f;
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
        final String fileName = WorkFolder.stripPath(resourceName);
        return newResourceCopy(fileName, resourceName);
    }

    @Override
    public WorkFolderInterface subfolder(String... pathComponents) throws IOException {
        if (0 == pathComponents.length) {
            throw new IllegalArgumentException("no path components given");
        }
        int relativePathLength = relativePath.length() + WorkFolder.computeRelativePathLength(pathComponents);
        final StringBuilder relativePathBuilder = new StringBuilder(relativePathLength);
        relativePathBuilder.append(relativePath);
        WorkFolder.prepareSubdirs(pathComponents, folderFile, relativePathBuilder);
        final WorkSubFolder folder = new WorkSubFolder(rootFolder, relativePathBuilder.toString());
        return folder;
    }

    @Override
    public void clean() throws IOException {
        WorkFolder.recursiveDelete(folderFile);
    }

    private File prepareTargetFile(String targetPath) {
        final File target = new File(folderFile, targetPath);
        // Ensure that folder structure up to the target file exists
        final File fileFolder = target.getParentFile();
        if ((null != fileFolder) && !fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        return target;
    }

    private InputStream openResource(String resourceName) throws IOException {
        final Class<?> searchBase = rootFolder.getResourceSearchBaseClass();
        final InputStream streamAsIs = searchBase.getResourceAsStream(resourceName);
        if (null != streamAsIs) {
            return streamAsIs;
        }
        // Try to prepend this folder's relative path to the resource name
        if ((-1 == resourceName.indexOf('/')) && (-1 == resourceName.indexOf('\\'))) {
            final String relativeCandidate = relativePath + File.separatorChar + resourceName;
            final InputStream relativeStream = searchBase.getResourceAsStream(resourceName);
            if (null != relativeStream) {
                return relativeStream;
            }
        }
        //
        return null;
    }

}
