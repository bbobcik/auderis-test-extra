package cz.auderis.test.matcher.multi;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.temp.multiprop.PropertyContainer;

import static org.hamcrest.CoreMatchers.describedAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
@RunWith(JUnitParamsRunner.class)
public class MultiPropertyMatcherTest {

    MultiPropertyMatcher<PropertyContainer> multiMatcher = new MultiPropertyMatcher<>(PropertyContainer.class, "property container");

    @Test
    @Parameters({
            "1    | 1 | xyz  | xyz | true",
            "1    | 0 | xyz  | xyz | false",
            "1    | 1 | xyz  | abc | false",
            "null | 1 | xyz  | xyz | true",
            "null | 0 | xyz  | xyz | true",
            "null | 0 | xyz  | abc | false",
            "1    | 1 | null | xyz | true",
            "1    | 1 | null | abc | true",
            "1    | 0 | null | xyz | false",
    })
    public void shouldCorrectlyEvaluateProperties(String expectedNum, int providedNum, String expectedText, String providedText, boolean expectedResult) throws Exception {
        // Given
        final PropertyContainer testedObject = new PropertyContainer(providedText, providedNum);

        // When
        multiMatcher.addProperty("text ", textMatcher(expectedText), Extract.TEXT);
        multiMatcher.addProperty("number ", numberMatcher(expectedNum), Extract.NUMBER);
        final boolean result = multiMatcher.matches(testedObject);

        // Then
        assertThat(result, is(expectedResult));
    }

    @Test
    @Parameters({
            "1    | xyz  | property container with text \"xyz\" and number 1",
            "1    | null | property container with number 1",
            "null | xyz  | property container with text \"xyz\"",
            "null | null | property container"
    })
    public void shouldCorrectlyDescribeMatcher(String expectedNum, String expectedText, String expectedDescription) throws Exception {
        // Given
        Description desc = new StringDescription();

        // When
        multiMatcher.addProperty("text ", textMatcher(expectedText), Extract.TEXT);
        multiMatcher.addProperty("number ", numberMatcher(expectedNum), Extract.NUMBER);
        multiMatcher.describeTo(desc);

        // Then
        final String resultText = desc.toString();
        assertThat(resultText, is(expectedDescription));
    }

    @Test
    @Parameters({
            "1    | 1 | xyz  | xyz | ",
            "1    | 0 | xyz  | xyz | number was <0>",
            "1    | 1 | xyz  | abc | text was \"abc\"",
            "1    | 2 | xyz  | opq | text was \"opq\" and number was <2>",
            "null | 0 | xyz  | abc | text was \"abc\"",
            "1    | 0 | null | xyz | number was <0>",
    })
    public void shouldCorrectlyDescribeMismatch(String expectedNum, int providedNum, String expectedText, String providedText, String expectedDescription) throws Exception {
        // Given
        final PropertyContainer testedObject = new PropertyContainer(providedText, providedNum);
        Description desc = new StringDescription();

        // When
        multiMatcher.addProperty("text ", textMatcher(expectedText), Extract.TEXT);
        multiMatcher.addProperty("number ", numberMatcher(expectedNum), Extract.NUMBER);
        multiMatcher.describeMismatch(testedObject, desc);

        // Then
        final String resultText = desc.toString();
        assertThat(resultText, is(expectedDescription));
    }

    private static String nullable(String s) {
        return "null".equals(s) ? null : s;
    }

    private static Matcher<Object> numberMatcher(String numSpec) {
        final String spec = nullable(numSpec);
        if (null == spec) {
            return null;
        }
        return describedAs(spec, is(Integer.valueOf(spec)));
    }

    private static Matcher<Object> textMatcher(String textSpec) {
        final String spec = nullable(textSpec);
        if (null == spec) {
            return null;
        }
        return describedAs('"' + spec + '"', is(spec));
    }


    enum Extract implements PropertyExtractor<PropertyContainer, Object> {
        TEXT {
            @Override
            public Object extract(PropertyContainer obj) {
                return obj.getText();
            }
        },
        NUMBER {
            @Override
            public Object extract(PropertyContainer obj) {
                return obj.getNumber();
            }
        }
    }

}
