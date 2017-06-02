/*
 * Copyright 2017 Boleslav Bobcik - Auderis
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
package cz.auderis.test.support;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Versatile utility class that merges chunks of text, surrounding them with optional
 * prefixes and suffixes and putting appropriate separators between entries. The
 * class is intended to be used with Hamcrest matchers, simplifying the task
 * of building a description of more complex matchers and their mismatching values.
 * <p>
 * The general structure of output is following:
 * <blockquote>
 * result ::= [<b>Prefix</b>] Item<sub>1</sub> <b>NS</b> Item<sub>2</sub>
 *            <b>NS</b> ... Item<sub>n-1</sub>
 *            <b>LS</b> Item<sub>n</sub> [<b>Suffix</b>]
 * </blockquote>
 * where <b>Prefix</b> is an optional main description prefix, <b>Suffix</b> is
 * an optional main description suffix, <b>NS</b> is "normal separator" string
 * and <b>LS</b> is "last separator" string. Additionally, each item has
 * the following structure:
 * <blockquote>
 * Item<sub>k</sub> :== [<b>Prefix<sub>k</sub></b>] Value<sub>k</sub> [<b>Suffix<sub>k</sub></b>]
 * </blockquote>
 * This means that each description item may individually have its optional prefix and suffix.
 *
 * <h3>Details</h3>
 *
 * <h4>Joiner items</h4>
 * A joiner item can have one of two forms, described in the following table:
 * <table border="1" summary="Joiner items">
 *   <thead>
 *     <tr>
 *       <th>Item type</th>
 *       <th>Usage</th>
 *       <th>Redundant when</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>plain value {@code V}</td>
 *       <td>description of items</td>
 *       <td>{@code V == null}
 *     </tr>
 *     <tr>
 *       <td>pair of value {@code V} and its associated matcher {@code M}</td>
 *       <td>description of mismatches</td>
 *       <td>{@code M == null || M.matches(V)}</td>
 *   </tbody>
 * </table>
 * <p>
 * Joiner items that are deemed redundant are not sent to output.
 *
 * <h4>Text chunks</h4>
 * Practically all text parts (prefixes, suffixes, separators, description item values etc.)
 * have type {@code java.lang.Object}. Typically they are strings or other implementations of
 * {@code java.lang.CharSequence}, but the general object type allows lazy evaluation. If a
 * text chunk object implements {@code java.util.concurrent.Callable} interface, it is invoked
 * during description rendering and the returned value is re-evaluated (i.e. if a {@code Callable C1}
 * returns a reference to another {@code Callable C2}, there will be two invocations of {@linkplain Callable#call()}:
 * <pre>
 * C2 = C1.call();
 * Result = C2.call();
 * </pre>
 *
 * @author Boleslav Bobcik
 */
public class NaturalDescriptionJoiner implements SelfDescribing {

    Object prefix;
    Object suffix;
    Object normalSeparator;
    Object lastSeparator;
    boolean usePrefixWhenEmpty;
    boolean useSuffixWhenEmpty;
    final List<DescriptionItem> items;

    /**
     * Constructs a description joiner with a prefix, suffix and both normal and last separators.
     *
     * @param prefix prefix placed at the start of output (optional, may be {@code null})
     * @param normalSeparator normal separator ({@code null} is equivalent to an empty string)
     * @param lastSeparator separator placed between last two items ({@code null} is equivalent to an empty string)
     * @param suffix suffix appended to the end of output (optional, may be {@code null})
     */
    public NaturalDescriptionJoiner(Object prefix, Object normalSeparator, Object lastSeparator, Object suffix) {
        this.prefix = prefix;
        this.normalSeparator = (null != normalSeparator) ? normalSeparator : "";
        this.lastSeparator = (null != lastSeparator) ? lastSeparator : "";
        this.suffix = suffix;
        this.items = new LinkedList<>();
    }

    /**
     * Constructs a description joiner with default settings: prefix and suffix are undefined,
     * normal separator is a string consisting of comma+space characters and the last separator
     * is the word "and" surrounded by a single space on both ends.
     * <p>
     * Equivalent: {@code new NaturalDescriptionJoiner(null, ", ", " and ", null)}
     *
     * @see #NaturalDescriptionJoiner(Object, Object, Object, Object)
     */
    public NaturalDescriptionJoiner() {
        this(null, ", ", " and ", null);
    }

    /**
     * Constructs a simple description joiner without prefix and suffix, where identical string is used
     * both as "normal" as well as the "last" separator.
     * <p>
     * Equivalent: {@code new NaturalDescriptionJoiner(null, separator, separator, null)}
     *
     * @param separator string that is placed between items ({@code null} is equivalent to an empty string)
     *
     * @see #NaturalDescriptionJoiner(Object, Object, Object, Object)
     */
    public NaturalDescriptionJoiner(Object separator) {
        this(null, separator, separator, null);
    }

    /**
     * Constructs a simple description joiner without prefix and suffix.
     * <p>
     * Equivalent: {@code new NaturalDescriptionJoiner(null, normalSeparator, lastSeparator, null)}
     *
     * @param normalSeparator normal separator ({@code null} is equivalent to an empty string)
     * @param lastSeparator separator placed between last two items ({@code null} is equivalent to an empty string)
     *
     * @see #NaturalDescriptionJoiner(Object, Object, Object, Object)
     */
    public NaturalDescriptionJoiner(Object normalSeparator, Object lastSeparator) {
        this(null, normalSeparator, lastSeparator, null);
    }

    /**
     * Modifies the main prefix in fluent style.
     *
     * @param prefix new description prefix
     * @return this joiner
     */
    public NaturalDescriptionJoiner withPrefix(Object prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Modifies the main suffix in fluent style.
     *
     * @param suffix new description suffix
     * @return this joiner
     */
    public NaturalDescriptionJoiner withSuffix(Object suffix) {
        this.suffix = suffix;
        return this;
    }

    /**
     * Modifies normal separator (i.e. the separator put between items with the exception of the last two items)
     * in fluent style.
     *
     * @param normalSep new normal separator ({@code null} is equivalent to an empty string)
     * @return this joiner
     */
    public NaturalDescriptionJoiner withNormalSeparator(Object normalSep) {
        this.normalSeparator = (null != normalSep) ? normalSep : "";
        return this;
    }

    /**
     * Modifies last separator (i.e. the separator put between the last two items)
     * in fluent style.
     *
     * @param lastSep new last separator ({@code null} is equivalent to an empty string)
     * @return this joiner
     */
    public NaturalDescriptionJoiner withLastSeparator(Object lastSep) {
        this.lastSeparator = (null != lastSep) ? lastSep : "";
        return this;
    }

    /**
     * Indicates whether a prefix string is prepended even when the main part of the output is empty.
     * By default the flag is {@code false}.
     *
     * @return {@code true} when the prefix is sent to output even when the main output part is empty
     */
    public boolean isUsePrefixWhenEmpty() {
        return usePrefixWhenEmpty;
    }

    /**
     * Modifies the flag that controls whether a prefix is prepended even when the main part of the output
     * is empty.
     *
     * @param usePrefixWhenEmpty new flag value
     */
    public void setUsePrefixWhenEmpty(boolean usePrefixWhenEmpty) {
        this.usePrefixWhenEmpty = usePrefixWhenEmpty;
    }

    /**
     * Using fluent style, sets the flag that controls whether a prefix is prepended
     * even when the main part of the output is empty to {@code true}.
     *
     * @return this joiner
     * @see #setUsePrefixWhenEmpty(boolean)
     */
    public NaturalDescriptionJoiner withPrefixWhenEmpty() {
        this.usePrefixWhenEmpty = true;
        return this;
    }

    /**
     * Using fluent style, resets the flag that controls whether a prefix is prepended
     * even when the main part of the output is empty to {@code false}.
     *
     * @return this joiner
     * @see #setUsePrefixWhenEmpty(boolean)
     */
    public NaturalDescriptionJoiner withoutPrefixWhenEmpty() {
        this.usePrefixWhenEmpty = false;
        return this;
    }

    /**
     * Indicates whether a suffix string is appended even when the main part of the output is empty.
     * By default the flag is {@code false}.
     *
     * @return {@code true} when the suffix is sent to output even when the main output part is empty
     */
    public boolean isUseSuffixWhenEmpty() {
        return useSuffixWhenEmpty;
    }

    /**
     * Modifies the flag that controls whether a suffix is prepended even when the main part of the output
     * is empty.
     *
     * @param useSuffixWhenEmpty new flag value
     */
    public void setUseSuffixWhenEmpty(boolean useSuffixWhenEmpty) {
        this.useSuffixWhenEmpty = useSuffixWhenEmpty;
    }

    /**
     * Using fluent style, sets the flag that controls whether a suffix is appended
     * even when the main part of the output is empty to {@code true}.
     *
     * @return this joiner
     * @see #setUseSuffixWhenEmpty(boolean)
     */
    public NaturalDescriptionJoiner withSuffixWhenEmpty() {
        this.useSuffixWhenEmpty = true;
        return this;
    }

    /**
     * Using fluent style, resets the flag that controls whether a suffix is appended
     * even when the main part of the output is empty to {@code false}.
     *
     * @return this joiner
     * @see #setUseSuffixWhenEmpty(boolean)
     */
    public NaturalDescriptionJoiner withoutSuffixWhenEmpty() {
        this.useSuffixWhenEmpty = false;
        return this;
    }

    /**
     * Indicates whether a list of accumulated items is empty or all items are redundant.
     * (See definition of redundant items in the class JavaDoc.)
     *
     * @return {@code false} when the joiner contains one or more non-redundant items
     */
    public boolean isEmpty() {
        compactItems();
        return items.isEmpty();
    }

    /**
     * Adds a new description item, allowing to specify an optional prefix and suffix. Apart from
     * strings and other character sequences, prefix and suffix can have following types:
     * <table border="1" summary="Supported types for prefix and suffix">
     *   <thead>
     *     <tr>
     *       <th>Type</th>
     *       <th>Processing</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>{@code null}</td>
     *       <td>omitted from output</td>
     *     </tr>
     *     <tr>
     *       <td>{@code CharSequence}</td>
     *       <td>normally appended to output</td>
     *     </tr>
     *     <tr>
     *       <td>{@link SelfDescribing}</td>
     *       <td>appended to output by means of calling its {@link SelfDescribing#describeTo(Description)} method</td>
     *     </tr>
     *     <tr>
     *       <td></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td></td>
     *       <td></td>
     *     </tr>
     *     <tr>
     *       <td></td>
     *       <td></td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * @param valuePrefix optional text chunk that will be prepended immediately before {@code value}
     * @param value value to be appended to output
     * @param valueSuffix optional text chunk that will be appended immediately after {@code value}
     * @return this joiner
     */
    public NaturalDescriptionJoiner add(Object valuePrefix, Object value, Object valueSuffix) {
        if (null != value) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, value, valueSuffix);
            items.add(item);
        }
        return this;
    }

    public NaturalDescriptionJoiner add(Object valuePrefix, Object value) {
        return add(valuePrefix, value, null);
    }

    public NaturalDescriptionJoiner add(Object value) {
        return add(null, value, null);
    }

    public <T> NaturalDescriptionJoiner add(Object valuePrefix, T value, Object valueSuffix, DescriptionProvider<? super T> valueDescriber) {
        if (null != value) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, value, valueSuffix, valueDescriber);
            items.add(item);
        }
        return this;
    }

    public <T> NaturalDescriptionJoiner addMismatch(Object valuePrefix, Matcher<? super T> valueMatcher, T value, Object valueSuffix) {
        if ((null != valueMatcher) && !valueMatcher.matches(value)) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, valueMatcher, value, valueSuffix);
            items.add(item);
        }
        return this;
    }

    public <T> NaturalDescriptionJoiner addMismatch(Object valuePrefix, Matcher<? super T> valueMatcher, T value) {
        return addMismatch(valuePrefix, valueMatcher, value, null);
    }

    public <T> NaturalDescriptionJoiner addMismatch(Matcher<? super T> valueMatcher, T value) {
        return addMismatch(null, valueMatcher, value, null);
    }

    public <T> NaturalDescriptionJoiner addMismatch(Object valuePrefix, Matcher<? super T> valueMatcher, T value, Object valueSuffix, MismatchDescriptionProvider<? super T> mismatchDescriber) {
        if ((null != valueMatcher) && !valueMatcher.matches(value)) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, valueMatcher, value, valueSuffix, mismatchDescriber);
            items.add(item);
        }
        return this;
    }

    /**
     * Appends description of accumulated items to the provided {@link Description}. When this argument
     * is {@code null}, no operation is performed.
     *
     * @param desc {@code Description} instance
     */
    @Override
    public void describeTo(Description desc) {
        if (null == desc) {
            return;
        }
        compactItems();
        final int itemCount = items.size();
        if (((0 == itemCount) && !usePrefixWhenEmpty && !useSuffixWhenEmpty)) {
            return;
        }
        if ((0 != itemCount) || usePrefixWhenEmpty) {
            smartAppend(prefix, desc);
        }
        int idx = itemCount - 1;
        for (DescriptionItem item : items) {
            appendItemSurroundingTextToDescription(item, item.valuePrefix, desc);
            if (null != item.matcher) {
                item.describeMismatch(desc);
            } else {
                item.describeValue(desc);
            }
            appendItemSurroundingTextToDescription(item, item.valueSuffix, desc);
            if (1 == idx) {
                smartAppend(lastSeparator, desc);
            } else if (idx > 1) {
                smartAppend(normalSeparator, desc);
            }
            --idx;
        }
        if ((0 != itemCount) || useSuffixWhenEmpty) {
            smartAppend(suffix, desc);
        }
    }

    /**
     * Appends prepared description to a provided {@link StringBuilder}.
     *
     * @param sb {@code StringBuilder} reference
     */
    public void appendTo(StringBuilder sb) {
        if (null == sb) {
            return;
        }
        final StringDescription desc = new StringDescription(sb);
        describeTo(desc);
    }

    /**
     * Removes redundant entries from accumulated items. See class JavaDoc for description when
     * each type of item is deemed redundant.
     */
    private void compactItems() {
        for (final Iterator<DescriptionItem> itemIterator = items.iterator(); itemIterator.hasNext(); ) {
            final DescriptionItem item = itemIterator.next();
            if (item.isRedundant()) {
                itemIterator.remove();
            }
        }
    }

    private static void appendItemSurroundingTextToDescription(DescriptionItem item, Object textChunk, Description desc) {
        if ((null != item.matcher) && (textChunk instanceof MismatchDescriptionProvider)) {
            ((MismatchDescriptionProvider) textChunk).describe(item.matcher, item.value, desc);
        } else if (textChunk instanceof DescriptionProvider) {
            ((DescriptionProvider) textChunk).describe(item.value, desc);
        } else {
            smartAppend(textChunk, desc);
        }
    }

    private static void smartAppend(Object obj, Description desc) {
        Object result = obj;
        while (result instanceof Callable) {
            try {
                result = ((Callable) result).call();
            } catch (Exception e) {
                throw new RuntimeException("Unable to obtain text for description", e);
            }
        }
        if (null == result) {
            // Nothing appended
        } else if (result instanceof SelfDescribing) {
            ((SelfDescribing) result).describeTo(desc);
        } else {
            desc.appendText(result.toString());
        }
    }


    static final class DescriptionItem {
        final Object valuePrefix;
        final Object valueSuffix;
        final Object value;
        final Matcher<Object> matcher;
        final DescriptionProvider<Object> valueDescriptionProvider;
        final MismatchDescriptionProvider<Object> mismatchDescriptionProvider;
        final boolean usingMatcher;

        DescriptionItem(Object valuePrefix, Object value, Object valueSuffix) {
            this(valuePrefix, value, valueSuffix, null);
        }

        DescriptionItem(Object valuePrefix, Object value, Object valueSuffix, DescriptionProvider descriptionProvider) {
            this.valuePrefix = valuePrefix;
            this.value = value;
            this.valueSuffix = valueSuffix;
            this.valueDescriptionProvider = descriptionProvider;
            this.matcher = null;
            this.mismatchDescriptionProvider = null;
            this.usingMatcher = false;
        }

        DescriptionItem(Object valuePrefix, Matcher<?> matcher, Object value, Object valueSuffix) {
            this(valuePrefix, matcher, value, valueSuffix, null);
        }

        DescriptionItem(Object valuePrefix, Matcher<?> matcher, Object value, Object valueSuffix, MismatchDescriptionProvider<?> mismatchDescriptionProvider) {
            this.valuePrefix = valuePrefix;
            this.value = value;
            this.valueSuffix = valueSuffix;
            this.valueDescriptionProvider = null;
            this.matcher = (Matcher<Object>) matcher;
            this.mismatchDescriptionProvider = (MismatchDescriptionProvider<Object>) mismatchDescriptionProvider;
            this.usingMatcher = true;
        }

        boolean isRedundant() {
            final boolean result;
            if (usingMatcher) {
                result = (null == matcher) || matcher.matches(value);
            } else {
                result = (null == value);
            }
            return result;
        }

        void describeValue(Description desc) {
            if (null != valueDescriptionProvider) {
                valueDescriptionProvider.describe(value, desc);
            } else if (value instanceof SelfDescribing) {
                ((SelfDescribing) value).describeTo(desc);
            } else {
                desc.appendText(String.valueOf(value));
            }
        }

        void describeMismatch(Description desc) {
            if (null != mismatchDescriptionProvider) {
                mismatchDescriptionProvider.describe(matcher, value, desc);
            } else if (null != matcher) {
                matcher.describeMismatch(value, desc);
            } else {
                desc.appendText("N/A");
            }
        }
    }

}
