package cz.auderis.test.rule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Defines operations that can be performed on any instance of work folder
 *
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public interface WorkFolderInterface {

    File asFile();

    String absolutePath();

    String relativePath();

    WorkFolderInterface getRootFolder();

    File newFile(String targetPath) throws IOException;

    File newFile(String targetPath, CharSequence initialContents) throws IOException;

    File newFile(String targetPath, InputStream initialContents) throws IOException;

    File newFile(String targetPath, InitialContentsProvider contentsProvider) throws IOException;

    File newResourceCopy(String targetPath, String resourceName) throws IOException;

    File newResourceCopy(String resourceName) throws IOException;

    WorkFolderInterface subfolder(String... pathComponents) throws IOException;

    void clean() throws IOException;

}
