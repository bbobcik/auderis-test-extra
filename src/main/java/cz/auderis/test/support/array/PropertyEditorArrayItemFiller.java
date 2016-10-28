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

package cz.auderis.test.support.array;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;

public class PropertyEditorArrayItemFiller implements ArrayItemFiller {

    private final PropertyEditor editor;
    private final Class<?> targetClass;

    public PropertyEditorArrayItemFiller(PropertyEditor editor, Class<?> targetClass) {
        assert null != editor;
        assert null != targetClass;
        this.editor = editor;
        this.targetClass = targetClass;
    }

    @Override
    public Class<?> getItemType() {
        return targetClass;
    }

    @Override
    public void setItem(Object target, int index, String itemSpec) {
        editor.setAsText(itemSpec);
        final Object itemValue = editor.getValue();
        Array.set(target, index, itemValue);
    }

}
