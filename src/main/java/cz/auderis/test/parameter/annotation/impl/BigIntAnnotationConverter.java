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

import java.math.BigInteger;

public class BigIntAnnotationConverter implements Converter<BigDec, BigInteger> {

    private String nullToken;

    @Override
    public void initialize(BigDec annotation) {
        nullToken = annotation.nullToken();
    }

    @Override
    public BigInteger convert(Object param) throws ConversionFailedException {
        if ((null == param) || (param instanceof BigInteger)) {
            return (BigInteger) param;
        }
        final String textParam = param.toString();
        if ((null != nullToken) && nullToken.equals(textParam)) {
            return null;
        }
        try {
            final BigInteger result = new BigInteger(textParam);
            return result;
        } catch (NumberFormatException e) {
            final ConversionFailedException e2 = new ConversionFailedException("Failed to convert value '" + param + "' to BigInteger");
            e2.initCause(e);
            throw e2;
        }
    }

}
