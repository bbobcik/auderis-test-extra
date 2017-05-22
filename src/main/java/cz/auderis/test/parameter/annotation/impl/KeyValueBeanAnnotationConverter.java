package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.parameter.annotation.KeyValueBean;
import junitparams.converters.Converter;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class KeyValueBeanAnnotationConverter extends AbstractKeyValueConverter implements Converter<KeyValueBean, Object> {

    @Override
    public void initialize(KeyValueBean annotation) {
        this.beanClass = annotation.value();
        this.propertyDelegateClass = annotation.propertyDelegate();
        initializePropertyDelegate();
    }

}
