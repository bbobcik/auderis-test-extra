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

import cz.auderis.test.parameter.annotation.BigDec;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;

import java.math.BigDecimal;

public class BigDecAnnotationConverter implements Converter<BigDec, BigDecimal> {

    private String nullToken;

    @Override
    public void initialize(BigDec annotation) {
        nullToken = annotation.nullToken();
    }

    @Override
    public BigDecimal convert(Object param) throws ConversionFailedException {
        if ((null == param) || (param instanceof BigDecimal)) {
            return (BigDecimal) param;
        }
        final String textParam = param.toString();
        if ((null != nullToken) && nullToken.equals(textParam)) {
            return null;
        }
        try {
            final BigDecimal result = new BigDecimal(textParam);
            return result;
        } catch (NumberFormatException e) {
            final ConversionFailedException e2 = new ConversionFailedException("Failed to convert value '" + param + "' to BigDecimal");
            e2.initCause(e);
            throw e2;
        }
    }

}
