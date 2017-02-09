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

public class NaturalDescriptionJoiner implements SelfDescribing {

    Object prefix;
    Object suffix;
    Object normalSeparator;
    Object lastSeparator;
    boolean usePrefixWhenEmpty;
    boolean useSuffixWhenEmpty;
    final List<DescriptionItem> items;

    public NaturalDescriptionJoiner(Object prefix, Object normalSeparator, Object lastSeparator, Object suffix) {
        this.prefix = prefix;
        this.normalSeparator = (null != normalSeparator) ? normalSeparator : "";
        this.lastSeparator = (null != lastSeparator) ? lastSeparator : "";
        this.suffix = suffix;
        this.items = new LinkedList<>();
    }

    public NaturalDescriptionJoiner() {
        this(null, ", ", " and ", null);
    }

    public NaturalDescriptionJoiner(Object separator) {
        this(null, separator, separator, null);
    }

    public NaturalDescriptionJoiner(Object normalSeparator, Object lastSeparator) {
        this(null, normalSeparator, lastSeparator, null);
    }

    public NaturalDescriptionJoiner withPrefix(Object prefix) {
        this.prefix = prefix;
        return this;
    }

    public NaturalDescriptionJoiner withSuffix(Object suffix) {
        this.suffix = suffix;
        return this;
    }

    public NaturalDescriptionJoiner withNormalSeparator(Object normalSep) {
        this.normalSeparator = (null != normalSep) ? normalSep : "";
        return this;
    }

    public NaturalDescriptionJoiner withLastSeparator(Object lastSep) {
        this.lastSeparator = (null != lastSep) ? lastSep : "";
        return this;
    }

    public boolean isUsePrefixWhenEmpty() {
        return usePrefixWhenEmpty;
    }

    public void setUsePrefixWhenEmpty(boolean usePrefixWhenEmpty) {
        this.usePrefixWhenEmpty = usePrefixWhenEmpty;
    }

    public NaturalDescriptionJoiner withPrefixWhenEmpty() {
        this.usePrefixWhenEmpty = true;
        return this;
    }

    public NaturalDescriptionJoiner withoutPrefixWhenEmpty() {
        this.usePrefixWhenEmpty = false;
        return this;
    }

    public boolean isUseSuffixWhenEmpty() {
        return useSuffixWhenEmpty;
    }

    public void setUseSuffixWhenEmpty(boolean useSuffixWhenEmpty) {
        this.useSuffixWhenEmpty = useSuffixWhenEmpty;
    }

    public NaturalDescriptionJoiner withSuffixWhenEmpty() {
        this.useSuffixWhenEmpty = true;
        return this;
    }

    public NaturalDescriptionJoiner withoutSuffixWhenEmpty() {
        this.useSuffixWhenEmpty = false;
        return this;
    }

    public boolean isEmpty() {
        compactItems();
        return items.isEmpty();
    }

    public NaturalDescriptionJoiner add(Object valuePrefix, Object value, Object valueSuffix) {
        if (null != value) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, value, valueSuffix);
            items.add(item);
        }
        return this;
    }

    public void add(Object valuePrefix, Object value) {
        add(valuePrefix, value, null);
    }

    public void add(Object value) {
        add(null, value, null);
    }

    public <T> NaturalDescriptionJoiner addMismatch(Object valuePrefix, Matcher<? super T> valueMatcher, T value, Object valueSuffix) {
        if ((null != valueMatcher) && !valueMatcher.matches(value)) {
            final DescriptionItem item = new DescriptionItem(valuePrefix, valueMatcher, value, valueSuffix);
            items.add(item);
        }
        return this;
    }

    public <T> void addMismatch(Object valuePrefix, Matcher<? super T> valueMatcher, T value) {
        addMismatch(valuePrefix, valueMatcher, value, null);
    }

    public <T> void addMismatch(Matcher<? super T> valueMatcher, T value) {
        addMismatch(null, valueMatcher, value, null);
    }

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
            appendToDescription(desc, prefix);
        }
        int idx = itemCount - 1;
        for (DescriptionItem item : items) {
            appendToDescription(desc, item.valuePrefix);
            if (null != item.matcher) {
                item.matcher.describeMismatch(item.value, desc);
            } else {
                appendToDescription(desc, item.value);
            }
            appendToDescription(desc, item.valueSuffix);
            if (1 == idx) {
                appendToDescription(desc, lastSeparator);
            } else if (idx > 1) {
                appendToDescription(desc, normalSeparator);
            }
            --idx;
        }
        if ((0 != itemCount) || useSuffixWhenEmpty) {
            appendToDescription(desc, suffix);
        }
    }

    public void appendTo(StringBuilder sb) {
        if (null == sb) {
            return;
        }
        final StringDescription desc = new StringDescription(sb);
        describeTo(desc);
    }

    private void compactItems() {
        for (final Iterator<DescriptionItem> itemIterator = items.iterator(); itemIterator.hasNext(); ) {
            final DescriptionItem item = itemIterator.next();
            if (item.isRedundant()) {
                itemIterator.remove();
            }
        }
    }

    private static void appendToDescription(Description desc, Object obj) {
        if (obj instanceof SelfDescribing) {
            ((SelfDescribing) obj).describeTo(desc);
        } else if (null != obj) {
            desc.appendText(obj.toString());
        }
    }

    static final class DescriptionItem {
        final Object valuePrefix;
        final Object valueSuffix;
        final Object value;
        final Matcher<?> matcher;
        final boolean usingMatcher;

        DescriptionItem(Object valuePrefix, Object value, Object valueSuffix) {
            this.valuePrefix = valuePrefix;
            this.value = value;
            this.valueSuffix = valueSuffix;
            this.matcher = null;
            this.usingMatcher = false;
        }

        DescriptionItem(Object valuePrefix, Matcher<?> matcher, Object value, Object valueSuffix) {
            this.valuePrefix = valuePrefix;
            this.value = value;
            this.valueSuffix = valueSuffix;
            this.matcher = matcher;
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
    }

}
