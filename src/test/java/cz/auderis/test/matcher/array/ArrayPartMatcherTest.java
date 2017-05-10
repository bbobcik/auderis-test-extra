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

package cz.auderis.test.matcher.array;

import cz.auderis.test.category.UnitTest;
import cz.auderis.test.parameter.annotation.MultiArray;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static cz.auderis.test.matcher.array.ArrayPartMatcher.contains;
import static cz.auderis.test.matcher.text.TextMatchers.matchingPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Created on 10.5.2017.
 */
@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class ArrayPartMatcherTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Integer[] sourceArray = prepareSourceArray();

    @Test
    @Parameters({
            "0 : ",
            "1 : 1",
            "1 : 5",
            "1 : 10",
            "2 : 1 2",
            "2 : 3 4",
            "2 : 9 10",
            "5 : 1 2 3 4 5",
            "5 : 4 5 6 7 8",
            "5 : 6 7 8 9 10",
            "10 : 1 2 3 4 5 6 7 8 9 10",
    })
    public void shouldMatchAnywhere(@MultiArray(Integer.class) Integer[] sample) throws Exception {
        assertThat(sourceArray, contains(sample));
    }

    @Test
    @Parameters({
            "1 : 0",
            "1 : 11",
            "2 : 2 1",
            "2 : 10 9",
            "2 : 1 3",
            "9 : 3 3 4 5 6 7 8 9 10",
    })
    public void shouldNotMatchAnywhere(@MultiArray(Integer.class) Integer[] sample) throws Exception {
        expectAssertion("does not contain the reference sub-array");
        assertThat(sourceArray, contains(sample));
    }

    @Test
    @Parameters({
            "0 :                       | 0",
            "0 :                       | 7",
            "0 :                       | 15",
            "1 : 1                     | 0",
            "1 : 10                    | 9",
            "2 : 1 2                   | 0",
            "2 : 1 2                   | 10",
            "2 : 2 3                   | 1",
            "2 : 8 9                   | 7",
            "2 : 9 10                  | 8",
            "9 : 1 2 3 4 5 6 7 8 9     | 0",
            "9 : 2 3 4 5 6 7 8 9 10    | 1",
            "10 : 1 2 3 4 5 6 7 8 9 10 | 0",
    })
    public void shouldMatchWithFixedStartOffset(@MultiArray(Integer.class) Integer[] sample, int startOffset) throws Exception {
        assertThat(sourceArray, contains(sample).withStartIndex(startOffset));
    }

    @Test
    @Parameters({
            "16 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 | 0 | has insufficient length \\(missing 1 items\\)",
            "1 : 2                                 | 0 | contains mismatch at offset 0",
            "1 : 9                                 | 9 | contains mismatch at offset 9",
            "3 : 1 3 3                             | 0 | contains mismatch at offset 1",
            "3 : 1 3 4                             | 0 | contains mismatch block at offsets 1-2",
            "3 : 9 9 10                            | 7 | contains mismatch at offset 7",
            "3 : 8 8 10                            | 7 | contains mismatch at offset 8",
            "3 : 8 9 9                             | 7 | contains mismatch at offset 9",
            "3 : 7 8 9                             | 7 | contains mismatch block at offsets 7-9",
            "5 : 2 1 3 5 4                         | 0 | contains 2 mismatching blocks\\, first at offsets 0-1",
            "8 : 2 4 3 5 6 1 8 1                   | 1 | contains 3 mismatching blocks\\, first at offsets 2-3",
    })
    public void shouldNotMatchWithFixedStart(@MultiArray(Integer.class) Integer[] sample, int startOffset, String expectedMismatchPattern) throws Exception {
        expectAssertion(expectedMismatchPattern);
        assertThat(sourceArray, contains(sample).withStartIndex(startOffset));
    }

    @Test
    @Parameters({
            "0 :                                 | 0",
            "0 :                                 | 5",
            "0 :                                 | 15",
            "1 : 1                               | 14",
            "1 : 10                              | 5",
            "1 : 5                               | 0",
            "2 : 1 2                             | 13",
            "2 : 2 3                             | 12",
            "2 : 8 9                             | 6",
            "2 : 9 10                            | 5",
            "2 : 3 4                             | 1",
            "2 : 4 5                             | 0",
            "14 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4   | 1",
            "14 : 2 3 4 5 6 7 8 9 10 1 2 3 4 5   | 0",
            "15 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 | 0",
    })
    public void shouldMatchWithFixedRemainingItems(@MultiArray(Integer.class) Integer[] sample, int itemsToFollow) throws Exception {
        assertThat(sourceArray, contains(sample).withNumberOfFollowingItems(itemsToFollow));
    }

    @Test
    @Parameters({
            "16 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 |  0 | has insufficient length \\(missing 1 items\\)",
            "1 : 2                                 |  5 | contains mismatch at offset 9",
            "1 : 9                                 | 14 | contains mismatch at offset 0",
            "3 : 1 3 3                             |  5 | contains mismatch block at offsets 7-9",
            "3 : 1 3 4                             | 12 | contains mismatch block at offsets 1-2",
            "3 : 9 9 10                            |  5 | contains mismatch at offset 7",
            "3 : 8 8 10                            |  5 | contains mismatch at offset 8",
            "3 : 8 9 9                             |  5 | contains mismatch at offset 9",
            "3 : 7 8 9                             |  5 | contains mismatch block at offsets 7-9",
            "5 : 2 1 3 5 4                         | 10 | contains 2 mismatching blocks\\, first at offsets 0-1",
            "8 : 2 4 3 5 6 1 8 1                   |  6 | contains 3 mismatching blocks\\, first at offsets 2-3",
    })
    public void shouldNotMatchWithFixedRemainingItems(@MultiArray(Integer.class) Integer[] sample, int itemsToFollow, String expectedMismatchPattern) throws Exception {
        expectAssertion(expectedMismatchPattern);
        assertThat(sourceArray, contains(sample).withNumberOfFollowingItems(itemsToFollow));
    }

    @Test
    @Parameters({
            "0 :                       | 0",
            "0 :                       | 10",
            "1 : 1                     | 0",
            "1 : 10                    | 9",
            "2 : 1 2                   | 0",
            "2 : 1 2                   | 1",
            "2 : 2 3                   | 1",
            "2 : 8 9                   | 7",
            "2 : 9 10                  | 8",
            "2 : 10 1                  | 9",
            "2 : 10 1                  | 12",
            "9 : 1 2 3 4 5 6 7 8 9     | 0",
            "9 : 2 3 4 5 6 7 8 9 10    | 1",
            "10 : 1 2 3 4 5 6 7 8 9 10 | 0",
    })
    public void shouldMatchWithStartOffsetConstraint(@MultiArray(Integer.class) Integer[] sample, int maxStartOffset) throws Exception {
        assertThat(sourceArray, contains(sample).withStartIndex(lessThanOrEqualTo(maxStartOffset)));
    }

    @Test
    @Parameters({
            "1 : 11                    | -1 | does not contain the reference sub-array",
            "5 : 5 6 7 8 8             |  0 | does not contain the reference sub-array",
            "1 : 1                     | 11 | contains 2 matches\\, but start offset constraint .* is not satisfied",
            "1 : 10                    |  9 | contains a match\\, but start offset constraint .* is not satisfied",
            "2 : 1 2                   | 12 | contains 2 matches\\, but start offset constraint .* is not satisfied",
            "2 : 5 6                   |  9 | contains a match\\, but start offset constraint .* is not satisfied",
            "2 : 8 9                   |  7 | contains a match\\, but start offset constraint .* is not satisfied",
            "2 : 9 10                  |  8 | contains a match\\, but start offset constraint .* is not satisfied",
            "9 : 1 2 3 4 5 6 7 8 9     |  0 | contains a match\\, but start offset constraint .* is not satisfied",
            "9 : 2 3 4 5 6 7 8 9 10    |  1 | contains a match\\, but start offset constraint .* is not satisfied",
            "10 : 1 2 3 4 5 6 7 8 9 10 |  0 | contains a match\\, but start offset constraint .* is not satisfied",
    })
    public void shouldNotMatchWithStartOffsetConstraint(@MultiArray(Integer.class) Integer[] sample, int minStartOffset, String expectedMessagePattern) throws Exception {
        expectAssertion(expectedMessagePattern);
        assertThat(sourceArray, contains(sample).withStartIndex(greaterThan(minStartOffset)));
    }

    @Test
    @Parameters({
            "0 :                                 | 0",
            "0 :                                 | 10",
            "1 : 1                               | 11",
            "1 : 1                               | 4",
            "1 : 10                              | 5",
            "2 : 1 2                             | 13",
            "2 : 1 2                             | 3",
            "2 : 2 3                             | 2",
            "2 : 8 9                             | 6",
            "2 : 9 10                            | 5",
            "2 : 10 1                            | 9",
            "2 : 10 1                            | 12",
            "9 : 1 2 3 4 5 6 7 8 9               | 6",
            "9 : 1 2 3 4 5 6 7 8 9               | 10",
            "9 : 2 3 4 5 6 7 8 9 10              | 5",
            "15 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 | 0",
    })
    public void shouldMatchWithFollowingItemsConstraint(@MultiArray(Integer.class) Integer[] sample, int maxFollowingItems) throws Exception {
        assertThat(sourceArray, contains(sample).withNumberOfFollowingItems(lessThanOrEqualTo(maxFollowingItems)));
    }

    @Test
    @Parameters({
            "1 : 11                              | -1 | does not contain the reference sub-array",
            "5 : 5 6 7 8 8                       |  0 | does not contain the reference sub-array",
            "1 : 1                               | 15 | contains 2 matches\\, but constraint for following item count .* is not satisfied",
            "1 : 10                              |  6 | contains a match\\, but constraint for following item count .* is not satisfied",
            "2 : 1 2                             | 14 | contains 2 matches\\, but constraint for following item count .* is not satisfied",
            "2 : 8 9                             |  7 | contains a match\\, but constraint for following item count .* is not satisfied",
            "2 : 9 10                            |  5 | contains a match\\, but constraint for following item count .* is not satisfied",
            "9 : 1 2 3 4 5 6 7 8 9               |  6 | contains a match\\, but constraint for following item count .* is not satisfied",
            "9 : 2 3 4 5 6 7 8 9 10              |  5 | contains a match\\, but constraint for following item count .* is not satisfied",
            "15 : 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 |  0 | contains a match\\, but constraint for following item count .* is not satisfied",
    })
    public void shouldNotMatchWithFollowingItemsConstraint(@MultiArray(Integer.class) Integer[] sample, int minFollowingItems, String expectedMessagePattern) throws Exception {
        expectAssertion(expectedMessagePattern);
        assertThat(sourceArray, contains(sample).withNumberOfFollowingItems(greaterThan(minFollowingItems)));
    }


    private void expectAssertion(String messagePattern) {
        expectedException.expect(AssertionError.class);
        if ((null != messagePattern) && !messagePattern.trim().isEmpty()) {
            expectedException.expectMessage((Matcher) matchingPattern(messagePattern));
        }
    }

    private static Integer[] prepareSourceArray() {
        final int size = 15;
        final Integer[] result = new Integer[size];
        for (int i=0; i<size; ++i) {
            result[i] = (i % 10) + 1;
        }
        return result;
    }

}
