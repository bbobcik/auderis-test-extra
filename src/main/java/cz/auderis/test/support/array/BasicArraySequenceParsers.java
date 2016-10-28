/*
 * Copyright 2016 Boleslav Bobcik - Auderis
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

package cz.auderis.test.support.array;

import java.util.ArrayList;
import java.util.List;

public enum BasicArraySequenceParsers implements ArraySequenceParser {

    TOKENS {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            final StringBuilder str = new StringBuilder(sourceText.length());
            int offset = initialOffset;
            boolean skippingMandatoryWhitespace = false;
            boolean skippingOptionalWhitespace = true;
            char currentQuote = 0;
            boolean escapeActive = false;
            STRING_SCAN:
            while (offset < sourceText.length()) {
                final char c = sourceText.charAt(offset);
                final boolean isWhitespace = Character.isWhitespace(c);
                final boolean isQuote = (('\'' == c) || ('"' == c));
                if (skippingMandatoryWhitespace) {
                    assert 0 == str.length();
                    if (!isWhitespace) {
                        throw new IllegalArgumentException("Invalid array specification, missing item separator: " + sourceText);
                    }
                    skippingOptionalWhitespace = true;
                    skippingMandatoryWhitespace = false;
                } else if (skippingOptionalWhitespace) {
                    assert 0 == str.length();
                    if (!isWhitespace) {
                        // Initial non-whitespace character
                        skippingOptionalWhitespace = false;
                        if (isQuote) {
                            currentQuote = c;
                        } else {
                            str.append(c);
                        }
                    } else {
                        // Whitespace skipped
                    }
                } else if (0 == currentQuote) {
                    // Non-quoted mode
                    if (isWhitespace) {
                        // Token terminated
                        result.add(str.toString());
                        str.setLength(0);
                        skippingOptionalWhitespace = true;
                    } else if ('\\' == c) {
                        throw new IllegalArgumentException("Invalid array specification, escape not allowed in unquoted token: " + sourceText);
                    } else if (isQuote) {
                        throw new IllegalArgumentException("Invalid array specification, quote not allowed in unquoted token: " + sourceText);
                    } else {
                        str.append(c);
                    }
                } else if (escapeActive) {
                    if ((currentQuote != c) && ('\\' != c)) {
                        str.append('\\');
                    }
                    str.append(c);
                    escapeActive = false;
                } else if ('\\' == c) {
                    escapeActive = true;
                } else if (c == currentQuote) {
                    // Token terminated
                    result.add(str.toString());
                    str.setLength(0);
                    currentQuote = 0;
                    skippingMandatoryWhitespace = true;
                } else {
                    str.append(c);
                }
                ++offset;
            }
            assert (skippingMandatoryWhitespace || skippingOptionalWhitespace) == (0 == str.length());
            if (str.length() > 0) {
                if (escapeActive) {
                    throw new IllegalArgumentException("Invalid array specification, unfinished escape sequence: " + sourceText);
                } else if (0 != currentQuote) {
                    throw new IllegalArgumentException("Invalid array specification, missing terminating quote " + currentQuote + ": " + sourceText);
                } else {
                    result.add(str.toString());
                }
            }
            return result;
        }
    }

}
