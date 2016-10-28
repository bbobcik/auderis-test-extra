/*
 * Copyright 2016 Boleslav Bobcik - Auderis
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

import cz.auderis.test.support.array.ArrayItemFiller;
import cz.auderis.test.support.array.ArraySequenceParser;
import cz.auderis.test.support.array.MultiArraySupport;
import junitparams.converters.ConversionFailedException;

import java.lang.reflect.Array;
import java.util.Queue;

abstract class AbstractMultiArrayConverter {

    private ArraySequenceParser parser;
    private ArrayItemFiller     itemFiller;

    protected AbstractMultiArrayConverter() {
    }

    protected AbstractMultiArrayConverter(ArraySequenceParser parser, ArrayItemFiller itemFiller) {
        this.parser = parser;
        this.itemFiller = itemFiller;
    }

    protected void setItemFiller(ArrayItemFiller itemFiller) {
        this.itemFiller = itemFiller;
    }

    protected void setItemParser(ArraySequenceParser parser) {
        this.parser = parser;
    }

    public Object convert(Object paramObj) throws ConversionFailedException {
        if (null == parser) {
            throw new IllegalStateException("item parser was not defined");
        } else if (null == itemFiller) {
            throw new IllegalStateException("item filler was not defined");
        }
        final String param = paramObj.toString();
        final MultiArraySupport.ParserResult parsedData = MultiArraySupport.parseArraySpec(param, parser);
        final int[] dimensions = parsedData.dimensions;
        final Object result = Array.newInstance(itemFiller.getItemType(), dimensions);
        fillDimension(result, 0, dimensions, parsedData.items);
        return result;
    }

    protected void fillDimension(Object target, int currentDim, int[] dimensions, Queue<String> items) {
        if (currentDim == dimensions.length - 1) {
            assert target.getClass().isArray();
            final int itemCount = dimensions[currentDim];
            for (int i=0; i<itemCount; ++i) {
                final String itemSpec = items.poll();
                itemFiller.setItem(target, i, itemSpec);
            }
        } else {
            // Descend one dimension lower
            final int subdimensionCount = dimensions[currentDim];
            for (int i=0; i<subdimensionCount; ++i) {
                final Object subdimension = Array.get(target, i);
                assert null != subdimension;
                assert subdimension.getClass().isArray();
                fillDimension(subdimension, 1 + currentDim, dimensions, items);
            }
        }
    }

}
