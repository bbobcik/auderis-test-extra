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

package cz.auderis.test.parameter.convert;

import junitparams.converters.ConversionFailedException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverter extends AbstractTypeConverter<Date> {

	static final Pattern FULL_ISO_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}([T ])\\d{2}:\\d{2}:\\d{2}((?:\\.\\d{3})?)");
	static final Pattern COMPACT_ISO_PATTERN = Pattern.compile("\\d{4}(-?)\\d{2}\\1\\d{2}([T ]?)\\d{2}(:?)\\d{2}\\3\\d{2}");
	static final Pattern DATE_ISO_PATTERN = Pattern.compile("\\d{4}(-?)\\d{2}\\1\\d{2}");

	public DateConverter() {
		super(Date.class);
	}

	@Override
	protected Date fromString(String objText, String option) throws ConversionFailedException {
		if (objText.trim().isEmpty()) {
			return null;
		}
		final DateFormat format = determineDatePattern(objText, option);
		try {
			return format.parse(objText);
		} catch (ParseException e) {
			throw new ConversionFailedException("Cannot parse date '" + objText + "': " + e.getMessage());
		}
	}

	static DateFormat determineDatePattern(String textParam, String options) throws ConversionFailedException {
		final DateFormat format;
		if ((null != options) && !options.trim().isEmpty()) {
			// Use user-specified pattern
			try {
				format = new SimpleDateFormat(options);
			} catch (IllegalArgumentException e) {
				throw new ConversionFailedException(e.getMessage());
			}
		} else {
			format = recognizeStandardDatePattern(textParam);
		}
		return format;
	}

	private static DateFormat recognizeStandardDatePattern(String textParam) throws ConversionFailedException {
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
		throw new ConversionFailedException("Cannot determine date format of '" + textParam + "'");
	}

}
