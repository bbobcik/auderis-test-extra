/*
 * Copyright 2016 Boleslav Bobcik - Auderis
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

package cz.auderis.test.matcher.array;

import cz.auderis.test.parameter.annotation.MultiArray;
import cz.auderis.test.parameter.annotation.MultiArrayInt;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static cz.auderis.test.matcher.array.ArrayDimensionMatcher.arrayWithDimensions;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class MultiDimArrayTest {

    @Test
    @Parameters({
            "2x2x2 : 1 2 -3 -4 -5 -6 7 8 | 2 * 2 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidatePrimitiveIntegerArrayDimensions(@MultiArrayInt Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x2x2 : 1 2 -3 -4 -5 -6 7 8 | 2 * 2 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidatePrimitiveIntegerArrayDimensions2(@MultiArray(int.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x2x2 : 1 2 -3 -4 -5 -6 7 8 | 2 * 2 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidateBoxedIntegerArrayDimensions(@MultiArray(Integer.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x2x2 : T T F F T F T F  | 2 * 2 * 2",
            "3     : F F T            | 3",
            "4x0   :                  | 4 X 0"
    })
    public void shouldValidatePrimitiveBooleanArrayDimensions(@MultiArray(boolean.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x2x2 : T True F False Yes No Y N  | 2 * 2 * 2",
            "3     : Y Y N            | 3",
            "4x0   :                  | 4 X 0"
    })
    public void shouldValidateBoxedBooleanArrayDimensions(@MultiArray(Boolean.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x1x2 : -5.009 -6.999 7.432 8.977131 | 2 * 1 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidatePrimitiveDoubleArrayDimensions(@MultiArray(double.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x1x2 : -5.009 -6.999 7.432 8.977131 | 2 * 1 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidateBoxedDoubleArrayDimensions(@MultiArray(Double.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

    @Test
    @Parameters({
            "2x1x2 : -5.009 -6.999 7.432 8.977131 | 2 * 1 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
    })
    public void shouldValidateBigDecimalArrayDimensions(@MultiArray(BigDecimal.class) Object multiArray, String expectedDimension) throws Exception {
        assertThat(multiArray, is(arrayWithDimensions(expectedDimension)));
    }

}
