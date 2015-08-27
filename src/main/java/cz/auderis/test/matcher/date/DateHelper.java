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

import cz.auderis.test.parameter.convert.DateConverter;
import junitparams.converters.ConversionFailedException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class DateHelper {

	static final String DATE_ISO_FORMAT = "yyyy-MM-dd";
	static final String FULL_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS";

	static final List<Integer> TIME_UNIT_FIELDS;
	static final Map<Integer, String> DATE_FIELDS;
	static final Map<Integer, String> TIME_FIELDS;

	static {
		final List<Integer> fieldList = Arrays.asList(
				Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
				Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND
		);
		TIME_UNIT_FIELDS = Collections.unmodifiableList(fieldList);
		//
		final Map<Integer, String> dateMap = new LinkedHashMap<Integer, String>(3);
		dateMap.put(Calendar.YEAR, "year");
		dateMap.put(Calendar.MONTH, "month");
		dateMap.put(Calendar.DAY_OF_MONTH, "day");
		DATE_FIELDS = Collections.unmodifiableMap(dateMap);
		//
		final Map<Integer, String> timeMap = new LinkedHashMap<Integer, String>(4);
		timeMap.put(Calendar.HOUR_OF_DAY, "hour");
		timeMap.put(Calendar.MINUTE, "minute");
		timeMap.put(Calendar.SECOND, "second");
		timeMap.put(Calendar.MILLISECOND, "millisecond");
		TIME_FIELDS = Collections.unmodifiableMap(timeMap);
	}

	static String formatDate(int leastFieldUnit, Date d) {
		assert (null != d);
		final DateFormat format;
		if (DATE_FIELDS.containsKey(leastFieldUnit)) {
			format = new SimpleDateFormat(DATE_ISO_FORMAT);
		} else {
			assert TIME_FIELDS.containsKey(leastFieldUnit);
			format = new SimpleDateFormat(FULL_ISO_FORMAT);
		}
		return format.format(d);
	}

	static String[] formatDates(int leastFieldUnit, Object... dates) {
		assert (null != dates) && (dates.length > 0);
		final DateFormat format;
		if (DATE_FIELDS.containsKey(leastFieldUnit)) {
			format = new SimpleDateFormat(DATE_ISO_FORMAT);
		} else {
			assert TIME_FIELDS.containsKey(leastFieldUnit);
			format = new SimpleDateFormat(FULL_ISO_FORMAT);
		}
		final String[] result = new String[dates.length];
		for (int i=0; i<dates.length; ++i) {
			final Object arg = dates[i];
			if (null == arg) {
				result[i] = null;
				continue;
			}
			final Date date;
			if (arg instanceof Date) {
				date = (Date) arg;
			} else if (arg instanceof Calendar) {
				date = ((Calendar) arg).getTime();
			} else {
				throw new IllegalArgumentException("bad argument " + i);
			}
			result[i] = format.format(date);
		}
		return result;
	}

	static String getUnitName(int unitField) {
		if (DATE_FIELDS.containsKey(unitField)) {
			return DATE_FIELDS.get(unitField);
		} else if (TIME_FIELDS.containsKey(unitField)) {
			return TIME_FIELDS.get(unitField);
		}
		throw new IllegalArgumentException("unsupported calendar field");
	}

	static Date parseDateAllowingNull(String dateText) {
		if ((null == dateText) || dateText.trim().isEmpty()) {
			return null;
		}
		return parseDate(dateText);
	}

	static Date parseDate(String dateText) {
		if (null == dateText) {
			throw new NullPointerException();
		}
		final DateConverter parser = new DateConverter();
		try {
			return parser.convert(dateText, null);
		} catch (ConversionFailedException e) {
			// Not a format supported by DateConverter, fall back to default patterns
		}
		try {
			final DateFormat defaultDateTimeFormat = DateFormat.getDateTimeInstance();
			return defaultDateTimeFormat.parse(dateText);
		} catch (ParseException e) {
			// Failed, try next
		}
		try {
			final DateFormat defaultFormat = DateFormat.getInstance();
			return defaultFormat.parse(dateText);
		} catch (ParseException e) {
			// Failed, report error
		}
		throw new IllegalArgumentException("Failed to parse date '" + dateText + "'");
	}

	static List<Integer> majorFieldUnits(int leastMajorUnit) {
		final int leastUnitIndex = TIME_UNIT_FIELDS.indexOf(leastMajorUnit);
		assert leastUnitIndex >= 0;
		final List<Integer> majorFields = TIME_UNIT_FIELDS.subList(0, leastUnitIndex + 1);
		return majorFields;
	}

	static List<Integer> minorFieldUnits(int leastMajorUnit) {
		final int leastUnitIndex = TIME_UNIT_FIELDS.indexOf(leastMajorUnit);
		assert leastUnitIndex >= 0;
		final int allFieldCount = TIME_UNIT_FIELDS.size();
		if (leastUnitIndex == allFieldCount - 1) {
			return Collections.emptyList();
		}
		final List<Integer> minorFields = TIME_UNIT_FIELDS.subList(leastUnitIndex + 1, allFieldCount);
		return minorFields;
	}

	static void setMinorFieldValues(Calendar targetCal, int... fieldValues) {
		final int allFieldCount = TIME_UNIT_FIELDS.size();
		assert (0 < fieldValues.length) && (fieldValues.length <= allFieldCount);
		final Iterator<Integer> fieldIterator = TIME_UNIT_FIELDS.listIterator(allFieldCount - fieldValues.length);
		for (final int fieldValue : fieldValues) {
			assert fieldIterator.hasNext();
			final int minorField = fieldIterator.next();
			targetCal.set(minorField, fieldValue);
		}
	}

	static void resetMinorFieldsToMinimum(Calendar targetCal, int leastMajorUnit) {
		for (final Integer minorUnit : minorFieldUnits(leastMajorUnit)) {
			final int minValue = targetCal.getActualMinimum(minorUnit);
			targetCal.set(minorUnit, minValue);
		}
	}

	static void resetMinorFieldsToMaximum(Calendar targetCal, int leastMajorUnit) {
		for (final Integer minorUnit : minorFieldUnits(leastMajorUnit)) {
			final int maxValue = targetCal.getActualMaximum(minorUnit);
			targetCal.set(minorUnit, maxValue);
		}
	}

}
