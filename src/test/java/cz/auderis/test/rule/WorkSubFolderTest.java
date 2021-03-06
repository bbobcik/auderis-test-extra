package cz.auderis.test.rule;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static cz.auderis.test.matcher.file.FileMatchers.isDirectory;
import static cz.auderis.test.matcher.file.FileMatchers.isFile;
import static cz.auderis.test.matcher.text.TextMatchers.matchingPattern;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class WorkSubFolderTest {

    @Rule
    public WorkFolder folder = WorkFolder.basic();

    @Test
    public void shouldCreateSingleSubfolder() throws Exception {
        // Given
        final File rootDir = folder.getRoot();
        String relPath = "subdir";
        // When
        final WorkFolderInterface subfolder = folder.subfolder(relPath);
        // Then
        assertThat(subfolder.asFile(), isDirectory());
        assertThat(subfolder.relativePath(), is(relPath));
    }

    @Test
    public void shouldCreateMultipleSubfolders() throws Exception {
        // Given
        final File rootDir = folder.getRoot();
        // When
        final WorkFolderInterface subfolder = folder.subfolder("a1", "b2", "c3");
        // Then
        assertThat(subfolder.asFile(), isDirectory());
        assertThat(subfolder.relativePath(), is("a1/b2/c3"));
    }

    @Test
    public void shouldCreateComplexSubfolders() throws Exception {
        // Given
        final File rootDir = folder.getRoot();
        // When
        final WorkFolderInterface subfolder1 = folder.subfolder("a1/b2");
        final WorkFolderInterface subfolder2 = folder.subfolder("a1", "b2/c3");
        final WorkFolderInterface subfolder3 = folder.subfolder("r9/s8/t7", "u6/v5/w4");
        // Then
        assertThat(subfolder1.asFile(), isDirectory());
        assertThat(subfolder2.asFile(), isDirectory());
        assertThat(subfolder3.asFile(), isDirectory());
        assertThat(subfolder1.relativePath(), is("a1/b2"));
        assertThat(subfolder2.relativePath(), is("a1/b2/c3"));
        assertThat(subfolder3.relativePath(), is("r9/s8/t7/u6/v5/w4"));
    }

    @Test
    public void shouldCreateFileInSubfolder() throws Exception {
        // When
        final WorkFolderInterface subfolder1 = folder.subfolder("a1/b2");
        final File file1 = subfolder1.newFile("file1.txt");
        final WorkFolderInterface subfolder2 = subfolder1.subfolder("c3");
        final File file2 = subfolder2.newFile("file2.txt");
        // Then
        assertThat(file1, isFile());
        assertThat(file2, isFile());
        assertThat(file1.getAbsolutePath(), matchingPattern("a1[/\\\\]b2[/\\\\]file1.txt$"));
        assertThat(file2.getAbsolutePath(), matchingPattern("a1[/\\\\]b2[/\\\\]c3[/\\\\]file2.txt$"));
    }
}
