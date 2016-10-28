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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public enum BuiltInItemSupport implements ArraySequenceParser, ArrayItemFiller {

    BIG_INTEGER(BigInteger.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                while (scanner.hasNext()) {
                    final BigInteger num = scanner.nextBigInteger();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse big integer values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final BigInteger value = new BigInteger(itemSpec);
            Array.set(target, index, value);
        }
    },

    BIG_DECIMAL(BigDecimal.class) {
        @Override
        public List<String> parseItems(String sourceText, int initialOffset, int expectedItemCount) {
            final List<String> result = new ArrayList<String>(expectedItemCount);
            try {
                final Scanner scanner = new Scanner(sourceText.substring(initialOffset));
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    final BigDecimal num = scanner.nextBigDecimal();
                    result.add(String.valueOf(num));
                }
            } catch (InputMismatchException e) {
                throw new IllegalArgumentException("Invalid array specification, cannot parse big decimal values: " + sourceText, e);
            }
            return result;
        }

        @Override
        public void setItem(Object target, int index, String itemSpec) {
            final BigDecimal value = new BigDecimal(itemSpec);
            Array.set(target, index, value);
        }
    };

    private final Class<?> itemType;

    BuiltInItemSupport(Class<?> itemType) {
        this.itemType = itemType;
    }

    @Override
    public Class<?> getItemType() {
        return itemType;
    }

    public static BuiltInItemSupport forType(Class<?> itemType) {
        for (final BuiltInItemSupport typeHandler : values()) {
            if (itemType == typeHandler.itemType) {
                return typeHandler;
            }
        }
        return null;
    }

}
