package cz.auderis.test.parameter.annotation;

import cz.auderis.test.category.UnitTest;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.test.temp.bean.SimpleBean;
import org.test.temp.bean.SimpleBeanPropertyDelegate;
import org.test.temp.bean.SimpleBeanStaticPropertyDelegate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
@RunWith(JUnitParamsRunner.class)
@Category(UnitTest.class)
public class KeyValueBeanTest {

    @Test
    @Parameters({
            "x=abc y=123            | abc | 123",
            "x=abc                  | abc | 0",
            "y=987                  |     | 987",
            "x=abc y=456 x=KLM y=-1 | KLM | -1"
    })
    public void shouldSetPropertyDirectly(@KeyValueBean(SimpleBean.class) SimpleBean bean, String expectedX, int expectedY) throws Exception {
        assertThat("property X", bean.getX(), is(expectedX));
        assertThat("property Y", bean.getY(), is(expectedY));
    }

    @Test
    @Parameters({
            "x=abc y=123            | ~abc~ | 124",
            "x=abc                  | ~abc~ | 0",
            "y=987                  |       | 988",
            "x=abc y=456 x=KLM y=-2 | ~KLM~ | -1",
            "z=123                  |       | 1230",
    })
    public void shouldSetPropertyViaDelegate(@KeyValueBean(value = SimpleBean.class, propertyDelegate = SimpleBeanPropertyDelegate.class) SimpleBean bean, String expectedX, int expectedY) throws Exception {
        assertThat("property X", bean.getX(), is(expectedX));
        assertThat("property Y", bean.getY(), is(expectedY));
    }

    @Test
    @Parameters({
            "staticX=abc1 | $abc1$ | 0",
    })
    public void shouldSetPropertyViaDelegateStaticSetter(@KeyValueBean(value = SimpleBean.class, propertyDelegate = SimpleBeanPropertyDelegate.class) SimpleBean bean, String expectedX, int expectedY) throws Exception {
        assertThat("property X", bean.getX(), is(expectedX));
        assertThat("property Y", bean.getY(), is(expectedY));
    }

    @Test
    @Parameters({
            "x=abc y=123            | #abc# | 125",
            "x=abc                  | #abc# | 0",
            "y=987                  |       | 989",
            "x=abc y=456 x=KLM y=-9 | #KLM# | -7"
    })
    public void shouldSetPropertyViaStaticDelegate(@KeyValueBean(value = SimpleBean.class, propertyDelegate = SimpleBeanStaticPropertyDelegate.class) SimpleBean bean, String expectedX, int expectedY) throws Exception {
        assertThat("property X", bean.getX(), is(expectedX));
        assertThat("property Y", bean.getY(), is(expectedY));
    }

    @Test
    @Parameters({
            "x=abc y=123                        | 0",
            "x=abc fieldA=123                   | 123",
            "y=987 fieldA=-543                  |-543",
            "fieldA=1 fieldA=2 x=XYZ fieldA=999 | 999",
    })
    public void shouldSetFieldDirectly(@KeyValueBean(SimpleBean.class) SimpleBean bean, long expectedA) throws Exception {
        assertThat("field A", bean.getA(), is(expectedA));
    }

}
