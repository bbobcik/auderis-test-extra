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

package cz.auderis.test.matcher.file;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

public final class FileMatchers {

    @Factory
    public static Matcher<? super File> hasFlags(FileFlags... flags) {
        final Set<FileFlags> requiredFlags;
        if (0 == flags.length) {
            requiredFlags = EnumSet.noneOf(FileFlags.class);
        } else {
            requiredFlags = EnumSet.of(flags[0], flags);
        }
        return new FileFlagMatcher(requiredFlags, null);
    }

    @Factory
    public static Matcher<? super File> isDirectory() {
        return new FileFlagMatcher(EnumSet.of(FileFlags.DIRECTORY), null);
    }

    @Factory
    public static Matcher<? super File> isFile() {
        return new FileFlagMatcher(EnumSet.of(FileFlags.NORMAL_FILE), null);
    }


    private FileMatchers() {
        throw new AssertionError();
    }
}
