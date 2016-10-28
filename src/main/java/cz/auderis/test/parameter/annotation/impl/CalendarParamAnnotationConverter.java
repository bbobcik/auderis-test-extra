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

package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.matcher.date.DateHelper;
import cz.auderis.test.parameter.annotation.CalendarParam;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;

import java.util.Calendar;
import java.util.Date;

public class CalendarParamAnnotationConverter implements Converter<CalendarParam, Calendar> {

    private String format;

    public CalendarParamAnnotationConverter() {
        format = "";
    }

    @Override
    public void initialize(CalendarParam annotation) {
        format = annotation.format();
    }

    @Override
    public Calendar convert(Object param) throws ConversionFailedException {
        if (param instanceof Calendar) {
            return (Calendar) param;
        }
        final Date date;
        if (param instanceof Date) {
            date = (Date) param;
        } else {
            try {
                date = DateHelper.parseDateAllowingNull(param.toString(), format);
            } catch (Exception e) {
                final ConversionFailedException e2 = new ConversionFailedException("Cannot convert '" + param + "' to date");
                e2.initCause(e);
                throw e2;
            }
        }
        final Calendar result = Calendar.getInstance();
        result.setTime(date);
        return result;
    }

}
