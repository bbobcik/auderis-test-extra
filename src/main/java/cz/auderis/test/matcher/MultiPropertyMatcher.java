package cz.auderis.test.matcher;

import cz.auderis.test.support.DescriptionProvider;
import cz.auderis.test.support.MismatchDescriptionProvider;
import cz.auderis.test.support.NaturalDescriptionJoiner;
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

    final List<PropMatcherEntry> entries;
    final String objectName;
    Callable<NaturalDescriptionJoiner> joinerProvider;
    Callable<NaturalDescriptionJoiner> mismatchJoinerProvider;

    public MultiPropertyMatcher(Class<T> matchedTypeClass, String objectName) {
        this(matchedTypeClass, objectName, null, null);
    }

    public MultiPropertyMatcher(Class<T> matchedTypeClass, String objectName, Callable<NaturalDescriptionJoiner> joinerProvider, Callable<NaturalDescriptionJoiner> mismatchJoinerProvider) {
        super(matchedTypeClass);
        this.entries = new LinkedList<>();
        this.objectName = (null != objectName) ? objectName : matchedTypeClass.getSimpleName();
        this.joinerProvider = joinerProvider;
        this.mismatchJoinerProvider = mismatchJoinerProvider;
    }

    public <P> PropertyEntry addProperty(Object propertyName, Matcher<? super P> propertyMatcher, PropertyExtractor<T, P> propertyExtractor) {
        if ((null == propertyMatcher) || (null == propertyExtractor)) {
            return DummyPropertyEntry.INSTANCE;
        }
        final PropMatcherEntry entry = new PropMatcherEntry((Matcher<Object>) propertyMatcher, propertyExtractor, propertyName, null);
        entries.add(entry);
        return entry;
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

    public interface PropertyEntry {
        PropertyEntry withPrefix(Object prefix);
        PropertyEntry withSuffix(Object suffix);
        PropertyEntry withMatcherDescriber(DescriptionProvider<Matcher<?>> describer);
        PropertyEntry withMismatchDescriber(MismatchDescriptionProvider<?> describer);
    }

    static final class PropMatcherEntry implements PropertyEntry {
        final Matcher<Object> matcher;
        final PropertyExtractor extractor;
        Object descPrefix;
        Object descSuffix;
        DescriptionProvider<Matcher<?>> valueDescriber;
        MismatchDescriptionProvider<Object> mismatchDescriber;

        PropMatcherEntry(Matcher<Object> matcher, PropertyExtractor extractor, Object descPrefix, Object descSuffix) {
            assert null != matcher;
            assert null != extractor;
            this.matcher = matcher;
            this.extractor = extractor;
            this.descPrefix = descPrefix;
            this.descSuffix = descSuffix;
        }

        @Override
        public PropertyEntry withPrefix(Object prefix) {
            this.descPrefix = prefix;
            return this;
        }

        @Override
        public PropertyEntry withSuffix(Object suffix) {
            this.descSuffix = suffix;
            return this;
        }

        @Override
        public PropertyEntry withMatcherDescriber(DescriptionProvider<Matcher<?>> describer) {
            this.valueDescriber = describer;
            return this;
        }

        @Override
        public PropertyEntry withMismatchDescriber(MismatchDescriptionProvider<?> describer) {
            this.mismatchDescriber = (MismatchDescriptionProvider<Object>) describer;
            return this;
        }

        boolean matches(Object obj) {
            final Object property = extractor.extract(obj);
            return matcher.matches(property);
        }

        void addToJoiner(NaturalDescriptionJoiner joiner) {
            joiner.add(descPrefix, matcher, descSuffix, valueDescriber);
        }

        void addMismatchToJoiner(Object obj, NaturalDescriptionJoiner joiner) {
            final Object property = extractor.extract(obj);
            if (!matcher.matches(property)) {
                joiner.addMismatch(descPrefix, matcher, property, descSuffix, mismatchDescriber);
            }
        }
    }

    enum DummyPropertyEntry implements PropertyEntry {
        INSTANCE {
            @Override
            public PropertyEntry withPrefix(Object prefix) {
                return this;
            }

            @Override
            public PropertyEntry withSuffix(Object suffix) {
                return this;
            }

            @Override
            public PropertyEntry withMatcherDescriber(DescriptionProvider<Matcher<?>> describer) {
                return this;
            }

            @Override
            public PropertyEntry withMismatchDescriber(MismatchDescriptionProvider<?> describer) {
                return this;
            }
        }
    }

}