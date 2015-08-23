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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashSet;
import java.util.Set;

class SuffixMatcher<T extends CharSequence> extends TypeSafeMatcher<T> {

	private static final char[][] BRACKET_CANDIDATES = { { '<', '>' }, { '[', ']' }, { '\u27EA', '\u27EB' } };

	private final String suffix;
	private final int suffixLength;
	private final boolean caseSensitive;

	SuffixMatcher(String suffix, boolean caseSensitive) {
		super(CharSequence.class);
		if (null == suffix) {
			throw new NullPointerException();
		}
		this.suffix = suffix;
		this.suffixLength = suffix.length();
		this.caseSensitive = caseSensitive;
	}

	@Override
	protected boolean matchesSafely(CharSequence testedText) {
		final int matchLength = commonSuffixLength(testedText);
		return (suffixLength == matchLength);
	}

	private int commonSuffixLength(CharSequence testedText) {
		final int textLen = testedText.length();
		final int len;
		final int resultMultiplier;
		if (textLen >= suffixLength) {
			len = suffixLength;
			resultMultiplier = 1;
		} else {
			len = textLen;
			resultMultiplier = -1;
		}
		for (int i=0; i<len; ++i) {
			final char testedChar = testedText.charAt(textLen - 1 - i);
			final char suffixChar = suffix.charAt(suffixLength - 1 - i);
			if (testedChar != suffixChar) {
				if (caseSensitive) {
					return resultMultiplier * i;
				}
				final char testedCharUC = Character.toUpperCase(testedChar);
				final char suffixCharUC = Character.toUpperCase(suffixChar);
				if (testedCharUC != suffixCharUC) {
					final char testedCharLC = Character.toLowerCase(testedChar);
					final char suffixCharLC = Character.toLowerCase(suffixChar);
					if (testedCharLC != suffixCharLC) {
						return resultMultiplier * i;
					}
				}
			}
		}
		return resultMultiplier * len;
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendText("ending with ");
		if (!caseSensitive && (suffixLength > 0)) {
			desc.appendText("(case insensitive) ");
		}
		desc.appendValue(suffix);
	}

	@Override
	protected void describeMismatchSafely(CharSequence testedText, Description desc) {
		desc.appendText("was ");
		final int matchLength = commonSuffixLength(testedText);
		assert matchLength < suffixLength;
		final int textLength = testedText.length();
		if (0 == matchLength) {
			// No match at all
			desc.appendValue(testedText);
		} else if (-matchLength == textLength) {
			// Whole tested text matched, but it was shorter than suffix
			assert textLength < suffixLength;
			desc.appendValue(testedText);
			desc.appendText(", short by ").appendText(Integer.toString(suffixLength - textLength)).appendText(" characters");
		} else if (matchLength > 0) {
			// Part of suffix matched, highlight mismatching part
			final String[] bracket = selectBracket(testedText);
			final StringBuilder str = new StringBuilder(textLength + bracket[0].length() + bracket[1].length());
			for (int i=0; i<textLength; ++i) {
				if (i == textLength - suffixLength) {
					str.append(bracket[0]);
				} else if (i == textLength - matchLength) {
					str.append(bracket[1]);
				}
				str.append(testedText.charAt(i));
			}
			desc.appendValue(str.toString());
			desc.appendText(", with last ").appendText(Integer.toString(matchLength)).appendText(" characters matching");
		} else {
			// matchLength between [-textLength+1, -1] means the text is shorter than the expected suffix
			final String[] bracket = selectBracket(testedText);
			final StringBuilder str = new StringBuilder(textLength + bracket[0].length() + bracket[1].length());
			str.append(bracket[0]);
			for (int i=0; i<textLength; ++i) {
				if (i == textLength + matchLength) {
					str.append(bracket[1]);
				}
				str.append(testedText.charAt(i));
			}
			desc.appendValue(str.toString());
			desc.appendText(", short by ").appendText(Integer.toString(suffixLength - textLength)).appendText(" characters");
			desc.appendText(" and with last ").appendText(Integer.toString(-matchLength)).appendText(" characters matching");
		}
	}

	private static String[] selectBracket(CharSequence testedText) {
		final Set<Character> availableBracketChars = new HashSet<Character>(2 * BRACKET_CANDIDATES.length);
		for (final char[] bracketCandidate : BRACKET_CANDIDATES) {
			availableBracketChars.add(bracketCandidate[0]);
			availableBracketChars.add(bracketCandidate[1]);
		}
		final int length = testedText.length();
		for (int i=0; i<length; ++i) {
			availableBracketChars.remove(testedText.charAt(i));
		}
		final String[] result = new String[2];
		for (final char[] bracketCandidate : BRACKET_CANDIDATES) {
			final char c1 = bracketCandidate[0];
			final char c2 = bracketCandidate[1];
			if (availableBracketChars.contains(c1) && availableBracketChars.contains(c2)) {
				result[0] = Character.toString(c1);
				result[1] = Character.toString(c2);
				return result;
			}
		}
		result[0] = " << ";
		result[1] = " >> ";
		return result;
	}

}
