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

import org.hamcrest.Matcher;

import java.math.BigDecimal;

public final class RangeMatchers {

	public static <T> Matcher<Number> withinBigRange(BigDecimal low, BigDecimal high) {
		return new InBigRangeMatcher(low, high, true, true);
	}

	public static <T> Matcher<Number> withinBigExclusiveRange(BigDecimal low, BigDecimal high) {
		return new InBigRangeMatcher(low, high, false, false);
	}

	public static <T> Matcher<Number> withinBigInclusiveExclusiveRange(BigDecimal low, BigDecimal high) {
		return new InBigRangeMatcher(low, high, true, false);
	}

	public static <T> Matcher<Number> withinBigExclusiveInclusiveRange(BigDecimal low, BigDecimal high) {
		return new InBigRangeMatcher(low, high, false, true);
	}

	public static <T> Matcher<Number> withinBigRange(long lowInt, long highInt) {
		final BigDecimal low = BigDecimal.valueOf(lowInt);
		final BigDecimal high = BigDecimal.valueOf(highInt);
		return new InBigRangeMatcher(low, high, true, true);
	}

	public static <T> Matcher<Number> withinBigExclusiveRange(long lowInt, long highInt) {
		final BigDecimal low = BigDecimal.valueOf(lowInt);
		final BigDecimal high = BigDecimal.valueOf(highInt);
		return new InBigRangeMatcher(low, high, false, false);
	}

	public static <T> Matcher<Number> withinBigInclusiveExclusiveRange(long lowInt, long highInt) {
		final BigDecimal low = BigDecimal.valueOf(lowInt);
		final BigDecimal high = BigDecimal.valueOf(highInt);
		return new InBigRangeMatcher(low, high, true, false);
	}

	public static <T> Matcher<Number> withinBigExclusiveInclusiveRange(long lowInt, long highInt) {
		final BigDecimal low = BigDecimal.valueOf(lowInt);
		final BigDecimal high = BigDecimal.valueOf(highInt);
		return new InBigRangeMatcher(low, high, false, true);
	}

	public static <T> Matcher<Number> withinBigRange(double lowFloat, double highFloat) {
		final BigDecimal low = BigDecimal.valueOf(lowFloat);
		final BigDecimal high = BigDecimal.valueOf(highFloat);
		return new InBigRangeMatcher(low, high, true, true);
	}

	public static <T> Matcher<Number> withinBigExclusiveRange(double lowFloat, double highFloat) {
		final BigDecimal low = BigDecimal.valueOf(lowFloat);
		final BigDecimal high = BigDecimal.valueOf(highFloat);
		return new InBigRangeMatcher(low, high, false, false);
	}

	public static <T> Matcher<Number> withinBigInclusiveExclusiveRange(double lowFloat, double highFloat) {
		final BigDecimal low = BigDecimal.valueOf(lowFloat);
		final BigDecimal high = BigDecimal.valueOf(highFloat);
		return new InBigRangeMatcher(low, high, true, false);
	}

	public static <T> Matcher<Number> withinBigExclusiveInclusiveRange(double lowFloat, double highFloat) {
		final BigDecimal low = BigDecimal.valueOf(lowFloat);
		final BigDecimal high = BigDecimal.valueOf(highFloat);
		return new InBigRangeMatcher(low, high, false, true);
	}

	public static <T> Matcher<Number> withinRange(Long low, Long high) {
		return new InRangeIntegerMatcher(low, high, true, true);
	}

	public static <T> Matcher<Number> withinExclusiveRange(Long low, Long high) {
		return new InRangeIntegerMatcher(low, high, false, false);
	}

	public static <T> Matcher<Number> withinInclusiveExclusiveRange(Long low, Long high) {
		return new InRangeIntegerMatcher(low, high, true, false);
	}

	public static <T> Matcher<Number> withinExclusiveInclusiveRange(Long low, Long high) {
		return new InRangeIntegerMatcher(low, high, false, true);
	}

}
