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

package cz.auderis.test.matcher.array;

import cz.auderis.test.support.array.MultiArraySupport;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayDimensionMatcher extends BaseMatcher<Object> {

    private final List<Matcher<? super Integer>> dimensionMatchers;
    private String                               fixedDescription;

    public ArrayDimensionMatcher(List<Matcher<? super Integer>> dimensionMatchers) {
        if (null == dimensionMatchers) {
            throw new NullPointerException();
        } else if (dimensionMatchers.isEmpty()) {
            throw new IllegalArgumentException("no matchers for dimensions provided");
        }
        int dim = 0;
        for (Matcher<? super Integer> matcher : dimensionMatchers) {
            if (null == matcher) {
                throw new IllegalArgumentException("matcher for dimension " + dim + " is undefined");
            }
            ++dim;
        }
        this.dimensionMatchers = new ArrayList<Matcher<? super Integer>>(dimensionMatchers);
    }

    public void setDescription(String description) {
        this.fixedDescription = description;
    }

    @Override
    public boolean matches(Object item) {
        return matchesInternal(item, dimensionMatchers);
    }

    private boolean matchesInternal(Object arrayObj, List<Matcher<? super Integer>> matchers) {
        final boolean objNotArray = ((null == arrayObj) || !arrayObj.getClass().isArray());
        if (objNotArray || matchers.isEmpty()) {
            return objNotArray == matchers.isEmpty();
        }
        final Matcher<? super Integer> dimensionMatcher = matchers.get(0);
        final int dimensionValue = getArrayLength(arrayObj);
        if (!dimensionMatcher.matches(dimensionValue)) {
            return false;
        }
        final List<Matcher<? super Integer>> remainingMatchers = matchers.subList(1, matchers.size());
        final Class<?> itemType = arrayObj.getClass().getComponentType();
        if (!Object.class.isAssignableFrom(itemType)) {
            // Items are instances of a primitive type
            return remainingMatchers.isEmpty();
        }
        for (final Object arrayItem : (Object[]) arrayObj) {
            if (!matchesInternal(arrayItem, remainingMatchers)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        if (null != fixedDescription) {
            description.appendText(fixedDescription);
        } else {
            description.appendText("array");
            String separator = " where";
            int dimensionIndex = 1;
            for (final Matcher<? super Integer> matcher : dimensionMatchers) {
                description.appendText(separator);
                description.appendText(" dimension[" + dimensionIndex + "] ");
                matcher.describeTo(description);
                ++dimensionIndex;
                if (dimensionIndex == dimensionMatchers.size()) {
                    separator = " and";
                } else {
                    separator = ", ";
                }
            }
        }
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        final List<Integer> effectiveDimensions = computeEffectiveDimensions(item);
        if (effectiveDimensions.isEmpty()) {
            description.appendText("object is not an array");
            return;
        }
        final StringBuilder effStr = new StringBuilder(effectiveDimensions.size() * 4);
        String separator = null;
        boolean divergenceDetected = false;
        for (final Integer dim : effectiveDimensions) {
            if (null == separator) {
                separator = " x ";
            } else {
                effStr.append(separator);
            }
            effStr.append(dim);
            if (dim < 0) {
                divergenceDetected = true;
            }
        }
        if (divergenceDetected) {
            description.appendText("array does not have consistent dimensions");
        } else if (null != fixedDescription) {
            description.appendText("was ");
            description.appendText(String.valueOf(effectiveDimensions.size()));
            description.appendText("-dimensional array ");
            description.appendText(effStr.toString());
        } else if (effectiveDimensions.size() != dimensionMatchers.size()) {
            description.appendText("array ");
            description.appendText(effStr.toString());
            description.appendText(" had " + effectiveDimensions.size() + " dimensions");
            description.appendText(" instead of " + dimensionMatchers.size());
        } else {
            for (int dimIdx=0; dimIdx<effectiveDimensions.size(); ++dimIdx) {
                final Integer dimension = effectiveDimensions.get(dimIdx);
                final Matcher<? super Integer> matcher = dimensionMatchers.get(dimIdx);
                if (!matcher.matches(dimension)) {
                    description.appendText("was array ");
                    description.appendText(effStr.toString());
                    description.appendText(" where dimension[" + (1 + dimIdx) + "] ");
                    matcher.describeMismatch(dimension, description);
                    break;
                }
            }
        }
    }

    private static List<Integer> computeEffectiveDimensions(Object arrayObj) {
        if ((null == arrayObj) || !arrayObj.getClass().isArray()) {
            return Collections.emptyList();
        }
        final int currentDimension = getArrayLength(arrayObj);
        final Class<?> itemType = arrayObj.getClass().getComponentType();
        if (!itemType.isArray()) {
            return Collections.singletonList(currentDimension);
        }
        List<Integer> subdim = null;
        for (Object item : (Object[]) arrayObj) {
            final List<Integer> itemDimensions;
            if (null == item) {
                itemDimensions = Collections.emptyList();
            } else {
                itemDimensions = computeEffectiveDimensions(item);
            }
            if (null == subdim) {
                subdim = itemDimensions;
            } else if (!subdim.equals(itemDimensions)) {
                // Different subdimensions detected
                return Arrays.asList(currentDimension, -1);
            }
        }
        if (null == subdim) {
            subdim = Collections.emptyList();
        }
        final List<Integer> result = new ArrayList<Integer>(1 + subdim.size());
        result.add(currentDimension);
        result.addAll(subdim);
        return result;
    }

    private static int getArrayLength(Object arrayObj) {
        assert null != arrayObj;
        assert arrayObj.getClass().isArray();
        final Class<?> itemType = arrayObj.getClass().getComponentType();
        if (Object.class.isAssignableFrom(itemType)) {
            return ((Object[]) arrayObj).length;
        } else if (int.class == itemType) {
            return ((int[]) arrayObj).length;
        } else if (boolean.class == itemType) {
            return ((boolean[]) arrayObj).length;
        } else if (char.class == itemType) {
            return ((char[]) arrayObj).length;
        } else if (byte.class == itemType) {
            return ((byte[]) arrayObj).length;
        } else if (long.class == itemType) {
            return ((long[]) arrayObj).length;
        } else if (short.class == itemType) {
            return ((short[]) arrayObj).length;
        } else if (double.class == itemType) {
            return ((double[]) arrayObj).length;
        } else if (float.class == itemType) {
            return ((float[]) arrayObj).length;
        }
        throw new AssertionError("Unsupported array type: " + arrayObj.getClass());
    }

    @Factory
    public static Matcher<? super Object> arrayWithDimension(int d) {
        return new ArrayDimensionMatcher(Collections.<Matcher<? super Integer>>singletonList(CoreMatchers.is(d)));
    }

    @Factory
    public static Matcher<? super Object> arrayWithDimensions(int d1, int d2, int... ds) {
        final List<Matcher<? super Integer>> dimMatchers = new ArrayList<Matcher<? super Integer>>(ds.length + 2);
        dimMatchers.add(CoreMatchers.is(d1));
        dimMatchers.add(CoreMatchers.is(d2));
        StringBuilder desc = new StringBuilder(16 + ds.length * 8);
        String separator = " x ";
        desc.append(2 + ds.length);
        desc.append("-dimensional array ").append(d1).append(separator).append(d2);
        for (final int dx : ds) {
            dimMatchers.add(CoreMatchers.is(dx));
            desc.append(separator).append(dx);
        }
        final ArrayDimensionMatcher matcher = new ArrayDimensionMatcher(dimMatchers);
        matcher.setDescription(desc.toString());
        return matcher;
    }

    @Factory
    public static Matcher<? super Object> arrayWithDimensions(String dimensionSpec) {
        final int[] dimensions = MultiArraySupport.parseDimensions(dimensionSpec);
        final List<Matcher<? super Integer>> dimMatchers = new ArrayList<Matcher<? super Integer>>(dimensions.length);
        StringBuilder desc = new StringBuilder(dimensions.length * 8);
        desc.append(dimensions.length);
        desc.append("-dimensional array ");
        String separator = null;
        for (final Integer expectedDimension : dimensions) {
            dimMatchers.add(CoreMatchers.is(expectedDimension));
            if (null == separator) {
                separator = " x ";
            } else {
                desc.append(separator);
            }
            desc.append(expectedDimension);
        }
        final ArrayDimensionMatcher matcher = new ArrayDimensionMatcher(dimMatchers);
        matcher.setDescription(desc.toString());
        return matcher;
    }

}
