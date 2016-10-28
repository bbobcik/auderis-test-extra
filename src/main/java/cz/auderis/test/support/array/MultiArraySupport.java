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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class MultiArraySupport {

    private static final Pattern DIMENSION_SPEC_SEPARATOR = Pattern.compile("\\s*[*xX]\\s*");

    public static ParserResult parseArraySpec(String arraySpec, ArraySequenceParser itemParser) {
        if ((null == arraySpec) || arraySpec.trim().isEmpty()) {
            return null;
        }
        final int endOfDimensionPart = arraySpec.indexOf(':');
        if (endOfDimensionPart < 1) {
            throw new IllegalArgumentException("Invalid array specification: " + arraySpec);
        }
        final String dimSpec = arraySpec.substring(0, endOfDimensionPart);
        final int[] dimensions = parseDimensions(dimSpec);
        int product = 1;
        for (final Integer dim : dimensions) {
            product *= dim;
        }
        final List<String> items;
        if (endOfDimensionPart < arraySpec.length() - 1) {
            items = itemParser.parseItems(arraySpec, endOfDimensionPart + 1, product);
        } else {
            items = Collections.emptyList();
        }
        if (product != items.size()) {
            throw new IllegalArgumentException("Invalid array specification, expected " + product + " items: " + arraySpec);
        }
        return new ParserResult(dimensions, new LinkedList<String>(items));
    }

    public static int[] parseDimensions(String dimSpec) {
        final List<Integer> dimensions = new ArrayList<Integer>(4);
        final Scanner scanner = new Scanner(dimSpec.trim());
        scanner.useDelimiter(DIMENSION_SPEC_SEPARATOR);
        while (scanner.hasNextInt()) {
            final int dim = scanner.nextInt();
            if (dim < 0) {
                throw new IllegalArgumentException("Invalid array specification, negative dimension: " + dimSpec);
            }
            dimensions.add(dim);
        }
        if (dimensions.isEmpty()) {
            throw new IllegalArgumentException("Invalid array specification, cannot parseArraySpec dimensions: " + dimSpec);
        }
        final int[] dimArray = new int[dimensions.size()];
        int i = 0;
        for (final Integer dimension : dimensions) {
            dimArray[i] = dimension;
            ++i;
        }
        return dimArray;
    }

    private MultiArraySupport() {
        throw new AssertionError();
    }

    public static final class ParserResult {
        public final int[] dimensions;
        public final Queue<String> items;

        private ParserResult(int[] dimensions, Queue<String> items) {
            this.dimensions = dimensions;
            this.items = items;
        }
    }

}
