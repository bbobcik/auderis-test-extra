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

import cz.auderis.test.matcher.date.DateHelper;
import junitparams.converters.ConversionFailedException;

import java.util.Date;

public class DateConverter extends AbstractTypeConverter<Date> {

	public DateConverter() {
		super(Date.class);
	}

	@Override
	protected Date fromString(String objText, String option) throws ConversionFailedException {
		try {
			return DateHelper.parseDateAllowingNull(objText, option);
		} catch (IllegalArgumentException e) {
			throw new ConversionFailedException("Cannot parse date '" + objText + "': " + e.getMessage());
		}
	}

}
