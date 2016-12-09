package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.parameter.annotation.UsingFactory;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class UsingFactoryAnnotationConverter implements Converter<UsingFactory, Object> {

    Class<?> targetType;
    Method factoryMethod;

    @Override
    public void initialize(UsingFactory annotation) {
        final Class<?> factoryClass = annotation.type();
        final String methodName = annotation.method();
        final Class<?> targetClass = PropertyEditorAnnotationConverter.optionalClass(annotation.targetType());
        final Set<Method> candidateMethods = new HashSet<>(4);
        for (final Method method : factoryClass.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) || !methodName.equals(method.getName())) {
                continue;
            }
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if ((1 != parameterTypes.length) || !parameterTypes[0].isAssignableFrom(String.class)) {
                continue;
            }
            if ((null == targetClass) || targetClass.isAssignableFrom(method.getReturnType())) {
                candidateMethods.add(method);
            }
        }
        if (candidateMethods.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot find factory method '" + methodName + "' in class " + factoryClass
            );
        } else if (candidateMethods.size() > 1) {
            throw new IllegalArgumentException(
                    "Ambiguous factory methods '" + methodName + "' in class " + factoryClass
            );
        }
        factoryMethod = candidateMethods.iterator().next();
        targetType = targetClass;
    }

    @Override
    public Object convert(Object param) throws ConversionFailedException {
        final Object result;
        try {
            final String paramString = PropertyEditorAnnotationConverter.asString(param);
            result = factoryMethod.invoke(null, paramString);
        } catch (Exception e) {
            throw new ConversionFailedException(
                    "Cannot convert value '" + param + "' using factory method " + factoryMethod
            );
        }
        if (null != targetType) {
            if ((null == result) && targetType.isPrimitive()) {
                throw new ConversionFailedException(
                        "Bad conversion result of value '" + param + "' using factory method " + factoryMethod
                        + ": cannot use null for primitive target type " + targetType
                );
            }
            if ((null != result) && !targetType.isAssignableFrom(result.getClass())) {
                throw new ConversionFailedException(
                        "Bad conversion result of value '" + param + "' using factory method " + factoryMethod
                                + ": cannot assign " + result.getClass() + " into " + targetType
                );
            }
        }
        return result;
    }

}
