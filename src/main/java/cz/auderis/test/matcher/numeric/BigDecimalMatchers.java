/*
 * Copyright 2015-2016 Boleslav Bobcik - Auderis
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

import org.hamcrest.CoreMatchers;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contains factory methods that create instances of {@link Matcher}s specialized on {@link BigDecimal} objects.
 */
public final class BigDecimalMatchers {

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} with the same numeric value
     * as the argument. It is intended to replace {@code org.hamcrest.Matchers.comparesEqualTo()}, while being
     * more readable.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasValue(BigDecimal num) {
        assert null != num;
        return new BigDecEqualityMatcher(num, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} with the same numeric value
     * as the argument and both values have the identical scale. This means that this matcher behaves exactly
     * as {@link BigDecimal#equals(Object)}, however it is intended as a more readable alternative.
     */
    @Factory
    public static <T> Matcher<BigDecimal> isStrictlyEqualTo(BigDecimal num) {
        assert null != num;
        return new BigDecEqualityMatcher(num, true);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose scale satisfies
     * the matcher provided as an argument. Notice that the scale reflects possible trailing zeros.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasScale(Matcher<? super Integer> scaleMatcher) {
        assert null != scaleMatcher;
        return new BigDecPropertyMatcher(null, scaleMatcher, false, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose scale is equal
     * to the argument. Notice that the scale reflects possible trailing zeros.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasScale(int expectedScale) {
        return new BigDecPropertyMatcher(null, CoreMatchers.is(expectedScale), false, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose normalized scale
     * (i.e. after trailing zeros removed) satisfies the matcher provided as an argument.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasNormalizedScale(Matcher<? super Integer> scaleMatcher) {
        assert null != scaleMatcher;
        return new BigDecPropertyMatcher(null, scaleMatcher, false, true);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose normalized scale
     * (i.e. after trailing zeros removed) is equal to the argument.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasNormalizedScale(int expectedScale) {
        return new BigDecPropertyMatcher(null, CoreMatchers.is(expectedScale), false, true);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose precision satisfies
     * the matcher provided as an argument. Notice that the precision reflects possible trailing zeros.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasPrecision(Matcher<? super Integer> precisionMatcher) {
        assert null != precisionMatcher;
        return new BigDecPropertyMatcher(precisionMatcher, null, false, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose normalized precision
     * (i.e. after trailing zeros stripped) satisfies
     * the matcher provided as an argument.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasNormalizedPrecision(Matcher<? super Integer> precisionMatcher) {
        assert null != precisionMatcher;
        return new BigDecPropertyMatcher(precisionMatcher, null, true, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose precision is equal
     * to the argument. Notice that the precision reflects possible trailing zeros.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasPrecision(int expectedPrecision) {
        return new BigDecPropertyMatcher(CoreMatchers.is(expectedPrecision), null, false, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} whose normalized precision
     * (i.e. after trailing zeros stripped) equals to the argument.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasNormalizedPrecision(int expectedPrecision) {
        return new BigDecPropertyMatcher(CoreMatchers.is(expectedPrecision), null, true, false);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} that, when rounded to the
     * given scale using the specified rounding mode, satisfies the provided matcher.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasRoundedValue(int scale, RoundingMode roundingMode, Matcher<? super BigDecimal> matcher) {
        return new BigDecRoundingMatcher(scale, roundingMode, matcher);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} that, when rounded to the
     * given scale using the specified rounding mode, numerically equals to the provided value.
     * @see #hasValue(BigDecimal)
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasRoundedValue(int scale, RoundingMode roundingMode, BigDecimal expectedValue) {
        return new BigDecRoundingMatcher(scale, roundingMode, hasValue(expectedValue));
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} that, when rounded to the
     * scale 0 using the specified rounding mode, satisfies the provided matcher.
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasRoundedValue(RoundingMode roundingMode, Matcher<? super BigDecimal> matcher) {
        return new BigDecRoundingMatcher(0, roundingMode, matcher);
    }

    /**
     * Creates a matcher that matches if the examined object is a {@code BigDecimal} that, when rounded to the
     * scale 0 using the specified rounding mode, numerically equals to the provided value.
     * @see #hasValue(BigDecimal)
     */
    @Factory
    public static <T> Matcher<BigDecimal> hasRoundedValue(RoundingMode roundingMode, BigDecimal expectedValue) {
        return new BigDecRoundingMatcher(0, roundingMode, hasValue(expectedValue));
    }

    private BigDecimalMatchers() {
        throw new AssertionError();
    }

}
