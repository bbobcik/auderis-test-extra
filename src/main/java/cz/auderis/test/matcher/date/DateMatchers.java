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

import org.hamcrest.Matcher;

import java.util.Calendar;
import java.util.Date;

public final class DateMatchers {

	public static <T> Matcher<Date> today() {
		final Date now = new Date();
		return new DateRangeMatcher(now, now, Calendar.DAY_OF_MONTH, true, true);
	}

	public static <T> Matcher<Date> thisMonth() {
		final Date now = new Date();
		return new DateRangeMatcher(now, now, Calendar.MONTH, true, true);
	}

	public static <T> Matcher<Date> thisYear() {
		final Date now = new Date();
		return new DateRangeMatcher(now, now, Calendar.YEAR, true, true);
	}

	public static <T> Matcher<Date> sameDayAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(refDate, refDate, Calendar.DAY_OF_MONTH, true, true);
	}

	public static <T> Matcher<Date> sameMonthAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(refDate, refDate, Calendar.MONTH, true, true);
	}

	public static <T> Matcher<Date> sameYearAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(refDate, refDate, Calendar.YEAR, true, true);
	}

	public static <T> Matcher<Date> sameDayAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return new DateRangeMatcher(refDate, refDate, Calendar.DAY_OF_MONTH, true, true);
	}

	public static <T> Matcher<Date> sameMonthAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return new DateRangeMatcher(refDate, refDate, Calendar.MONTH, true, true);
	}

	public static <T> Matcher<Date> sameYearAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return new DateRangeMatcher(refDate, refDate, Calendar.YEAR, true, true);
	}

	public static <T> Matcher<Date> beforeDate(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(null, refDate, Calendar.DAY_OF_MONTH, true, false);
	}

	public static <T> Matcher<Date> beforeDate(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return new DateRangeMatcher(null, refDate, Calendar.DAY_OF_MONTH, true, false);
	}

	public static <T> Matcher<Date> before(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(null, refDate, Calendar.MILLISECOND, true, false);
	}

	public static <T> Matcher<Date> before(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return new DateRangeMatcher(null, refDate, Calendar.MILLISECOND, true, false);
	}

	public static <T> Matcher<Date> afterDate(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(refDate, null, Calendar.DAY_OF_MONTH, false, true);
	}

	public static <T> Matcher<Date> afterDate(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return new DateRangeMatcher(refDate, null, Calendar.DAY_OF_MONTH, false, true);
	}

	public static <T> Matcher<Date> after(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return new DateRangeMatcher(refDate, null, Calendar.MILLISECOND, false, true);
	}

	public static <T> Matcher<Date> after(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return new DateRangeMatcher(refDate, null, Calendar.MILLISECOND, false, true);
	}

	public static <T> Matcher<Date> betweenDates(Date startDate, Date endDate) {
		return new DateRangeMatcher(startDate, endDate, Calendar.DAY_OF_MONTH, true, true);
	}

	public static <T> Matcher<Date> betweenDates(String startDateText, String endDateText) {
		final Date startDate = DateHelper.parseDateAllowingNull(startDateText);
		final Date endDate = DateHelper.parseDateAllowingNull(endDateText);
		return new DateRangeMatcher(startDate, endDate, Calendar.DAY_OF_MONTH, true, true);
	}

	public static <T> Matcher<Date> between(Date startDate, Date endDate) {
		return new DateRangeMatcher(startDate, endDate, Calendar.MILLISECOND, true, true);
	}

	public static <T> Matcher<Date> between(String startDateText, String endDateText) {
		final Date startDate = DateHelper.parseDateAllowingNull(startDateText);
		final Date endDate = DateHelper.parseDateAllowingNull(endDateText);
		return new DateRangeMatcher(startDate, endDate, Calendar.MILLISECOND, true, true);
	}

	private DateMatchers() {
		throw new AssertionError();
	}

}
