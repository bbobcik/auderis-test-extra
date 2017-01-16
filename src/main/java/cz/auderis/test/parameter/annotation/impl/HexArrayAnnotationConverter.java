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

import cz.auderis.test.parameter.annotation.HexArray;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;

import java.util.Arrays;

public class HexArrayAnnotationConverter implements Converter<HexArray, byte[]> {

    private int size;

    @Override
    public void initialize(HexArray annotation) {
        size = annotation.size();
    }

    @Override
    public byte[] convert(Object param) throws ConversionFailedException {
        final HexChunkParser parser = new HexChunkParser();
        final byte[] baseArray = parser.parseByteArray(param);
        final byte[] result;
        if ((size <= 0) || (size == baseArray.length)) {
            result = baseArray;
        } else {
            result = Arrays.copyOf(baseArray, size);
        }
        return result;
    }

}
