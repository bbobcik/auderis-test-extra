/*
 * Copyright 2015 Boleslav Bobcik - Auderis
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

package cz.auderis.test.matcher.numeric;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class InRangeIntegerMatcher extends TypeSafeMatcher<Number> {

	private static final Set<Class<?>> INTEGER_CLASSES = supportedIntegerClasses();

	private final Long lowEnd;
	private final Long highEnd;
	private final boolean includeLowEnd;
	private final boolean includeHighEnd;

	InRangeIntegerMatcher(Long low, Long high, boolean includeLow, boolean includeHigh) {
		super(Number.class);
		if ((null != low) && (null != high)) {
			final int cmp = high.compareTo(low);
			if (cmp < 0) {
				throw new IllegalArgumentException("invalid range definition - high < low");
			} else if ((0 == cmp) && (!includeLow || !includeHigh)) {
				throw new IllegalArgumentException("invalid range definition - range empty");
			}
		}
		this.lowEnd = low;
		this.highEnd = high;
		this.includeLowEnd = includeLow;
		this.includeHighEnd = includeHigh;
	}

	private boolean isUnsupportedNumberType(Number num) {
		final Class<?> numClass = num.getClass();
		return !INTEGER_CLASSES.contains(numClass);
	}

	@Override
	protected boolean matchesSafely(Number num) {
		if (isUnsupportedNumberType(num)) {
			return false;
		}
		final long value = num.longValue();
		if (null != lowEnd) {
			if ((value < lowEnd) || (!includeLowEnd && (value == lowEnd))) {
				return false;
			}
		}
		if (null != highEnd) {
			if ((value > highEnd) || (!includeHighEnd && (value == highEnd))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("in range ");
		if (null == lowEnd) {
			description.appendText("(-inf");
		} else {
			final String rangeBrace = includeLowEnd ? "[" : "(";
			description.appendText(rangeBrace).appendText(lowEnd.toString());
		}
		description.appendText("; ");
		if (null == highEnd) {
			description.appendText("+inf)");
		} else {
			final String rangeBrace = includeHighEnd ? "]" : ")";
			description.appendText(highEnd.toString()).appendText(rangeBrace);
		}
	}

	@Override
	protected void describeMismatchSafely(Number num, Description out) {
		out.appendText("was ").appendValue(num);
		if (isUnsupportedNumberType(num)) {
			out.appendText(" of unsupported type ").appendValue(num.getClass());
			return;
		}
		final long value = num.longValue();
		if (null == lowEnd) {
			// Ignore
		} else if (value < lowEnd) {
			out.appendText(", less than lower range limit");
		} else if (!includeLowEnd && (value == lowEnd)) {
			out.appendText(", equal to excluded lower range limit");
		}
		if (null == highEnd) {
			// Ignore
		} else if (value > highEnd) {
			out.appendText(", greater than upper range limit");
		} else if (!includeHighEnd && (value == highEnd)) {
			out.appendText(", equal to excluded upper range limit");
		}
	}


	private static Set<Class<?>> supportedIntegerClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>(16);
		classes.add(Byte.TYPE);
		classes.add(Byte.class);
		classes.add(Short.TYPE);
		classes.add(Short.class);
		classes.add(Integer.TYPE);
		classes.add(Integer.class);
		classes.add(Long.TYPE);
		classes.add(Long.class);
		classes.add(AtomicInteger.class);
		classes.add(AtomicLong.class);
		return Collections.unmodifiableSet(classes);
	}

}
