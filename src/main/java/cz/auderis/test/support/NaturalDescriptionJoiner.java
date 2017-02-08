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
import org.hamcrest.SelfDescribing;

import java.util.Deque;
import java.util.LinkedList;

public class NaturalDescriptionJoiner implements SelfDescribing {

    Object prefix;
    Object suffix;
    Object normalSeparator;
    Object lastSeparator;
    boolean usePrefixWhenEmpty;
    boolean useSuffixWhenEmpty;
    final Deque<DescriptionItem> items;

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

    @Override
    public void describeTo(Description desc) {
        final int itemCount = items.size();
        if ((null == desc) || ((0 == itemCount) && !usePrefixWhenEmpty && !useSuffixWhenEmpty)) {
            return;
        }
        if ((0 != itemCount) || usePrefixWhenEmpty) {
            appendToDescription(desc, prefix);
        }
        int idx = itemCount - 1;
        for (DescriptionItem item : items) {
            appendToDescription(desc, item.valuePrefix);
            appendToDescription(desc, item.value);
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
        final int itemCount = items.size();
        if ((null == sb) || ((0 == itemCount) && !usePrefixWhenEmpty && !useSuffixWhenEmpty)) {
            return;
        }
        if ((0 != itemCount) || usePrefixWhenEmpty) {
            appendToStrBuilder(sb, prefix);
        }
        int idx = itemCount - 1;
        for (DescriptionItem item : items) {
            appendToStrBuilder(sb, item.valuePrefix);
            appendToStrBuilder(sb, item.value);
            appendToStrBuilder(sb, item.valueSuffix);
            if (1 == idx) {
                appendToStrBuilder(sb, lastSeparator);
            } else if (idx > 1) {
                appendToStrBuilder(sb, normalSeparator);
            }
            --idx;
        }
        if ((0 != itemCount) || useSuffixWhenEmpty) {
            appendToStrBuilder(sb, suffix);
        }
    }

    private static void appendToDescription(Description desc, Object obj) {
        if (obj instanceof SelfDescribing) {
            ((SelfDescribing) obj).describeTo(desc);
        } else if (null != obj) {
            desc.appendText(obj.toString());
        }
    }

    private static void appendToStrBuilder(StringBuilder sb, Object obj) {
        if (null != obj) {
            sb.append(obj);
        }
    }

    static final class DescriptionItem {
        final Object valuePrefix;
        final Object valueSuffix;
        final Object value;

        DescriptionItem(Object valuePrefix, Object value, Object valueSuffix) {
            this.valuePrefix = valuePrefix;
            this.value = value;
            this.valueSuffix = valueSuffix;
        }
    }

}
