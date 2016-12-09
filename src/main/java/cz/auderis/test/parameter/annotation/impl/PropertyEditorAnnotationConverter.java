package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.parameter.annotation.UsingPropertyEditor;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * Created by bbobcik on 12/8/16.
 */
public class PropertyEditorAnnotationConverter implements Converter<UsingPropertyEditor, Object> {

    Class<?> targetType;
    PropertyEditor editor;

    @Override
    public void initialize(UsingPropertyEditor annotation) {
        final Class<?> targetType = optionalClass(annotation.of());
        final Class<?> editorClass = optionalClass(annotation.editor());
        if ((null == targetType) && (null == editorClass)) {
            throw new IllegalArgumentException("Property editor not specified");
        } else if (null == editorClass) {
            this.editor = PropertyEditorManager.findEditor(targetType);
            if (null == editor) {
                throw new IllegalArgumentException("Property editor for type " + targetType + " not found");
            }
        } else {
            if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
                throw new IllegalArgumentException("Invalid property editor type: " + editorClass);
            }
            try {
                final Object editorObject = editorClass.newInstance();
                this.editor = (PropertyEditor) editorObject;
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot create property editor instance: " + editorClass, e);
            }
        }
        this.targetType = targetType;
    }

    @Override
    public Object convert(Object param) throws ConversionFailedException {
        final String paramString;
        if (null == param) {
            paramString = null;
        } else if (param instanceof String) {
            paramString = (String) param;
        } else {
            paramString = param.toString();
        }
        try {
            editor.setAsText(paramString);
            final Object result = editor.getValue();
            return result;
        } catch (Exception e) {
            final ConversionFailedException e2 = new ConversionFailedException(
                    "Cannot convert value '" + param + "' using " + editor.toString()
            );
            e2.initCause(e);
            throw e2;
        }
    }

    private static Class<?> optionalClass(Class<?> cls) {
        if ((null == cls) || (cls == Void.class)) {
            return null;
        }
        return cls;
    }

}
