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

import cz.auderis.test.parameter.annotation.MultiArray;
import cz.auderis.test.support.array.ArraySequenceParser;
import cz.auderis.test.support.array.BasicArraySequenceParsers;
import cz.auderis.test.support.array.BuiltInItemSupport;
import cz.auderis.test.support.array.PrimitiveArrayItemSupport;
import cz.auderis.test.support.array.PropertyEditorArrayItemFiller;
import junitparams.converters.Converter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class MultiArrayConverter extends AbstractMultiArrayConverter implements Converter<MultiArray, Object> {

    @Override
    public void initialize(MultiArray annotation) {
        final Class<?> itemType = annotation.value();
        assignEditor(itemType);
    }

    private void assignEditor(Class<?> itemType) {
        // Handle primitive item types
        final PrimitiveArrayItemSupport primitiveHandler = PrimitiveArrayItemSupport.forType(itemType);
        if (null != primitiveHandler) {
            assert itemType.isPrimitive();
            setItemParser(primitiveHandler);
            setItemFiller(primitiveHandler);
            return;
        }
        // Handle types that have built-in support
        final BuiltInItemSupport builtInSupport = BuiltInItemSupport.forType(itemType);
        if (null != builtInSupport) {
            setItemParser(builtInSupport);
            setItemFiller(builtInSupport);
            return;
        }
        // For boxed variants of primitive types, use the same item parser as for the corresponding primitive type
        ArraySequenceParser parser = PrimitiveArrayItemSupport.forBoxedType(itemType);
        if (null == parser) {
            parser = BasicArraySequenceParsers.TOKENS;
        }
        // Use appropriate Bean property editor
        final PropertyEditor editor = PropertyEditorManager.findEditor(itemType);
        if (null != editor) {
            final PropertyEditorArrayItemFiller editorFiller = new PropertyEditorArrayItemFiller(editor, itemType);
            setItemParser(parser);
            setItemFiller(editorFiller);
            return;
        }
        // No success
        throw new IllegalArgumentException("Cannot create array with item type " + itemType);
    }

}
