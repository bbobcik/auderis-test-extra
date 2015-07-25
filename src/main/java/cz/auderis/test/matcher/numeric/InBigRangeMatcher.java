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

import java.math.BigDecimal;
import java.math.BigInteger;

class InBigRangeMatcher extends TypeSafeMatcher<Number> {

	private final BigDecimal lowEnd;
	private final BigDecimal highEnd;
	private final boolean includeLowEnd;
	private final boolean includeHighEnd;

	InBigRangeMatcher(BigDecimal low, BigDecimal high, boolean includeLow, boolean includeHigh) {
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

	private BigDecimal toBigValue(Number num) {
		final BigDecimal value;
		if (num instanceof BigDecimal) {
			value = (BigDecimal) num;
		} else if ((num instanceof Double) || (num instanceof Float)) {
			value = BigDecimal.valueOf(num.doubleValue());
		} else if (num instanceof BigInteger) {
			value = new BigDecimal((BigInteger) num);
		} else {
			value = BigDecimal.valueOf(num.longValue());
		}
		return value;
	}

	@Override
	protected boolean matchesSafely(Number num) {
		final BigDecimal value = toBigValue(num);
		if (null != lowEnd) {
			final int cmp = value.compareTo(lowEnd);
			if ((cmp < 0) || (!includeLowEnd && (0 == cmp))) {
				return false;
			}
		}
		if (null != highEnd) {
			final int cmp = value.compareTo(highEnd);
			if ((cmp > 0) || (!includeHighEnd && (0 == cmp))) {
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
		final BigDecimal value = toBigValue(num);
		if (null == lowEnd) {
			// Ignore
		} else if (value.compareTo(lowEnd) < 0) {
			out.appendText(", less than lower range limit");
		} else if (!includeLowEnd && (0 == value.compareTo(lowEnd))) {
			out.appendText(", equal to excluded lower range limit");
		}
		if (null == highEnd) {
			// Ignore
		} else if (value.compareTo(highEnd) > 0) {
			out.appendText(", greater than upper range limit");
		} else if (!includeHighEnd && (0 == value.compareTo(highEnd))) {
			out.appendText(", equal to excluded upper range limit");
		}
	}

}
