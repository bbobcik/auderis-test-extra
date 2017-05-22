package cz.auderis.test.parameter.annotation.impl;

import cz.auderis.test.category.DetailedTest;
import cz.auderis.test.category.UnitTest;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
@RunWith(JUnitParamsRunner.class)
@Category({UnitTest.class, DetailedTest.class})
public class KeyValueBeanAnnotationConverterTest {

    @Test
    @Parameters({
            "x=123 y=abc abc='hello world' | 0 | x | 123",
            "x=123 y=abc abc='hello world' | 1 | y | abc",
            "x=123 y=abc abc='hello world' | 2 | abc | hello world",
            "m=-3.14 klm = 'hello\\'moon' | 1 | klm | hello'moon",
            "m=-3.14 klm = 'hello\\\\sun' | 1 | klm | hello\\sun",
            "m=-3.14 klm = 'hello\\ universe' | 1 | klm | hello universe",
    })
    public void shouldParseKeyValues(String keyValueSpec, int index, String expectedKey, String expectedValue) throws Exception {
        // When
        final List<KeyValueBeanAnnotationConverter.KeyValue> keyValues = KeyValueBeanAnnotationConverter.parseKeyValues(keyValueSpec);
        // Then
        assertThat(keyValues, hasSize(greaterThan(index)));
        final KeyValueBeanAnnotationConverter.KeyValue keyValue = keyValues.get(index);
        assertThat(keyValue.key, is(expectedKey));
        assertThat(keyValue.value, is(expectedValue));
    }

    @Test
    @Parameters({
            "x = '<null>' y = <null> | 1 | y",
            "y = '<null>' x = <null> | 1 | x",
    })
    public void shouldParseNullValues(String keyValueSpec, int index, String expectedKey) throws Exception {
        // When
        final List<KeyValueBeanAnnotationConverter.KeyValue> keyValues = KeyValueBeanAnnotationConverter.parseKeyValues(keyValueSpec);
        // Then
        assertThat(keyValues, hasSize(greaterThan(index)));
        final KeyValueBeanAnnotationConverter.KeyValue keyValue = keyValues.get(index);
        assertThat(keyValue.key, is(expectedKey));
        assertThat(keyValue.value, is(nullValue()));
    }

}
