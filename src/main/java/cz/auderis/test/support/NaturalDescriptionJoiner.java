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
            appendItemSurroundingTextToDescription(item.matcher, item.valuePrefix, desc);
            if (null != item.matcher) {
                item.describeMismatch(desc);
            } else {
                item.describeValue(desc);
            }
            appendItemSurroundingTextToDescription(item.matcher, item.valueSuffix, desc);
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

    private static void appendItemSurroundingTextToDescription(Matcher<?> matcher, Object obj, Description desc) {
        if ((null != matcher) && (obj instanceof MismatchDescriptionProvider)) {
            ((MismatchDescriptionProvider) obj).describe(matcher, obj, desc);
        } else if (obj instanceof DescriptionProvider) {
            ((DescriptionProvider) obj).describe(obj, desc);
        } else {
            smartAppend(obj, desc);
        }
    }

    private static void smartAppend(Object obj, Description desc) {
        Object result = obj;
        boolean reevaluate;
        do {
            if (result instanceof Callable) {
                try {
                    result = ((Callable) result).call();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to obtain text for description", e);
                }
            }
            reevaluate = !((null == result) || (result instanceof CharSequence) || (result instanceof SelfDescribing));
        } while (reevaluate);
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
