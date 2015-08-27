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
package cz.auderis.test.matcher.text;

import org.hamcrest.Matcher;

public final class TextMatchers {

	public static <T extends CharSequence> Matcher<T> withPrefix(String prefix) {
		return new PrefixMatcher<T>(prefix, true);
	}

	public static <T extends CharSequence> Matcher<T> withCaseInsensitivePrefix(String prefix) {
		return new PrefixMatcher<T>(prefix, false);
	}

	public static <T extends CharSequence> Matcher<T> withSuffix(String suffix) {
		return new SuffixMatcher<T>(suffix, true);
	}

	public static <T extends CharSequence> Matcher<T> withCaseInsensitiveSuffix(String suffix) {
		return new SuffixMatcher<T>(suffix, false);
	}

	private TextMatchers() {
		throw new AssertionError();
	}

}
