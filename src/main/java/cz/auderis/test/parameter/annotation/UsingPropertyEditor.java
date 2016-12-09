package cz.auderis.test.parameter.annotation;

import cz.auderis.test.parameter.annotation.impl.PropertyEditorAnnotationConverter;
import junitparams.converters.Param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bbobcik on 12/8/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Param(converter = PropertyEditorAnnotationConverter.class)
public @interface UsingPropertyEditor {

    Class<?> of() default Void.class;

    Class<?> editor() default Void.class;

}
