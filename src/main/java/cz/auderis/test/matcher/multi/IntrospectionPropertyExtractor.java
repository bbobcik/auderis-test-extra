package cz.auderis.test.matcher.multi;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IntrospectionPropertyExtractor<T> implements PropertyExtractor<T, Object> {

    final ExtractorImpl extractor;

    public IntrospectionPropertyExtractor(Class<T> objectClass, String propertyName) {
        if ((null == objectClass) || (null == propertyName)) {
            throw new NullPointerException();
        }
        this.extractor = findExtractor(objectClass, propertyName);
    }

    @Override
    public Object extract(T obj) {
        return extractor.extract(obj);
    }

    private static ExtractorImpl findExtractor(Class<?> cls, String propertyName) {
        ExtractorImpl result = getBeanPropertyExtractor(cls, propertyName);
        if (null != result) return result;
        result = getGetterExtractor(cls, propertyName);
        if (null != result) return result;
        result = getFieldExtractor(cls, propertyName);
        if (null != result) return result;
        //
        throw new IllegalArgumentException("Cannot find property '" + propertyName + "': " + cls.getName());
    }

    private static ExtractorImpl getBeanPropertyExtractor(Class<?> cls, String propertyName) {
        final PropertyDescriptor sourceProperty;
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(cls);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (null == propertyDescriptors) {
                return null;
            }
            if (propertyName.isEmpty()) {
                final int defaultPropertyIndex = beanInfo.getDefaultPropertyIndex();
                sourceProperty = propertyDescriptors[defaultPropertyIndex];
            } else {
                PropertyDescriptor foundProperty = null;
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyName.equals(propertyDescriptor.getName())) {
                        foundProperty = propertyDescriptor;
                        break;
                    }
                }
                sourceProperty = foundProperty;
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException("Cannot examine class " + cls.getName() + ": property '" + propertyName + '\'', e);
        }
        final Method readMethod = (null != sourceProperty) ? sourceProperty.getReadMethod() : null;
        if (null == readMethod) {
            return null;
        }
        return new BeanPropertyExtractor(propertyName, readMethod);
    }

    private static ExtractorImpl getGetterExtractor(Class<?> cls, String propertyName) {
        final String capitalizedPropertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        final String getterName = "get" + capitalizedPropertyName;
        final String altGetterName = "is" + capitalizedPropertyName;
        Method foundGetter = null;
        Class<?> examinedClass = cls;
        CLASS_SCAN:
        while (null != examinedClass) {
            final Method[] declaredMethods = examinedClass.getDeclaredMethods();
            METHOD_SCAN:
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isSynthetic()) {
                    continue METHOD_SCAN;
                }
                final Class<?> returnType = declaredMethod.getReturnType();
                if ((void.class == returnType) || (Void.class == returnType)) {
                    continue METHOD_SCAN;
                }
                final Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if (0 != parameterTypes.length) {
                    continue METHOD_SCAN;
                }
                final String methodName = declaredMethod.getName();
                if (getterName.equals(methodName) || altGetterName.equals(methodName)) {
                    foundGetter = declaredMethod;
                    break CLASS_SCAN;
                }
            }
            examinedClass = examinedClass.getSuperclass();
        }
        if (null == foundGetter) {
            return null;
        }
        return new BeanPropertyExtractor(propertyName, foundGetter);
    }

    private static ExtractorImpl getFieldExtractor(Class<?> cls, String propertyName) {
        Field foundField = null;
        Class<?> examinedClass = cls;
        CLASS_SCAN:
        while (null != examinedClass) {
            final Field[] declaredFields = examinedClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                final String fieldName = declaredField.getName();
                if (propertyName.equals(fieldName)) {
                    foundField = declaredField;
                    break CLASS_SCAN;
                }
            }
            examinedClass = examinedClass.getSuperclass();
        }
        if (null == foundField) {
            return null;
        }
        return new FieldExtractor(foundField);
    }



    private interface ExtractorImpl {
        Object extract(Object instance);
    }

    private static final class BeanPropertyExtractor implements ExtractorImpl {
        final String name;
        final Method propertyGetter;

        private BeanPropertyExtractor(String name, Method propertyGetter) {
            this.name = name;
            this.propertyGetter = propertyGetter;
        }

        @Override
        public Object extract(Object instance) {
            final boolean origAccessible = propertyGetter.isAccessible();
            try {
                propertyGetter.setAccessible(true);
                return propertyGetter.invoke(instance);
            } catch (Exception e) {
                throw new RuntimeException("Cannot access property '" + name + '\'', e);
            } finally {
                propertyGetter.setAccessible(origAccessible);
            }
        }
    }

    private static final class FieldExtractor implements ExtractorImpl {
        final Field field;

        private FieldExtractor(Field field) {
            this.field = field;
        }

        @Override
        public Object extract(Object instance) {
            final boolean origAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                return field.get(instance);
            } catch (Exception e) {
                throw new RuntimeException("Cannot access field '" + field.getName() + '\'', e);
            } finally {
                field.setAccessible(origAccessible);
            }
        }
    }

}
