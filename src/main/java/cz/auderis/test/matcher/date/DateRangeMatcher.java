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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;

import static cz.auderis.test.matcher.date.DateHelper.TIME_UNIT_FIELDS;

class DateRangeMatcher extends TypeSafeMatcher<Date> {

	private final Calendar rangeStart;
	private final Calendar rangeEnd;
	private final int leastTimeUnit;

	DateRangeMatcher(Date start, Date end, int leastTimeUnit, boolean includeStart, boolean includeEnd) {
		super(Date.class);
		if ((null == start) && (null == end)) {
			throw new IllegalArgumentException("at most one range endpoint may be null");
		} else if ((null != start) && (null != end) && end.before(start)) {
			throw new IllegalArgumentException("end date precedes start date");
		} else if (!TIME_UNIT_FIELDS.contains(leastTimeUnit)) {
			throw new IllegalArgumentException("unrecognized time unit");
		}
		this.leastTimeUnit = leastTimeUnit;
		if (null != start) {
			this.rangeStart = Calendar.getInstance();
			rangeStart.setTime(start);
			if (!includeStart) {
				rangeStart.add(leastTimeUnit, 1);
			}
			DateHelper.resetMinorFieldsToMinimum(rangeStart, leastTimeUnit);
		} else {
			this.rangeStart = null;
		}
		if (null != end) {
			this.rangeEnd = Calendar.getInstance();
			rangeEnd.setTime(end);
			if (!includeEnd) {
				rangeEnd.add(leastTimeUnit, -1);
			}
			DateHelper.resetMinorFieldsToMaximum(rangeEnd, leastTimeUnit);
		} else {
			rangeEnd = null;
		}
	}

	@Override
	protected boolean matchesSafely(Date testedDate) {
		final Calendar testCalendar = Calendar.getInstance();
		testCalendar.setTime(testedDate);
		if ((null != rangeStart) && testCalendar.before(rangeStart)) {
			return false;
		} else if ((null != rangeEnd) && testCalendar.after(rangeEnd)) {
			return false;
		}
		return true;
	}

	@Override
	public void describeTo(Description description) {
		final String[] isoDates = DateHelper.formatDates(leastTimeUnit, rangeStart, rangeEnd);
		if (null == rangeEnd) {
			description.appendText("same or later date as ").appendText(isoDates[0]);
		} else if (null == rangeStart) {
			description.appendText("same or earlier date as ").appendText(isoDates[1]);
		} else {
			description.appendText("date between ").appendText(isoDates[0]);
			description.appendText(" and ").appendText(isoDates[1]);
		}
	}

	@Override
	protected void describeMismatchSafely(Date testedDate, Description desc) {
		final String[] isoDates = DateHelper.formatDates(leastTimeUnit, testedDate, rangeStart, rangeEnd);
		desc.appendValue(isoDates[0]);
		final Calendar testCalendar = Calendar.getInstance();
		testCalendar.setTime(testedDate);
		if ((null != rangeStart) && testCalendar.before(rangeStart)) {
			desc.appendText(" is before start date ").appendText(isoDates[1]);
		} else {
			assert (null != rangeEnd) && testCalendar.after(rangeEnd);
			desc.appendText(" is after end date ").appendText(isoDates[2]);
		}
	}

}
