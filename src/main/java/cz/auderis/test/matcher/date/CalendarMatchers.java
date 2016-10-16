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

package cz.auderis.test.matcher.date;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.Calendar;
import java.util.Date;

public final class CalendarMatchers {

	@Factory
	public static <T> Matcher<Calendar> today() {
		final Date now = new Date();
		return forCalendar(new DateRangeMatcher(now, now, Calendar.DAY_OF_MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> thisMonth() {
		final Date now = new Date();
		return forCalendar(new DateRangeMatcher(now, now, Calendar.MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> thisYear() {
		final Date now = new Date();
		return forCalendar(new DateRangeMatcher(now, now, Calendar.YEAR, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameDayAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.DAY_OF_MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameMonthAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameYearAs(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.YEAR, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameDayAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.DAY_OF_MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameMonthAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> sameYearAs(String dateText) {
		final Date refDate = DateHelper.parseDate(dateText);
		return forCalendar(new DateRangeMatcher(refDate, refDate, Calendar.YEAR, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> beforeDate(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(null, refDate, Calendar.DAY_OF_MONTH, true, false));
	}

	@Factory
	public static <T> Matcher<Calendar> beforeDate(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return forCalendar(new DateRangeMatcher(null, refDate, Calendar.DAY_OF_MONTH, true, false));
	}

	@Factory
	public static <T> Matcher<Calendar> before(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(null, refDate, Calendar.MILLISECOND, true, false));
	}

	@Factory
	public static <T> Matcher<Calendar> before(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return forCalendar(new DateRangeMatcher(null, refDate, Calendar.MILLISECOND, true, false));
	}

	@Factory
	public static <T> Matcher<Calendar> afterDate(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(refDate, null, Calendar.DAY_OF_MONTH, false, true));
	}

	@Factory
	public static <T> Matcher<Calendar> afterDate(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return forCalendar(new DateRangeMatcher(refDate, null, Calendar.DAY_OF_MONTH, false, true));
	}

	@Factory
	public static <T> Matcher<Calendar> after(Date refDate) {
		if (null == refDate) {
			throw new NullPointerException();
		}
		return forCalendar(new DateRangeMatcher(refDate, null, Calendar.MILLISECOND, false, true));
	}

	@Factory
	public static <T> Matcher<Calendar> after(String refDateText) {
		final Date refDate = DateHelper.parseDate(refDateText);
		return forCalendar(new DateRangeMatcher(refDate, null, Calendar.MILLISECOND, false, true));
	}

	@Factory
	public static <T> Matcher<Calendar> betweenDates(Date startDate, Date endDate) {
		return forCalendar(new DateRangeMatcher(startDate, endDate, Calendar.DAY_OF_MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> betweenDates(String startDateText, String endDateText) {
		final Date startDate = DateHelper.parseDateAllowingNull(startDateText);
		final Date endDate = DateHelper.parseDateAllowingNull(endDateText);
		return forCalendar(new DateRangeMatcher(startDate, endDate, Calendar.DAY_OF_MONTH, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> between(Date startDate, Date endDate) {
		return forCalendar(new DateRangeMatcher(startDate, endDate, Calendar.MILLISECOND, true, true));
	}

	@Factory
	public static <T> Matcher<Calendar> between(String startDateText, String endDateText) {
		final Date startDate = DateHelper.parseDateAllowingNull(startDateText);
		final Date endDate = DateHelper.parseDateAllowingNull(endDateText);
		return forCalendar(new DateRangeMatcher(startDate, endDate, Calendar.MILLISECOND, true, true));
	}

	private CalendarMatchers() {
		throw new AssertionError();
	}

	private static Matcher<Calendar> forCalendar(Matcher<Date> dateMatcher) {
		return new CalendarMatcherAdapter(dateMatcher);
	}

}
