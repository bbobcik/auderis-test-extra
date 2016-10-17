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

enum CommonCharPattern implements SimpleCharPatternMatcher.CharSequencePattern {

    JAVA_IDENTIFIER {
        @Override
        public boolean isValidStartChar(char c) {
            return Character.isJavaIdentifierStart(c);
        }

        @Override
        public boolean isValidChar(char c) {
            return Character.isJavaIdentifierPart(c);
        }

        @Override
        public String toString() {
            return "Java identifier";
        }
    },

    XML_NAME {
        @Override
        public boolean isValidStartChar(char c) {
            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))) {
                return true;
            } else if ((':' == c) || ('_' == c)) {
                return true;
            } else if ((c >= '\u00C0' &&  c <= '\u00D6') || (c >= '\u00D8' &&  c <= '\u00F6') || (c >= '\u00F8' &&  c <= '\u02FF')) {
                return true;
            } else if ((c >= '\u0370' && c <= '\u037D') || (c >= '\u037F' && c <= '\u1FFF') || (c >= '\u200C' && c <= '\u200D')) {
                return true;
            } else if ((c >= '\u2070' && c <= '\u218F') || (c >= '\u2C00' && c <= '\u2FEF') || (c >= '\u3001' && c <= '\uD7FF')) {
                return true;
            } else if ((c >= '\uF900' && c <= '\uFDCF') || (c >= '\uFDF0' && c <= '\uFFFD')) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isValidChar(char c) {
            // | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
            if (('-' == c) || ('.' == c) || ('\u00B7' == c)) {
                return true;
            } else if ((c >= '0' && c <= '9') || (c >= '\u0300' && c <= '\u036F') || (c >= '\u203F' && c <= '\u2040')) {
                return true;
            }
            return isValidStartChar(c);
        }

        @Override
        public String toString() {
            return "XML name";
        }
    },

    DECIMAL_DIGITS {
        @Override
        public boolean isValidStartChar(char c) {
            if (('-' == c) || ('+' == c)) {
                return true;
            }
            return isValidChar(c);
        }

        @Override
        public boolean isValidChar(char c) {
            return (c >= '0' && c <= '9');
        }

        @Override
        public String toString() {
            return "sequence of decimal digits";
        }
    },

    HEXADECIMAL_DIGITS {
        @Override
        public boolean isValidStartChar(char c) {
            return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
        }

        @Override
        public boolean isValidChar(char c) {
            return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
        }

        @Override
        public String toString() {
            return "sequence of hexadecimal digits";
        }
    },

    BINARY_DIGITS {
        @Override
        public boolean isValidStartChar(char c) {
            return (('0' == c) || ('1' == c));
        }

        @Override
        public boolean isValidChar(char c) {
            return (('0' == c) || ('1' == c));
        }

        @Override
        public String toString() {
            return "sequence of binary digits";
        }
    },


}
