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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateHelper {

	static final String DATE_ISO_FORMAT = "yyyy-MM-dd";
	static final String FULL_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS";

	static final Pattern FULL_ISO_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}([T ])\\d{2}:\\d{2}:\\d{2}((?:\\.\\d{3})?)");
	static final Pattern COMPACT_ISO_PATTERN = Pattern.compile("\\d{4}(-?)\\d{2}\\1\\d{2}([T ]?)\\d{2}(:?)\\d{2}\\3\\d{2}");
	static final Pattern DATE_ISO_PATTERN = Pattern.compile("\\d{4}(-?)\\d{2}\\1\\d{2}");

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

	public static String formatDate(int leastFieldUnit, Date d) {
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

	public static String[] formatDates(int leastFieldUnit, Object... dates) {
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

	public static String getUnitName(int unitField) {
		if (DATE_FIELDS.containsKey(unitField)) {
			return DATE_FIELDS.get(unitField);
		} else if (TIME_FIELDS.containsKey(unitField)) {
			return TIME_FIELDS.get(unitField);
		}
		throw new IllegalArgumentException("unsupported calendar field");
	}

	public static Date parseDateAllowingNull(String dateText) {
		return parseDateAllowingNull(dateText, null);
	}

	public static Date parseDateAllowingNull(String dateText, String optionalCustomFormat) {
		if ((null == dateText) || dateText.trim().isEmpty()) {
			return null;
		}
		return parseDate(dateText, optionalCustomFormat);
	}

	public static Date parseDate(String dateText) {
		return parseDate(dateText, null);
	}

	public static Date parseDate(String dateText, String optionalCustomFormat) {
		if (null == dateText) {
			throw new NullPointerException();
		} else if (dateText.trim().isEmpty()) {
			return null;
		}
		final DateFormat format = determineDatePattern(dateText, optionalCustomFormat);
		try {
			return format.parse(dateText);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Cannot parse date '" + dateText + "'", e);
		}
	}

	public static List<Integer> majorFieldUnits(int leastMajorUnit) {
		final int leastUnitIndex = TIME_UNIT_FIELDS.indexOf(leastMajorUnit);
		assert leastUnitIndex >= 0;
		final List<Integer> majorFields = TIME_UNIT_FIELDS.subList(0, leastUnitIndex + 1);
		return majorFields;
	}

	public static List<Integer> minorFieldUnits(int leastMajorUnit) {
		final int leastUnitIndex = TIME_UNIT_FIELDS.indexOf(leastMajorUnit);
		assert leastUnitIndex >= 0;
		final int allFieldCount = TIME_UNIT_FIELDS.size();
		if (leastUnitIndex == allFieldCount - 1) {
			return Collections.emptyList();
		}
		final List<Integer> minorFields = TIME_UNIT_FIELDS.subList(leastUnitIndex + 1, allFieldCount);
		return minorFields;
	}

	public static void setMinorFieldValues(Calendar targetCal, int... fieldValues) {
		final int allFieldCount = TIME_UNIT_FIELDS.size();
		assert (0 < fieldValues.length) && (fieldValues.length <= allFieldCount);
		final Iterator<Integer> fieldIterator = TIME_UNIT_FIELDS.listIterator(allFieldCount - fieldValues.length);
		for (final int fieldValue : fieldValues) {
			assert fieldIterator.hasNext();
			final int minorField = fieldIterator.next();
			targetCal.set(minorField, fieldValue);
		}
	}

	public static void resetMinorFieldsToMinimum(Calendar targetCal, int leastMajorUnit) {
		for (final Integer minorUnit : minorFieldUnits(leastMajorUnit)) {
			final int minValue = targetCal.getActualMinimum(minorUnit);
			targetCal.set(minorUnit, minValue);
		}
	}

	public static void resetMinorFieldsToMaximum(Calendar targetCal, int leastMajorUnit) {
		for (final Integer minorUnit : minorFieldUnits(leastMajorUnit)) {
			final int maxValue = targetCal.getActualMaximum(minorUnit);
			targetCal.set(minorUnit, maxValue);
		}
	}

	public static DateFormat determineDatePattern(String textParam, String options) {
		final DateFormat format;
		if ((null != options) && !options.trim().isEmpty()) {
			// Use user-specified pattern
			format = new SimpleDateFormat(options);
		} else {
			format = recognizeStandardDatePattern(textParam);
		}
		return format;
	}

	private static DateFormat recognizeStandardDatePattern(String textParam) {
		final StringBuilder formatBuilder = new StringBuilder(32);
		final Matcher matcher = FULL_ISO_PATTERN.matcher(textParam);
		if (matcher.matches()) {
			formatBuilder.append("yyyy-MM-dd");
			final String dateTimeSeparator = matcher.group(1);
			formatBuilder.append('\'').append(dateTimeSeparator).append('\'');
			formatBuilder.append("HH:mm:ss");
			final String millisPart = matcher.group(2);
			if ((null != millisPart) && !millisPart.isEmpty()) {
				formatBuilder.append(".SSS");
			}
			return new SimpleDateFormat(formatBuilder.toString());
		}
		//
		matcher.usePattern(COMPACT_ISO_PATTERN).reset();
		if (matcher.matches()) {
			final String datePartSeparator = matcher.group(1);
			formatBuilder.append("yyyy");
			formatBuilder.append(datePartSeparator).append("MM");
			formatBuilder.append(datePartSeparator).append("dd");
			final String dateTimeSeparator = matcher.group(2);
			if (!dateTimeSeparator.isEmpty()) {
				formatBuilder.append('\'').append(dateTimeSeparator).append('\'');
			}
			final String timePartSeparator = matcher.group(3);
			formatBuilder.append("HH");
			formatBuilder.append(timePartSeparator).append("mm");
			formatBuilder.append(timePartSeparator).append("ss");
			return new SimpleDateFormat(formatBuilder.toString());
		}
		//
		matcher.usePattern(DATE_ISO_PATTERN).reset();
		if (matcher.matches()) {
			final String datePartSeparator = matcher.group(1);
			formatBuilder.append("yyyy");
			formatBuilder.append(datePartSeparator).append("MM");
			formatBuilder.append(datePartSeparator).append("dd");
			return new SimpleDateFormat(formatBuilder.toString());
		}
		//
		throw new IllegalArgumentException("Cannot determine date format of '" + textParam + "'");
	}

	private DateHelper() {
		throw new AssertionError();
	}

}
