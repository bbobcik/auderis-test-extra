package cz.auderis.test.parameter.annotation;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.temp.factory.ValueClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Is.is;

@RunWith(JUnitParamsRunner.class)
public class UsingFactoryAnnotationTest {

    @Test
    @Parameters({
            "-123 | -123"
    })
    public void shouldConvertPrimitiveInt(
            @UsingFactory(type = Integer.class) int converted,
            int expected
    ) throws Exception {
        assertThat(converted, is(expected));
    }

    @Test
    @Parameters({
            "6789 | 6789"
    })
    public void shouldConvertBoxedInt(
            @UsingFactory(type = Integer.class) Integer converted,
            int expected
    ) throws Exception {
        assertThat(converted, is(expected));
    }

    @Test
    @Parameters({
            "-456 | -456"
    })
    public void shouldConvertWithDefaultMethod(
            @UsingFactory(type = ValueClass.class) ValueClass converted,
            int expected
    ) throws Exception {
        assertThat(converted, hasProperty("value", is(expected)));
    }

    @Test
    @Parameters({
            "xxxx | 4",
            "xyx  | 2"
    })
    public void shouldConvertWithExplicitMethod(
            @UsingFactory(type = ValueClass.class, method = "countX") ValueClass converted,
            int expected
    ) throws Exception {
        assertThat(converted, hasProperty("value", is(expected)));
    }

}
