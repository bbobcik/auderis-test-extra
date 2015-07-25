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

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class InBigRangeMatcherTest {

	@Test
	@Category(UnitTest.class)
	public void shouldContainNumbersFromRange() throws Exception {
		final BigDecimal low = new BigDecimal("-147.9");
		final BigDecimal high = new BigDecimal("765.12345");
		final BigDecimal step = new BigDecimal("0.1");
		//
		final Matcher<Number> inRange = RangeMatchers.withinBigRange(low, high);
		//
		for (BigDecimal x = low.add(step); x.compareTo(high) < 0; x = x.add(step)) {
			assertThat(x, inRange);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldNotContainNumberFromOutside() throws Exception {
		final BigDecimal low = new BigDecimal("-147.9");
		final BigDecimal high = new BigDecimal("765.12345");
		final BigDecimal step = new BigDecimal("0.00000000001");
		//
		final Matcher<Number> inRange = RangeMatchers.withinBigRange(low, high);
		//
		assertThat(low.subtract(step), not(inRange));
		assertThat(high.add(step), not(inRange));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldIncludeLowPointInLowInclusiveRange() throws Exception {
		final BigDecimal low = new BigDecimal("123.987654321");
		//
		final Matcher<Number> inRangeII = RangeMatchers.withinBigRange(low, null);
		final Matcher<Number> inRangeIE = RangeMatchers.withinBigInclusiveExclusiveRange(low, null);
		//
		assertThat(low, inRangeII);
		assertThat(low, inRangeIE);
	}

	@Test
	@Category(UnitTest.class)
	public void shouldExcludeLowPointInLowExclusiveRange() throws Exception {
		final BigDecimal low = new BigDecimal("123.987654321");
		//
		final Matcher<Number> inRangeEE = RangeMatchers.withinBigExclusiveRange(low, null);
		final Matcher<Number> inRangeEI = RangeMatchers.withinBigExclusiveInclusiveRange(low, null);
		//
		assertThat(low, not(inRangeEE));
		assertThat(low, not(inRangeEI));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldIncludeHighPointInHighInclusiveRange() throws Exception {
		final BigDecimal high = new BigDecimal("122333.444455555");
		//
		final Matcher<Number> inRangeII = RangeMatchers.withinBigRange(null, high);
		final Matcher<Number> inRangeEI = RangeMatchers.withinBigExclusiveInclusiveRange(null, high);
		//
		assertThat(high, inRangeII);
		assertThat(high, inRangeEI);
	}

	@Test
	@Category(UnitTest.class)
	public void shouldExcludeHighPointInHighExclusiveRange() throws Exception {
		final BigDecimal high = new BigDecimal("122333.444455555");
		//
		final Matcher<Number> inRangeEE = RangeMatchers.withinBigExclusiveRange(null, high);
		final Matcher<Number> inRangeIE = RangeMatchers.withinBigInclusiveExclusiveRange(null, high);
		//
		assertThat(high, not(inRangeEE));
		assertThat(high, not(inRangeIE));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchAllNumericTypes() throws Exception {
		final BigDecimal low = BigDecimal.ZERO;
		final BigDecimal high = BigDecimal.TEN;
		final Matcher<Number> inRange = RangeMatchers.withinBigRange(low, high);
		//
		assertThat((byte) 1, inRange);
		assertThat((short) 2, inRange);
		assertThat(3, inRange);
		assertThat(4L, inRange);
		assertThat(5.0F, inRange);
		assertThat(6.0D, inRange);
		assertThat(BigInteger.valueOf(7L), inRange);
		assertThat(new AtomicInteger(8), inRange);
		assertThat(new AtomicLong(9L), inRange);
	}

}