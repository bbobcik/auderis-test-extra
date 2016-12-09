package cz.auderis.test.parameter.annotation;

import cz.auderis.test.parameter.annotation.impl.UsingFactoryAnnotationConverter;
import junitparams.converters.Param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Param(converter = UsingFactoryAnnotationConverter.class)
public @interface UsingFactory {

    Class<?> type();

    String method() default "valueOf";

    Class<?> targetType() default Void.class;

}
