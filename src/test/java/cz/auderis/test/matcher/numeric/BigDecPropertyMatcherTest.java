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

import cz.auderis.test.parameter.annotation.BigDec;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BigDecPropertyMatcherTest {

    @Test
    @Parameters({
            "0 | 1",
            "1 | 1",
            "-2 | 1",
            "3.1 | 2",
            "-42 | 2",
            "50 | 2",
            "4567 | 4",
            "-67890.000 | 8",
            "987654321.1357924680 | 19"
    })
    public void shouldMatchPrecision(@BigDec BigDecimal num, int expectedPrecision) throws Exception {
        Matcher<BigDecimal> matcher = BigDecimalMatchers.hasPrecision(expectedPrecision);
        assertThat(num, matcher);
    }

    @Test
    @Parameters({
            "0 | 1",
            "1 | 1",
            "-2 | 1",
            "3.1 | 2",
            "-42 | 2",
            "50 | 1",
            "4567 | 4",
            "-67890.000 | 4",
            "987654321.1357924680 | 18"
    })
    public void shouldMatchNormalizedPrecision(@BigDec BigDecimal num, int expectedPrecision) throws Exception {
        Matcher<BigDecimal> matcher = BigDecimalMatchers.hasNormalizedPrecision(expectedPrecision);
        assertThat(num, matcher);
    }

    @Test
    @Parameters({
            "0 | 0",
            "1 | 0",
            "-2 | 0",
            "3.1 | 1",
            "-42.1 | 1",
            "50.0 | 1",
            "-4.728 | 3",
            "9.314159000 | 9",
            "-67890.000 | 3",
            "-987654321.1357924680 | 10"
    })
    public void shouldMatchScale(@BigDec BigDecimal num, int expectedScale) throws Exception {
        Matcher<BigDecimal> matcher = BigDecimalMatchers.hasScale(expectedScale);
        assertThat(num, matcher);
    }

    @Test
    @Parameters({
            "0 | 0",
            "1 | 0",
            "-2 | 0",
            "3.1 | 1",
            "-42.1 | 1",
            "50.0 | -1",
            "-4.728 | 3",
            "9.314159000 | 6",
            "-67800.000 | -2",
            "-987654321.1357924680 | 9",
            "7000.000 | -3"
    })
    public void shouldMatchNormalizedScale(@BigDec BigDecimal num, int expectedScale) throws Exception {
        Matcher<BigDecimal> matcher = BigDecimalMatchers.hasNormalizedScale(expectedScale);
        assertThat(num, matcher);
    }



}
