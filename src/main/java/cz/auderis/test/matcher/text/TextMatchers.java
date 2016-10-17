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

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

public final class TextMatchers {

    @Factory
    public static <T> Matcher<CharSequence> matchingPattern(Pattern p) {
        assert null != p;
        return new TextPatternMatcher(p);
    }

    @Factory
    public static <T> Matcher<CharSequence> matchingPattern(String textPattern) {
        assert null != textPattern;
        final Pattern p = Pattern.compile(textPattern);
        return new TextPatternMatcher(p);
    }

    @Factory
	public static <T extends CharSequence> Matcher<T> withPrefix(String prefix) {
		return new PrefixMatcher<T>(prefix, true);
	}

    @Factory
	public static <T extends CharSequence> Matcher<T> withCaseInsensitivePrefix(String prefix) {
		return new PrefixMatcher<T>(prefix, false);
	}

    @Factory
	public static <T extends CharSequence> Matcher<T> withSuffix(String suffix) {
		return new SuffixMatcher<T>(suffix, true);
	}

    @Factory
	public static <T extends CharSequence> Matcher<T> withCaseInsensitiveSuffix(String suffix) {
		return new SuffixMatcher<T>(suffix, false);
	}

    @Factory
	public static <T> Matcher<CharSequence> validJavaIdentifier() {
		return new SimpleCharPatternMatcher(CommonCharPattern.JAVA_IDENTIFIER);
	}

    @Factory
    public static <T> Matcher<CharSequence> validXmlName() {
        return new SimpleCharPatternMatcher(CommonCharPattern.XML_NAME);
    }

    @Factory
    public static <T> Matcher<CharSequence> validDecimalNumber() {
        return new SimpleCharPatternMatcher(CommonCharPattern.DECIMAL_DIGITS);
    }

    @Factory
    public static <T> Matcher<CharSequence> validHexadecimalNumber() {
        return new SimpleCharPatternMatcher(CommonCharPattern.HEXADECIMAL_DIGITS);
    }

    @Factory
    public static <T> Matcher<CharSequence> validBinaryNumber() {
        return new SimpleCharPatternMatcher(CommonCharPattern.BINARY_DIGITS);
    }

    private TextMatchers() {
		throw new AssertionError();
	}

}
