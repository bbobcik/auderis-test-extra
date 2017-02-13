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

package cz.auderis.test.matcher.multi;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.test.temp.multiprop.PropTopClass;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.not;

public class FluentMultiPropertyMatcherTest {

    public static final int INT_VALUE = -77;
    public static final String TEXT_VALUE = "xyzABC";
    public static final BigDecimal DECIMAL_VALUE = BigDecimal.valueOf(31415L, 4);
    public static final long LONG_VALUE = 987123L;

    PropTopClass testedObject = new PropTopClass(INT_VALUE, TEXT_VALUE, DECIMAL_VALUE, LONG_VALUE);
    PropTopClass badObject = new PropTopClass(INT_VALUE+1, TEXT_VALUE+"x", DECIMAL_VALUE.scaleByPowerOfTen(1), LONG_VALUE-123L);

    MultiPropertyMatcher<PropTopClass> matchesRules;

    @Test
    public void shouldMatchSingleProperty() throws Exception {
        // Given / When
        matchesRules = MultiPropertyMatcher.of(PropTopClass.class, "property container")
                                           .addProperty("longProperty ", is(LONG_VALUE), (PropertyExtractor<PropTopClass, ?>) (PropTopClass obj) -> obj.getLongProperty());

        // Then
        assertThat(testedObject, matchesRules);
        assertThat(badObject, not(matchesRules));
    }

    @Test
    public void shouldMatchAllProperties() throws Exception {
        // Given / When
        matchesRules = MultiPropertyMatcher.of(PropTopClass.class, "property container")
                                           .addProperty("longProperty",
                                                        is(LONG_VALUE),
                                                        (PropertyExtractor<PropTopClass, ?>) (PropTopClass obj) -> obj.getLongProperty()
                                           )
                                           .addProperty("intProperty", is(INT_VALUE))
                                           .addProperty("decimalProperty", (Matcher) comparesEqualTo(DECIMAL_VALUE))
                                           .addProperty("textProperty", is(TEXT_VALUE));

        // Then
        assertThat(testedObject, matchesRules);
        assertThat(badObject, (matchesRules));
    }

}
