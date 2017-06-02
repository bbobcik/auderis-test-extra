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

import cz.auderis.test.category.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class BasicWorkFolderTest {

    @Rule
    public WorkFolder folder = WorkFolder.basic();

    @Test
    @Category(UnitTest.class)
    public void shouldCreateWorkFolder() throws Exception {
        assertThat(folder, is(notNullValue()));
        final File workRoot = folder.getRoot();
        assertThat(workRoot, is(notNullValue()));
        assertThat("Root exists", workRoot.exists(), is(true));
        assertThat("Root is directory", workRoot.isDirectory(), is(true));
        assertThat("Root is readable", workRoot.canRead(), is(true));
        assertThat("Root is writable", workRoot.canWrite(), is(true));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeRelativeResource() throws Exception {
        final File f = folder.newResourceCopy("resourceX.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("resourceX.txt"));
        assertThat(f.getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeRelativeResourceUnderDifferentName() throws Exception {
        final File f = folder.newResourceCopy("diff.name.bin", "resourceX.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("diff.name.bin"));
        assertThat(f.getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeRelativeResourceIntoSubfolder() throws Exception {
        final File f = folder.newResourceCopy("sub1/sub2/res.obj", "resourceX.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("res.obj"));
        assertThat(f.getParentFile().getName(), is("sub2"));
        assertThat(f.getParentFile().getParentFile().getName(), is("sub1"));
        assertThat(f.getParentFile().getParentFile().getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeAbsoluteResource() throws Exception {
        final File f = folder.newResourceCopy("/rootResource.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("rootResource.txt"));
        assertThat(f.getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeAbsoluteResourceUnderDifferentName() throws Exception {
        final File f = folder.newResourceCopy("x.bin", "/rootResource.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("x.bin"));
        assertThat(f.getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeAbsoluteResourceIntoSubfolder() throws Exception {
        final File f = folder.newResourceCopy("sub_A/sub_B/ABC.obj", "/rootResource.txt");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.length(), is(greaterThan(2L)));
        assertThat(f.getName(), is("ABC.obj"));
        assertThat(f.getParentFile().getName(), is("sub_B"));
        assertThat(f.getParentFile().getParentFile().getName(), is("sub_A"));
        assertThat(f.getParentFile().getParentFile().getParentFile(), is(folder.getRoot()));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeFileWithString() throws Exception {
        final File f = folder.newFile("file.txt", "Hello world");
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.getName(), is("file.txt"));
        assertThat(f.length(), is(greaterThan(2L)));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeFileWithLines() throws Exception {
        final File f = folder.newFile("a/b/file.txt", ContentProviders.lines(
                "Line 1",
                "Line 2",
                "Line 3"
        ));
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.getName(), is("file.txt"));
        assertThat(f.length(), is(greaterThan(2L)));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldInitializeFileWithSerializedObject() throws Exception {
        // Given
        final Map<String, Integer> numMap = new HashMap<>(3);
        numMap.put("One", 1);
        numMap.put("Two", 2);
        numMap.put("Three", 3);
        // When
        final File f = folder.newFile("x/y/file.bin", ContentProviders.serializedForm(numMap));
        // Then
        assertThat(f, is(notNullValue()));
        assertThat(f.isFile(), is(true));
        assertThat(f.getName(), is("file.bin"));
        Object loadedObject;
        try (
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            loadedObject = ois.readObject();
        }
        assertThat(loadedObject, instanceOf(Map.class));
        Map<?, ?> loadedMap = (Map<?, ?>) loadedObject;
        assertThat(loadedMap, is(numMap));
    }

    @Test
    @Category(UnitTest.class)
    public void shouldCreateClassLoaderAttachedToTestFolder() throws Exception {
        // Given
        folder.newFile("META-INF/test-contents.lst", "EMPTY");
        // When
        final ClassLoader loader = folder.getClassLoaderForResources();
        final InputStream resourceStream = loader.getResourceAsStream("META-INF/test-contents.lst");
        // Then
        assertThat(resourceStream, is(notNullValue()));
        resourceStream.close();
    }

}
