/*
 * Copyright 2015-2016 Boleslav Bobcik - Auderis
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

public class SimpleCharPatternMatcher extends TypeSafeMatcher<CharSequence> {

    public interface CharSequencePattern {
        boolean isValidStartChar(char c);
        boolean isValidChar(char c);
    }

    private final CharSequencePattern pattern;

    public SimpleCharPatternMatcher(CharSequencePattern pattern) {
        super(CharSequence.class);
        assert null != pattern;
        this.pattern = pattern;
    }

    @Override
    protected boolean matchesSafely(CharSequence seq) {
        if (seq.length() < 1) {
            return false;
        } else if (pattern.isValidStartChar(seq.charAt(0))) {
            return false;
        }
        for (int i=1; i<seq.length(); ++i) {
            final char c = seq.charAt(i);
            if (!pattern.isValidChar(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(pattern.toString());
    }

    @Override
    protected void describeMismatchSafely(CharSequence seq, Description out) {
        if (seq.length() < 1) {
            out.appendText("was empty");
        } else {
            int badStart = -1;
            if (!pattern.isValidStartChar(seq.charAt(0))) {
                badStart = 0;
            } else {
                for (int i = 1; i < seq.length(); ++i) {
                    final char c = seq.charAt(i);
                    if (!pattern.isValidChar(c)) {
                        badStart = i;
                        break;
                    }
                }
            }
            assert badStart >= 0;
            int badEnd = -1;
            for (int i = badStart + 1; i < seq.length(); ++i) {
                final char c = seq.charAt(i);
                if (pattern.isValidChar(c)) {
                    badEnd = i;
                    break;
                }
            }
            assert badEnd >= 1;
            out.appendValue(seq);
            if (0 == badStart) {
                out.appendText(" has invalid prefix ");
            } else {
                out.appendText(" contains invalid part ");
            }
            out.appendValue(seq.subSequence(badStart, badEnd));
        }
    }

}
