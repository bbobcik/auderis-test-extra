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
import java.util.Calendar;
import java.util.Date;

public class CalendarConverter extends AbstractTypeConverter<Calendar> {

	public CalendarConverter() {
		super(Calendar.class);
	}

	@Override
	protected Calendar fromString(String objText, String option) throws ConversionFailedException {
		if (objText.trim().isEmpty()) {
			return null;
		}
		final DateFormat format = DateConverter.determineDatePattern(objText, option);
		try {
			final Date date = format.parse(objText);
			final Calendar result = Calendar.getInstance();
			result.setTime(date);
			return result;
		} catch (ParseException e) {
			throw new ConversionFailedException("Cannot parse date '" + objText + "': " + e.getMessage());
		}
	}

}
