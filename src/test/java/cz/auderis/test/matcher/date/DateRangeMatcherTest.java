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

package cz.auderis.test.matcher.date;

import cz.auderis.test.category.UnitTest;
import cz.auderis.test.parameter.convert.DateConverter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.ConvertParam;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static cz.auderis.test.matcher.date.DateMatchers.sameYearAs;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class DateRangeMatcherTest {

	@Test
	@Parameters({
			"2015-01-01T00:00:00.000 | 2015-12-31T23:59:59.999",
			"2015-12-31T23:59:59.999 | 2015-01-01T00:00:00.000",
			"2015-01-01T00:00:00.000 | 2015-01-01T00:00:00.000",
			"2015-12-31T23:59:59.999 | 2015-12-31T23:59:59.999"
	})
	@TestCaseName("[{index}] {0} same year as {1}")
	@Category(UnitTest.class)
	public void shouldMatchSameYear(
			@ConvertParam(value = DateConverter.class) Date sampleDate,
			@ConvertParam(value = DateConverter.class) Date referenceDate
	) throws Exception {
		assertThat(sampleDate, is(sameYearAs(referenceDate)));
	}

	@Test
	@Parameters({
			"2015-01-01T00:00:00.000 | 2014-12-31T23:59:59.999",
			"2015-12-31T23:59:59.999 | 2016-01-01T00:00:00.000",
			"2015-01-01T00:00:00.000 | 2010-01-01T00:00:00.000",
			"2015-12-31T23:59:59.999 | 2020-12-31T23:59:59.999"
	})
	@TestCaseName("[{index}] {0} has different year from {1}")
	@Category(UnitTest.class)
	public void shouldNotMatchDifferentYears(
			@ConvertParam(value = DateConverter.class) Date sampleDate,
			@ConvertParam(value = DateConverter.class) Date referenceDate
	) throws Exception {
		assertThat(sampleDate, not(sameYearAs(referenceDate)));
	}

	@Test
	@Parameters({
			"2000-01-01T00:00:00.000 | true",
			"2015-01-01T00:00:00.000 | true",
			"2015-06-06T23:59:59.999 | true",
			"2015-06-07T00:00:00.000 | false",
			"2015-06-07T12:00:00.000 | false",
			"2020-01-01T00:00:00.000 | false"
	})
	@TestCaseName("[{index}] {0} in interval (-inf, 2015-06-07) = {1}")
	@Category(UnitTest.class)
	public void shouldContainDateInStartOpenDateInterval(
			@ConvertParam(value = DateConverter.class) Date sampleDate,
			boolean beforeEnd
	) throws Exception {
		final Date endDate = DateHelper.parseDate("2015-06-07T08:09:10");
		final DateRangeMatcher inInterval = new DateRangeMatcher(null, endDate, Calendar.DAY_OF_MONTH, true, false);
		if (beforeEnd) {
			assertThat(sampleDate, inInterval);
		} else {
			assertThat(sampleDate, not(inInterval));
		}
	}

	@Test
	@Parameters({
			"2000-01-01T00:00:00.000 | false",
			"2015-01-01T00:00:00.000 | false",
			"2015-06-06T23:59:59.999 | false",
			"2015-06-07T00:00:00.000 | false",
			"2015-06-07T12:00:00.000 | false",
			"2015-06-07T23:59:59.999 | false",
			"2015-06-08T00:00:00.000 | true",
			"2020-01-01T00:00:00.000 | true"
	})
	@TestCaseName("[{index}] {0} in interval (2015-06-07, +inf) = {1}")
	@Category(UnitTest.class)
	public void shouldContainDateInEndOpenDateInterval(
			@ConvertParam(value = DateConverter.class) Date sampleDate,
			boolean beforeEnd
	) throws Exception {
		final Date startDate = DateHelper.parseDate("2015-06-07T08:09:10");
		final DateRangeMatcher inInterval = new DateRangeMatcher(startDate, null, Calendar.DAY_OF_MONTH, false, true);
		if (beforeEnd) {
			assertThat(sampleDate, inInterval);
		} else {
			assertThat(sampleDate, not(inInterval));
		}
	}

}
