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
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.Callable;

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
    @Parameters(method = "matcherPairs")
    public void shouldSkipItemsWithNullOrSatisfiedMatcher(Matcher<? super String> matcher1, String value1, Matcher<? super String> matcher2, String value2, String expectedResult) throws Exception {
        // Given
        joiner.withNormalSeparator(":");
        joiner.withLastSeparator("..");

        // When
        joiner.addMismatch(matcher1, value1);
        joiner.addMismatch(matcher2, value2);
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

    @Test
    @Parameters({
            "null | A+B+C+X+Y*Z",
            "(%s) | (A)+(B)+(C)+(X)+(Y)*(Z)",
            "%-2s.  | A .+B .+C .+X .+Y .*Z ."
    })
    public void shouldFormatValues(String valueFormat, String expectedResult) throws Exception {
        // Given
        joiner.withNormalSeparator("+");
        joiner.withLastSeparator("*");
        final FmtDescProvider fmt = new FmtDescProvider(nullable(valueFormat));

        // When
        joiner.add(null, "A", null, fmt);
        joiner.add(null, "B", null, fmt);
        joiner.add(null, "C", null, fmt);
        joiner.add("K", null, "L", fmt);
        joiner.add(null, "X", null, fmt);
        joiner.add(null, "Y", null, fmt);
        joiner.add(null, "Z", null, fmt);
        joiner.describeTo(description);

        // Then
        final String result = description.toString();
        assertThat(result, is(expectedResult));
    }

    @Test
    @Parameters(method = "boundaryTextGenerators")
    public void shouldUseFunctionalPrefix(Object textSpec, String expectedText) throws Exception {
        // Given/ When
        joiner.withPrefix(textSpec).setUsePrefixWhenEmpty(true);
        joiner.describeTo(description);

        // Then
        final String result = description.toString();
        assertThat(result, is(expectedText));
    }

    @Test
    @Parameters(method = "boundaryTextGenerators")
    public void shouldUseFunctionalSuffix(Object textSpec, String expectedText) throws Exception {
        // Given/ When
        joiner.withSuffix(textSpec).setUseSuffixWhenEmpty(true);
        joiner.describeTo(description);

        // Then
        final String result = description.toString();
        assertThat(result, is(expectedText));
    }


    public Object[][] matcherPairs() {
        return new Object[][] {
                // matcher1, value1, matcher2, value2, expected result
                { null,    "x",  null,    "y", ""},
                { is("x"), "x",  is("y"), "y", ""},
                { is("x"), "a",  is("y"), "y", "was \"a\""},
                { is("x"), "a",  is("y"), "b", "was \"a\"..was \"b\""},
                { is("x"), null, is("y"), "b", "was null..was \"b\""},
                { null,    null, is("y"), "b", "was \"b\""},
        };
    }

    public Object[][] boundaryTextGenerators() {
        return new Object[][] {
                // generator, expected result
                { callable("Text"),                                       "Text" },
                { callable(callable("InnerText")),                        "InnerText" },
                { (SelfDescribing) desc -> desc.appendText("LambdaText"), "LambdaText" },
                { null,                                                   "" },
                { callable(null),                                         "" },
                { callable(callable(null)),                               "" }
        };
    }

    static String nullable(String str) {
        return "null".equals(str) ? null : str;
    }

    static Callable<Object> callable(Object result) {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return result;
            }
        };
    }


    static final class FmtDescProvider implements DescriptionProvider<Object> {
        final String fmt;

        FmtDescProvider(String fmt) {
            this.fmt = fmt;
        }

        @Override
        public void describe(Object object, Description targetDescription) {
            if (null != fmt) {
                final String text = String.format(fmt, object);
                targetDescription.appendText(text);
            } else {
                targetDescription.appendText(object.toString());
            }
        }
    }

}
