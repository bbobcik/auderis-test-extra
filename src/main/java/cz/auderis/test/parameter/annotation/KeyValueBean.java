package cz.auderis.test.parameter.annotation;

import cz.auderis.test.parameter.annotation.impl.KeyValueBeanAnnotationConverter;
import junitparams.converters.Param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Param(converter = KeyValueBeanAnnotationConverter.class)
public @interface KeyValueBean {

    Class<?> value();

    Class<?> propertyDelegate() default Void.class;

}
