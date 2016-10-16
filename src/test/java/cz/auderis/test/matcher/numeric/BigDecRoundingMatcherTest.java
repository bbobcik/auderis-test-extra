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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BigDecRoundingMatcherTest {

    @Test
    @Parameters({
            "1230 | 0 | UP | 1230",
            "1234 | 0 | UP | 1234",
            "12345.1 | 0 | UP | 12346",
            "12345.9 | 0 | DOWN | 12345",
            "-12345.1 | 0 | UP | -12346",
            "-12345.9 | 0 | DOWN | -12345",
            "4.569 | 1 | FLOOR | 4.5",
            "4.569 | 1 | CEILING | 4.6",
            "-4.569 | 1 | FLOOR | -4.6",
            "-4.569 | 1 | CEILING | -4.5",
    })
    public void shouldCorrectlyMatchRoundedValue(BigDecimal num, int scale, RoundingMode mode, BigDecimal expectedValue) throws Exception {
        final Matcher<BigDecimal> matcher = BigDecimalMatchers.hasRoundedValue(scale, mode, expectedValue);
        assertThat(num, matcher);
    }

}
