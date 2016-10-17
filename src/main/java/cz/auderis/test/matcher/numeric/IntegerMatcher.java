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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IntegerMatcher extends TypeSafeMatcher<Number> {

    private static final Set<Class<?>> SYSTEM_INTEGER_CLASSES = Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(
            Byte.class, Byte.TYPE,
            Short.class, Short.TYPE,
            Integer.class, Integer.TYPE,
            Long.class, Long.TYPE,
            BigInteger.class,
            AtomicInteger.class,
            AtomicLong.class
    )));


    public IntegerMatcher() {
        super(Number.class);
    }

    @Override
    protected boolean matchesSafely(Number num) {
        final boolean isInteger;
        if (SYSTEM_INTEGER_CLASSES.contains(num.getClass())) {
            isInteger = true;
        } else if (num instanceof BigDecimal) {
            final BigDecimal decNum = (BigDecimal) num;
            isInteger = (decNum.stripTrailingZeros().scale() <= 0);
        } else {
            isInteger = false;
        }
        return isInteger;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("integer value");
    }

    @Override
    protected void describeMismatchSafely(Number num, Description out) {
        out.appendText("was ");
        if (num instanceof BigDecimal) {
            final BigDecimal decNum = (BigDecimal) num;
            out.appendValue(decNum.toPlainString());
            out.appendText(" with decimal scale " + decNum.scale());
        } else {
            out.appendText("unsupported type " + num.getClass() + " with value ");
            out.appendValue(num);
        }
    }

}
