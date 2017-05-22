package cz.auderis.test.parameter.annotation.impl;

import junitparams.converters.ConversionFailedException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public abstract class AbstractKeyValueConverter {

    protected static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?:^\\s*|\\G\\s+)(\\w+)\\s*=\\s*(?:((?!')\\S*)|'((?:[^'\\\\]|\\\\.)*+)(?<!\\\\)')");

    protected Class<?> beanClass;
    protected Class<?> propertyDelegateClass;
    private Object propertyDelegate;

    protected AbstractKeyValueConverter() {
    }

    protected AbstractKeyValueConverter(Class<?> beanClass, Class<?> propertyDelegateClass) {
        setTypes(beanClass, propertyDelegateClass);
    }

    protected void setTypes(Class<?> beanClass, Class<?> propertyDelegateClass) {
        if (null == beanClass) {
            throw new NullPointerException();
        }
        this.beanClass = beanClass;
        this.propertyDelegateClass = propertyDelegateClass;
        initializePropertyDelegate();
    }

    protected void initializePropertyDelegate() {
        if ((null == propertyDelegateClass) || (Void.class == propertyDelegateClass) || (void.class == propertyDelegateClass)) {
            this.propertyDelegateClass = null;
            this.propertyDelegate = null;
        } else {
            try {
                this.propertyDelegate = propertyDelegateClass.newInstance();
            } catch (IllegalAccessException e) {
                // We assume that the delegate class contains static methods
                this.propertyDelegate = null;
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot create property delegate instance for " + beanClass + ": " + propertyDelegateClass, e);
            }
        }
    }

    public Object convert(Object param) throws ConversionFailedException {
        if ((null == param) || beanClass.isAssignableFrom(param.getClass())) {
            return param;
        }
        final List<KeyValue> keyValues = parseKeyValues(param);
        final Object result;
        try {
            result = beanClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create instance of a bean: " + beanClass, e);
        }
        applyProperties(result, keyValues);
        return result;
    }

    void applyProperties(Object target, List<KeyValue> keyValues) {
        for (KeyValue keyValue : keyValues) {
            final String propertyName = keyValue.key;
            final Setter propertySetter = resolvePropertySetter(propertyName);
            if (null != propertySetter) {
                final Class<?> setterArgumentType = propertySetter.valueParameterType;
                final Object setterArgument = adaptArgumentToType(keyValue.value, setterArgumentType);
                try {
                    propertySetter.method.setAccessible(true);
                    if (propertySetter.applyOnDelegate) {
                        propertySetter.method.invoke(propertyDelegate, target, setterArgument);
                    } else {
                        propertySetter.method.invoke(target, setterArgument);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot set property '" + propertyName + "' on " + beanClass + ": " + setterArgument, e);
                } finally {
                    propertySetter.restoreAccess();
                }
            } else {
                // Fallback: try injecting into the field
                final Field propertyField = findField(propertyName, beanClass);
                if (null == propertyField) {
                    throw new IllegalArgumentException("Setter for property '" + propertyName + "' not found: " + beanClass);
                }
                final Class<?> fieldType = propertyField.getType();
                final Object fieldValue = adaptArgumentToType(keyValue.value, fieldType);
                final boolean origAccess = propertyField.isAccessible();
                try {
                    propertyField.setAccessible(true);
                    propertyField.set(target, fieldValue);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Cannot inject field '" + propertyName + "' in " + beanClass + ": " + fieldValue, e);
                } finally {
                    propertyField.setAccessible(origAccess);
                }
            }
        }
    }

    static Object adaptArgumentToType(String value, Class<?> targetType) {
        if (String.class == targetType) {
            return value;
        }
        // Try static valueOf() method
        try {
            final Method valueOfMethod = targetType.getMethod("valueOf", String.class);
            final Class<?> returnType = valueOfMethod.getReturnType();
            if (Modifier.isStatic(valueOfMethod.getModifiers()) && (returnType == targetType)) {
                final Object valueOfResult = valueOfMethod.invoke(null, value);
                return valueOfResult;
            }
        } catch (Exception e) {
            // Either there is no valueOf() method or it failed to process the argument, continue
        }
        // Try using JavaBeans property editor approach
        final PropertyEditor propertyEditor = PropertyEditorManager.findEditor(targetType);
        if (null == propertyEditor) {
            throw new IllegalStateException("No property editor defined: " + targetType);
        }
        propertyEditor.setAsText(value);
        final Object setterArgument = propertyEditor.getValue();
        return setterArgument;
    }

    Setter resolvePropertySetter(String propertyName) {
        final String capitalizedPropertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        // Resolve getter and infer property type
        final String getterName = "get" + capitalizedPropertyName;
        final Method delegateGetter = findMethod(getterName, propertyDelegateClass, beanClass);
        final Class<?> propertyType;
        if (null != delegateGetter) {
            propertyType = delegateGetter.getReturnType();
        } else {
            final Method directGetter = findMethod(getterName, beanClass);
            if (null != directGetter) {
                propertyType = directGetter.getReturnType();
            } else {
                propertyType = Void.class;
            }
        }
        // Look up setter: from delegate with proper argument type
        final String setterName = "set" + capitalizedPropertyName;
        if (Void.class != propertyType) {
            final Method delegateSetter = findMethod(setterName, propertyDelegateClass, beanClass, propertyType);
            if (null != delegateSetter) {
                return new Setter(delegateSetter, true, propertyType);
            }
        }
        // Fallback 1: from delegate, but with string type argument
        final Method delegateStringSetter = findMethod(setterName, propertyDelegateClass, beanClass, String.class);
        if (null != delegateStringSetter) {
            return new Setter(delegateStringSetter, true, String.class);
        }
        // Fallback 2: normal setter from the bean itself (with proper argument type)
        if (Void.class != propertyType) {
            final Method directSetter = findMethod(setterName, beanClass, propertyType);
            if (null != directSetter) {
                return new Setter(directSetter, false, propertyType);
            }
        }
        // No setter was found
        return null;
    }

    static Method findMethod(String methodName, Class<?> startClass, Class<?>... argumentTypes) {
        Class<?> cls = startClass;
        while (null != cls) {
            try {
                final Method method = cls.getDeclaredMethod(methodName, argumentTypes);
                return method;
            } catch (NoSuchMethodException e) {
                // No match, continue
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    static Field findField(String fieldName, Class<?> startClass) {
        Class<?> cls = startClass;
        while (null != cls) {
            try {
                final Field field = cls.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                // No match, continue
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    static List<KeyValue> parseKeyValues(Object param) {
        final String beanSpec = param.toString();
        //
        final List<KeyValue> recordList = new LinkedList<>();
        final Map<String, KeyValue> recordsByKey = new HashMap<>(16);
        final Matcher beanSpecMatcher = KEY_VALUE_PATTERN.matcher(beanSpec);
        while (beanSpecMatcher.find()) {
            final String key = beanSpecMatcher.group(1).intern();
            String value = beanSpecMatcher.group(3);
            if (null == value) {
                value = beanSpecMatcher.group(2);
                if ("<null>".equalsIgnoreCase(value)) {
                    value = null;
                }
            } else {
                value = unescapeValue(value);
            }
            KeyValue keyValue = recordsByKey.get(key);
            if (null == keyValue) {
                keyValue = new KeyValue(key);
                recordList.add(keyValue);
                recordsByKey.put(key, keyValue);
            }
            keyValue.value = value;
        }
        return recordList;
    }

    static String unescapeValue(String value) {
        final int length = value.length();
        final StringBuilder str = new StringBuilder(length);
        int lastIndex = 0;
        int index;
        while (-1 != (index = value.indexOf('\\', lastIndex))) {
            str.append(value, lastIndex, index);
            final int escapedCharIndex = Math.min(index + 1, length - 1);
            str.append(value.charAt(escapedCharIndex));
            lastIndex = escapedCharIndex + 1;
        }
        if (lastIndex < length) {
            str.append(value, lastIndex, length);
        }
        return str.toString();
    }

    static final class KeyValue {
        final String key;
        String value;

        KeyValue(String key) {
            this.key = key;
        }
    }

    static final class Setter {
        final Method method;
        final boolean applyOnDelegate;
        final Class<?> valueParameterType;
        final boolean originallyAccessible;

        Setter(Method method, boolean applyOnDelegate, Class<?> valueParameterType) {
            this.method = method;
            this.applyOnDelegate = applyOnDelegate;
            this.valueParameterType = valueParameterType;
            this.originallyAccessible = method.isAccessible();
        }

        void restoreAccess() {
            if (!originallyAccessible) {
                method.setAccessible(false);
            }
        }

    }

}
