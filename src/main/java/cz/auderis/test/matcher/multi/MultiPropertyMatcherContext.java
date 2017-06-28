package cz.auderis.test.matcher.multi;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public interface MultiPropertyMatcherContext {

    MultiPropertyMatcher<?> parentMatcher();

    Object matchedObject();

}
