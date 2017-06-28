package cz.auderis.test.matcher.multi;

import cz.auderis.test.support.ContextAwareDescriptionProvider;
import cz.auderis.test.support.DescriptionProvider;
import cz.auderis.test.support.MismatchDescriptionProvider;
import junitparams.JUnitParamsRunner;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
@RunWith(JUnitParamsRunner.class)
public class MultiPropertyMatcherContextTest {

    ContextReceiver ctxReceiver;
    ItemDescriber describer = new ItemDescriber();
    MismatchDescriber mismatchDescriber = new MismatchDescriber();
    Description description = new StringDescription();
    MultiPropertyMatcher<TestClass> matcher;

    @Before
    public void prepareMatcher() throws Exception {
        ctxReceiver = new ContextReceiver();
        describer = new ItemDescriber();
        description = new StringDescription();
        matcher = MultiPropertyMatcher.of(TestClass.class, "test class");
    }

    @Test
    public void shouldProvideContextToDefaultContextReceiverOnItemDescription() throws Exception {
        // When
        matcher.setDefaultContextReceiver(ctxReceiver);
        matcher.addFixedProperty("value", 1, new IntrospectionPropertyExtractor(TestClass.class, "value"));
        matcher.describeTo(description);
        // Then
        assertThat("received context", ctxReceiver.context, is(instanceOf(MultiPropertyMatcherContext.class)));
        MultiPropertyMatcherContext mpContext = (MultiPropertyMatcherContext) ctxReceiver.context;
        assertThat("parent matcher", mpContext.parentMatcher(), is(sameInstance(matcher)));
        assertThat("matched object", mpContext.matchedObject(), is(nullValue()));
    }

    @Test
    public void shouldProvideContextToItemDescriber() throws Exception {
        // When
        matcher.addFixedProperty("value", 1, new IntrospectionPropertyExtractor(TestClass.class, "value"))
               .withMatcherDescriber(describer);
        matcher.describeTo(description);
        // Then
        assertThat("received context", describer.context, is(instanceOf(MultiPropertyMatcherContext.class)));
        MultiPropertyMatcherContext mpContext = (MultiPropertyMatcherContext) describer.context;
        assertThat("parent matcher", mpContext.parentMatcher(), is(sameInstance(matcher)));
        assertThat("matched object", mpContext.matchedObject(), is(nullValue()));
    }

    @Test
    public void shouldProvideContextToDefaultContextReceiverOnMismatchDescription() throws Exception {
        // Given
        final TestClass test = new TestClass(1);
        // When
        matcher.setDefaultContextReceiver(ctxReceiver);
        matcher.addFixedProperty("value", 2, new IntrospectionPropertyExtractor(TestClass.class, "value"));
        matcher.describeMismatch(test, description);
        // Then
        assertThat("received context", ctxReceiver.context, is(instanceOf(MultiPropertyMatcherContext.class)));
        MultiPropertyMatcherContext mpContext = (MultiPropertyMatcherContext) ctxReceiver.context;
        assertThat("parent matcher", mpContext.parentMatcher(), is(sameInstance(matcher)));
        assertThat("matched object", mpContext.matchedObject(), is(sameInstance(test)));
    }

    @Test
    public void shouldProvideContextToMismatchDescriber() throws Exception {
        // Given
        final TestClass test = new TestClass(1);
        // When
        matcher.addFixedProperty("value", 2, new IntrospectionPropertyExtractor(TestClass.class, "value"))
                .withMismatchDescriber(mismatchDescriber);
        matcher.describeMismatch(test, description);
        // Then
        assertThat("received context", mismatchDescriber.context, is(instanceOf(MultiPropertyMatcherContext.class)));
        MultiPropertyMatcherContext mpContext = (MultiPropertyMatcherContext) mismatchDescriber.context;
        assertThat("parent matcher", mpContext.parentMatcher(), is(sameInstance(matcher)));
        assertThat("matched object", mpContext.matchedObject(), is(sameInstance(test)));
    }


    static class TestClass {
        int value;

        public TestClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    static class ContextReceiver implements ContextAwareDescriptionProvider {
        Object context;

        @Override
        public void setContext(Object descriptionContext) {
            this.context = descriptionContext;
        }
    }

    static class ItemDescriber implements DescriptionProvider<Object>, ContextAwareDescriptionProvider {
        Object context;

        @Override
        public void setContext(Object descriptionContext) {
            this.context = descriptionContext;
        }

        @Override
        public void describe(Object object, Description targetDescription) {
            targetDescription.appendText("item ").appendValue(object);
        }
    }

    static class MismatchDescriber implements MismatchDescriptionProvider<Object>, ContextAwareDescriptionProvider {
        Object context;

        @Override
        public void setContext(Object descriptionContext) {
            this.context = descriptionContext;
        }

        @Override
        public void describe(Matcher<? super Object> matcher, Object object, Description targetDescription) {
            targetDescription.appendText("mismatch on ").appendValue(object);
        }
    }

}
