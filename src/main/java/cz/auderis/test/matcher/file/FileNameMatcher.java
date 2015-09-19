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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;

public class FileNameMatcher extends TypeSafeMatcher<File> {

	private final String name;

	FileNameMatcher(String expectedName) {
		this.name = expectedName;
	}

	@Override
	protected boolean matchesSafely(File testedFile) {
		final String testedName = testedFile.getName();
		return testedName.equals(name);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("named ").appendValue(name);
	}

	@Override
	protected void describeMismatchSafely(File testedFile, Description desc) {
		desc.appendValue(testedFile.getName());
	}

}
