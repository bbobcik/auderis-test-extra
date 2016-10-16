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
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

public class BigDecEqualityMatcher extends TypeSafeMatcher<BigDecimal> {

    private final BigDecimal expectedValue;
    private final boolean strictMatch;

    protected BigDecEqualityMatcher(BigDecimal expectedValue, boolean strictMatch) {
        super(BigDecimal.class);
        assert null != expectedValue;
        this.expectedValue = expectedValue;
        this.strictMatch = strictMatch;
    }

    @Override
    protected boolean matchesSafely(BigDecimal num) {
        final boolean match;
        if (strictMatch) {
            match = expectedValue.equals(num);
        } else {
            match = (0 == expectedValue.compareTo(num));
        }
        return match;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("decimal value ");
        description.appendValue(expectedValue);
        if (strictMatch) {
            description.appendText(" with scale " + expectedValue.scale());
        }
    }

    @Override
    protected void describeMismatchSafely(BigDecimal num, Description out) {
        out.appendText("value ").appendValue(num);
        final boolean sameValue = (0 == expectedValue.compareTo(num));
        if (!sameValue) {
            final BigDecimal delta = num.subtract(expectedValue);
            out.appendText(" was different by " + delta.toPlainString());
        } else if (strictMatch) {
            out.appendText(" had scale " + num.scale());
        }
    }

}
