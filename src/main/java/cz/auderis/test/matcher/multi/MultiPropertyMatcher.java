package cz.auderis.test.matcher.multi;

import cz.auderis.test.support.DescriptionProvider;
import cz.auderis.test.support.MismatchDescriptionProvider;
import cz.auderis.test.support.NaturalDescriptionJoiner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class MultiPropertyMatcher<T> extends TypeSafeMatcher<T> {

    public static <T> MultiPropertyMatcher<T> of(Class<T> targetType, String objectName) {
        return new MultiPropertyMatcher<>(targetType, objectName);
    }

    final Class<T> expectedClass;
    final List<PropMatcherEntry> entries;
    final String objectName;
    Callable<NaturalDescriptionJoiner> joinerProvider;
    Callable<NaturalDescriptionJoiner> mismatchJoinerProvider;
    PropertyEntry lastEntry;

    public MultiPropertyMatcher(Class<T> matchedTypeClass, String objectName) {
        this(matchedTypeClass, objectName, null, null);
    }

    public MultiPropertyMatcher(Class<T> matchedTypeClass, String objectName, Callable<NaturalDescriptionJoiner> joinerProvider, Callable<NaturalDescriptionJoiner> mismatchJoinerProvider) {
        super(matchedTypeClass);
        this.expectedClass = matchedTypeClass;
        this.entries = new LinkedList<>();
        this.objectName = (null != objectName) ? objectName : matchedTypeClass.getSimpleName();
        this.joinerProvider = joinerProvider;
        this.mismatchJoinerProvider = mismatchJoinerProvider;
        this.lastEntry = DummyPropertyEntry.INSTANCE;
    }

    public <P> MultiPropertyMatcher<T> addProperty(Object propertyName, Matcher<? super P> propertyMatcher, PropertyExtractor<T, P> propertyExtractor) {
        if ((null == propertyMatcher) || (null == propertyExtractor)) {
            lastEntry = DummyPropertyEntry.INSTANCE;
        } else {
            final Object descriptionPrefix = withSmartRightWhitespace(propertyName);
            final PropMatcherEntry entry = new PropMatcherEntry((Matcher<Object>) propertyMatcher, propertyExtractor, descriptionPrefix, null);
            entries.add(entry);
            lastEntry = entry;
        }
        return this;
    }

    public MultiPropertyMatcher<T> addProperty(String propertyName, Matcher<? super Object> propertyMatcher) {
        final PropertyExtractor<T,Object> propertyExtractor = new IntrospectionPropertyExtractor<>(expectedClass, propertyName);
        return addProperty(propertyName, propertyMatcher, propertyExtractor);
    }

    public <P> MultiPropertyMatcher<T> addFixedProperty(Object propertyName, P fixedValue, PropertyExtractor<T, P> propertyExtractor) {
        final Matcher<? super P> matcher;
        if (null != fixedValue) {
            final String description;
            if (fixedValue instanceof CharSequence) {
                description = '"' + fixedValue.toString() + '"';
            } else {
                description = '<' + String.valueOf(fixedValue) + '>';
            }
            final Matcher<P> baseMatcher = CoreMatchers.is(fixedValue);
            matcher = CoreMatchers.describedAs(description, baseMatcher);
        } else {
            matcher = CoreMatchers.nullValue();
        }
        return addProperty(propertyName, matcher, propertyExtractor);
    }

    public <P> MultiPropertyMatcher<T> addFixedProperty(String propertyName, P fixedValue) {
        final PropertyExtractor<T,Object> propertyExtractor = new IntrospectionPropertyExtractor<>(expectedClass, propertyName);
        return addFixedProperty(propertyName, fixedValue, propertyExtractor);
    }

    public MultiPropertyMatcher<T> withPrefix(Object prefix) {
        lastEntry.withPrefix(prefix);
        return this;
    }

    public MultiPropertyMatcher<T> withSuffix(Object suffix) {
        lastEntry.withSuffix(suffix);
        return this;
    }

    public MultiPropertyMatcher<T> withMatcherDescriber(DescriptionProvider<Matcher<?>> describer) {
        lastEntry.withMatcherDescriber(describer);
        return this;
    }

    public MultiPropertyMatcher<T> withMismatchDescriber(MismatchDescriptionProvider<?> describer) {
        lastEntry.withMismatchDescriber(describer);
        return this;
    }

    public void setJoinerProvider(Callable<NaturalDescriptionJoiner> joinerProvider) {
        this.joinerProvider = joinerProvider;
    }

    public void setMismatchJoinerProvider(Callable<NaturalDescriptionJoiner> mismatchJoinerProvider) {
        this.mismatchJoinerProvider = mismatchJoinerProvider;
    }

    @Override
    protected boolean matchesSafely(T obj) {
        for (PropMatcherEntry entry : entries) {
            if (!entry.matches(obj)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(objectName);
        final NaturalDescriptionJoiner joiner;
        if (null == joinerProvider) {
            joiner = new NaturalDescriptionJoiner(" with ", ", ", " and ", null);
        } else {
            try {
                joiner = joinerProvider.call();
            } catch (Exception e) {
                throw new RuntimeException("Cannot describe object " + objectName, e);
            }
        }
        for (PropMatcherEntry entry : entries) {
            entry.addToJoiner(joiner);
        }
        joiner.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        final NaturalDescriptionJoiner joiner;
        if (null == mismatchJoinerProvider) {
            joiner = new NaturalDescriptionJoiner(", ", " and ");
        } else {
            try {
                joiner = mismatchJoinerProvider.call();
            } catch (Exception e) {
                throw new RuntimeException("Cannot describe mismatch of object " + objectName, e);
            }
        }
        for (PropMatcherEntry entry : entries) {
            entry.addMismatchToJoiner(item, joiner);
        }
        joiner.describeTo(mismatchDescription);
    }

    static Object withSmartRightWhitespace(Object textObj) {
        if ((null == textObj) || !(textObj instanceof CharSequence)) {
            return textObj;
        }
        final CharSequence text = (CharSequence) textObj;
        final int length = text.length();
        if (0 == length) {
            return textObj;
        }
        final char lastChar = text.charAt(length - 1);
        return Character.isLetterOrDigit(lastChar) ? String.valueOf(text) + ' ' : textObj;
    }

}
