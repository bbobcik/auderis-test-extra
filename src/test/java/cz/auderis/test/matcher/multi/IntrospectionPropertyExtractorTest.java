package cz.auderis.test.matcher.multi;

import org.junit.Test;
import org.test.temp.multiprop.PropTopClass;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

public class IntrospectionPropertyExtractorTest {

    public static final int INT_VALUE = -77;
    public static final String TEXT_VALUE = "xyzABC";
    public static final BigDecimal DECIMAL_VALUE = BigDecimal.valueOf(31415L, 4);
    public static final long LONG_VALUE = 987123L;

    final PropTopClass testedObject = new PropTopClass(INT_VALUE, TEXT_VALUE, DECIMAL_VALUE, LONG_VALUE);
    IntrospectionPropertyExtractor<PropTopClass> extractor;

    @Test
    public void shouldExtractIntProperty() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "intProperty");
        assertThat(extractor.extract(testedObject), is(INT_VALUE));
    }

    @Test
    public void shouldExtractIntField() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "intField");
        assertThat(extractor.extract(testedObject), is(INT_VALUE));
    }

    @Test
    public void shouldExtractTextProperty() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "textProperty");
        assertThat(extractor.extract(testedObject), is(TEXT_VALUE));
    }

    @Test
    public void shouldExtractTextField() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "textField");
        assertThat(extractor.extract(testedObject), is(TEXT_VALUE));
    }

    @Test
    public void shouldExtractDecimalProperty() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "decimalProperty");
        final Object value = extractor.extract(testedObject);
        assertThat(value, instanceOf(BigDecimal.class));
        assertThat((BigDecimal) value, comparesEqualTo(DECIMAL_VALUE));
    }

    @Test
    public void shouldExtractDecimalField() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "decimalField");
        final Object value = extractor.extract(testedObject);
        assertThat(value, instanceOf(BigDecimal.class));
        assertThat((BigDecimal) value, comparesEqualTo(DECIMAL_VALUE));
    }

    @Test
    public void shouldExtractLongProperty() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "longProperty");
        assertThat(extractor.extract(testedObject), is(LONG_VALUE));
    }

    @Test
    public void shouldExtractLongField() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "longField");
        assertThat(extractor.extract(testedObject), is(LONG_VALUE));
    }

    @Test
    public void shouldExtractNullProperty() throws Exception {
        extractor = new IntrospectionPropertyExtractor<>(PropTopClass.class, "nullProperty");
        assertThat(extractor.extract(testedObject), is(nullValue()));
    }

}
