/*
 * Copyright 2015 Boleslav Bobcik - Auderis
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

import cz.auderis.test.category.UnitTest;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static cz.auderis.test.matcher.numeric.NumericMatchers.withinExclusiveInclusiveRange;
import static cz.auderis.test.matcher.numeric.NumericMatchers.withinExclusiveRange;
import static cz.auderis.test.matcher.numeric.NumericMatchers.withinInclusiveExclusiveRange;
import static cz.auderis.test.matcher.numeric.NumericMatchers.withinRange;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class InRangeMatcherTest {

	@Test
	@Category(UnitTest.class)
	public void shouldContainNumbersFromRange() throws Exception {
		final long low = -123L;
		final long high = 456L;
		//
		final Matcher<Number> inRangeII = withinRange(low, high);
		final Matcher<Number> inRangeEE = withinExclusiveRange(low, high);
		final Matcher<Number> inRangeIE = withinInclusiveExclusiveRange(low, high);
		final Matcher<Number> inRangeEI = withinExclusiveInclusiveRange(low, high);
		//
		for (long x = low + 1L; x < high; ++x) {
			assertThat(x, inRangeII);
			assertThat(x, inRangeEE);
			assertThat(x, inRangeIE);
			assertThat(x, inRangeEI);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldNotContainNumberFromOutside() throws Exception {
		final long low = -12345L;
		final long high = 45678L;
		//
		final Matcher<Number> inRangeII = withinRange(low, high);
		final Matcher<Number> inRangeEE = withinExclusiveRange(low, high);
		final Matcher<Number> inRangeIE = withinInclusiveExclusiveRange(low, high);
		final Matcher<Number> inRangeEI = withinExclusiveInclusiveRange(low, high);
		//
		assertThat(low - 1L, not(inRangeII));
		assertThat(low - 1L, not(inRangeEE));
		assertThat(low - 1L, not(inRangeIE));
		assertThat(low - 1L, not(inRangeEI));
		assertThat(high + 1L, not(inRangeII));
		assertThat(high + 1L, not(inRangeEE));
		assertThat(high + 1L, not(inRangeIE));
		assertThat(high + 1L, not(inRangeEI));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldIncludeLowPointInLowInclusiveRange() throws Exception {
		final long low = 12L;
		//
		final Matcher<Number> inRangeII = withinRange(low, null);
		final Matcher<Number> inRangeIE = withinInclusiveExclusiveRange(low, null);
		//
		assertThat(low, inRangeII);
		assertThat(low, inRangeIE);
	}

	@Test
	@Category(UnitTest.class)
	public void shouldExcludeLowPointInLowExclusiveRange() throws Exception {
		final long low = 12L;
		//
		final Matcher<Number> inRangeEE = withinExclusiveRange(low, null);
		final Matcher<Number> inRangeEI = withinExclusiveInclusiveRange(low, null);
		//
		assertThat(low, not(inRangeEE));
		assertThat(low, not(inRangeEI));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldIncludeHighPointInHighInclusiveRange() throws Exception {
		final long high = 987654321L;
		//
		final Matcher<Number> inRangeII = withinRange(null, high);
		final Matcher<Number> inRangeEI = withinExclusiveInclusiveRange(null, high);
		//
		assertThat(high, inRangeII);
		assertThat(high, inRangeEI);
	}

	@Test
	@Category(UnitTest.class)
	public void shouldExcludeHighPointInHighExclusiveRange() throws Exception {
		final long high = 987654321L;
		//
		final Matcher<Number> inRangeEE = withinExclusiveRange(null, high);
		final Matcher<Number> inRangeIE = withinInclusiveExclusiveRange(null, high);
		//
		assertThat(high, not(inRangeEE));
		assertThat(high, not(inRangeIE));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchIntegralNumericTypes() throws Exception {
		final long low = 0L;
		final long high = 10L;
		final Matcher<Number> inRange = withinRange(low, high);
		//
		assertThat((byte) 1, inRange);
		assertThat((short) 2, inRange);
		assertThat(3, inRange);
		assertThat(4L, inRange);
		assertThat(new AtomicInteger(5), inRange);
		assertThat(new AtomicLong(6L), inRange);
	}

	@Test
	@Category(UnitTest.class)
	public void shouldFailOnNonIntegralNumericTypes() throws Exception {
		final long low = 0L;
		final long high = 10L;
		final Matcher<Number> inRange = withinRange(low, high);
		assertThat(1.0F, not(inRange));
		assertThat(2.0D, not(inRange));
		assertThat(BigInteger.valueOf(3L), not(inRange));
		assertThat(BigDecimal.valueOf(4L), not(inRange));
	}

}
