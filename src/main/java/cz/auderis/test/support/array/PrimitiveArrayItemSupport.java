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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public enum PrimitiveArrayItemSupport implements ArraySequenceParser, ArrayItemFiller {

    BOOLEAN(boolean.class, Boolean.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
            while (scanner.hasNext()) {
                final String token = scanner.next();
                final boolean value;
                if (BOOLEAN_TRUE_VALUES.contains(token)) {
                    value = true;
                } else if (BOOLEAN_FALSE_VALUES.contains(token)) {
                    value = false;
                } else if ("true".equalsIgnoreCase(token) || "yes".equalsIgnoreCase(token)) {
                    value = true;
                } else if ("false".equalsIgnoreCase(token) || "no".equalsIgnoreCase(token)) {
                    value = false;
                } else {
                    throw new IllegalArgumentException("Invalid array specification, bad boolean value '" + token + "': " + sourceText);
                }
                result.add(String.valueOf(value));
            }
            return result;

        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final boolean value = Boolean.parseBoolean(itemSpec);
            Array.setBoolean(target, index, value);
        }
    },

    BYTE(byte.class, Byte.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                while (scanner.hasNext()) {
                    final byte num = scanner.nextByte();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse byte values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final byte value = Byte.parseByte(itemSpec);
            Array.setByte(target, index, value);
        }
    },

    CHAR(char.class, Character.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> tokens = BasicArraySequenceParsers.TOKENS.parseItems(sourceText, initialOffset, expectedItemCount);
            int totalSize = 0;
            for (final String token : tokens) {
                totalSize += token.length();
            }
            final List<String> result = new ArrayList<String>(totalSize);
            for (final String token : tokens) {
                final char[] tokenChars = token.toCharArray();
                for (final char tokenChar : tokenChars) {
                    result.add(String.valueOf(tokenChar));
                }
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final char value = itemSpec.charAt(0);
            Array.setChar(target, index, value);
        }
    },

    SHORT(short.class, Short.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                while (scanner.hasNext()) {
                    final short num = scanner.nextShort();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse short values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final short value = Short.parseShort(itemSpec);
            Array.setShort(target, index, value);
        }
    },

    INT(int.class, Integer.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                while (scanner.hasNext()) {
                    final int num = scanner.nextInt();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse integer values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final int value = Integer.parseInt(itemSpec);
            Array.setInt(target, index, value);
        }
    },

    LONG(long.class, Long.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                while (scanner.hasNext()) {
                    final long num = scanner.nextLong();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse long integer values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final long value = Long.parseLong(itemSpec);
            Array.setLong(target, index, value);
        }
    },

    FLOAT(float.class, Float.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    final float num = scanner.nextFloat();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse float values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final float value = Float.parseFloat(itemSpec);
            Array.setFloat(target, index, value);
        }
    },

    DOUBLE(double.class, Double.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    final double num = scanner.nextDouble();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse double values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final double value = Double.parseDouble(itemSpec);
            Array.setDouble(target, index, value);
        }
    };

    private final Class<?> itemType;
    private final Class<?> boxedType;

    PrimitiveArrayItemSupport(Class<?> itemType, Class<?> boxedType) {
        this.itemType = itemType;
        this.boxedType = boxedType;
    }

    @Override
    public Class<?> getItemType() {
        return itemType;
    }

    public static PrimitiveArrayItemSupport forType(Class<?> type) {
        for (final PrimitiveArrayItemSupport filler : values()) {
            if (type == filler.itemType) {
                return filler;
            }
        }
        return null;
    }

    public static PrimitiveArrayItemSupport forBoxedType(Class<?> type) {
        for (final PrimitiveArrayItemSupport filler : values()) {
            if (type == filler.boxedType) {
                return filler;
            }
        }
        return null;
    }

    final static Set<String> BOOLEAN_TRUE_VALUES  = new HashSet<String>(Arrays.asList("1", "Y", "y", "T", "t"));
    final static Set<String> BOOLEAN_FALSE_VALUES = new HashSet<String>(Arrays.asList("0", "N", "n", "F", "f"));
}
