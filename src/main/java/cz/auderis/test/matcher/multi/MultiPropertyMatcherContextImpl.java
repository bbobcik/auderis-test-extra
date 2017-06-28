package cz.auderis.test.matcher.multi;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
final class MultiPropertyMatcherContextImpl implements MultiPropertyMatcherContext {

    private final MultiPropertyMatcher<?> matcher;
    private final Object matchedObject;

    public MultiPropertyMatcherContextImpl(MultiPropertyMatcher<?> matcher, Object matchedObject) {
        this.matcher = matcher;
        this.matchedObject = matchedObject;
    }

    @Override
    public MultiPropertyMatcher<?> parentMatcher() {
        return matcher;
    }

    @Override
    public Object matchedObject() {
        return matchedObject;
    }

}
