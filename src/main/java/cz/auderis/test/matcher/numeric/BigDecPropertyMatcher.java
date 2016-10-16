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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

public class BigDecPropertyMatcher extends TypeSafeMatcher<BigDecimal> {

    private final Matcher<? super Integer> precisionMatcher;
    private final Matcher<? super Integer> scaleMatcher;
    private final boolean useNormalizedPrecision;
    private final boolean useNormalizedScale;

    public BigDecPropertyMatcher(Matcher<? super Integer> precisionMatcher, Matcher<? super Integer> scaleMatcher, boolean normalizedPrecision, boolean normalizedScale) {
        super(BigDecimal.class);
        this.precisionMatcher = precisionMatcher;
        this.scaleMatcher = scaleMatcher;
        this.useNormalizedPrecision = normalizedPrecision;
        this.useNormalizedScale = normalizedScale;
    }

    @Override
    protected boolean matchesSafely(BigDecimal num) {
        if ((null != precisionMatcher) && !precisionMatcher.matches(getPrecision(num))) {
            return false;
        } else if ((null != scaleMatcher) && !scaleMatcher.matches(getScale(num))) {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("decimal number");
        String separator = " with ";
        if (null != precisionMatcher) {
            description.appendText(separator);
            separator = " and ";
            if (useNormalizedPrecision) {
                description.appendText("normalized ");
            }
            description.appendText("precision that ");
            description.appendDescriptionOf(precisionMatcher);
        }
        if (null != scaleMatcher) {
            description.appendText(separator);
            if (useNormalizedScale) {
                description.appendText("normalized ");
            }
            description.appendText("scale that ");
            description.appendDescriptionOf(scaleMatcher);
        }
    }

    @Override
    protected void describeMismatchSafely(BigDecimal num, Description out) {
        out.appendText("was decimal number ");
        out.appendValue(num);
        String separator = " with ";
        if ((null != precisionMatcher) && !precisionMatcher.matches(getPrecision(num))) {
            out.appendText(separator);
            separator = " and with ";
            if (useNormalizedPrecision) {
                out.appendText("normalized ");
            }
            out.appendText("precision that ");
            precisionMatcher.describeMismatch(getPrecision(num), out);
        }
        if ((null != scaleMatcher) && !scaleMatcher.matches(getScale(num))) {
            out.appendText(separator);
            if (useNormalizedScale) {
                out.appendText("normalized ");
            }
            out.appendText("scale that ");
            scaleMatcher.describeMismatch(getScale(num), out);
        }
    }

    private int getPrecision(BigDecimal num) {
        if (useNormalizedPrecision) {
            return num.stripTrailingZeros().precision();
        }
        return num.precision();
    }

    private int getScale(BigDecimal num) {
        if (useNormalizedScale) {
            return num.stripTrailingZeros().scale();
        }
        return num.scale();
    }

}
