/*
 * Copyright 2017 Boleslav Bobcik - Auderis
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
package cz.auderis.test.support;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(JUnitParamsRunner.class)
public class NaturalDescriptionJoinerTest {

    NaturalDescriptionJoiner joiner = new NaturalDescriptionJoiner();
    Description description = new StringDescription();

    @Test
    @Parameters({
            "a    | b    | c    | a:b..c",
            "a    | b    | null | a..b",
            "a    | null | c    | a..c",
            "null | b    | c    | b..c",
    })
    public void shouldSkipItemsWithNullValues(String x1, String x2, String x3, String expectedResult) throws Exception {
        // Given
        joiner.withNormalSeparator(":");
        joiner.withLastSeparator("..");

        // When
        for (String x : Arrays.asList(x1, x2, x3)) {
            joiner.add(nullable(x));
        }
        joiner.describeTo(description);

        // Then
        final String result = description.toString();
        assertThat(result, is(expectedResult));
    }

    @Test
    @Parameters({
            "a    | b    | c    | abc",
            "a    | b    | null | ab",
            "a    | null | c    | ",
            "null | b    | c    | bc",
    })
    public void shouldSkipNullValues(String prefix, String value, String suffix, String expectedResult) throws Exception {
        // Given
        joiner.withNormalSeparator(":");
        joiner.withLastSeparator("..");

        // When
        joiner.add(nullable(prefix), nullable(value), nullable(suffix));
        joiner.describeTo(description);

        // Then
        final String result = description.toString();
        assertThat(result, is(expectedResult));
    }

    @Test
    @Parameters({
            "prefix | true  | suffix | true  | prefixsuffix",
            "prefix | false | suffix | true  | suffix",
            "null   | true  | suffix | true  | suffix",
            "null   | false | suffix | true  | suffix",
            "prefix | true  | suffix | false | prefix",
            "prefix | false | suffix | false | ",
            "null   | true  | suffix | false | ",
            "null   | false | suffix | false | ",
            "prefix | true  | null   | true  | prefix",
            "prefix | false | null   | true  | ",
            "null   | true  | null   | true  | ",
            "null   | false | null   | true  | ",
            "prefix | true  | null   | false | prefix",
            "prefix | false | null   | false | ",
            "null   | true  | null   | false | ",
            "null   | false | null   | false | "
    })
    public void shouldUsePrefixAndSuffix(String prefix, boolean usePrefix, String suffix, boolean useSuffix, String expectedResult) throws Exception {
        // Given
        joiner.withPrefix(nullable(prefix));
        joiner.setUsePrefixWhenEmpty(usePrefix);
        joiner.withSuffix(nullable(suffix));
        joiner.setUseSuffixWhenEmpty(useSuffix);

        // When
        joiner.describeTo(description);

        final String result = description.toString();
        assertThat(result, is(expectedResult));
    }

    static String nullable(String str) {
        return "null".equals(str) ? null : str;
    }

}
