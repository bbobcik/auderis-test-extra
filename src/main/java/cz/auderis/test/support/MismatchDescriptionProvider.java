package cz.auderis.test.support;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public interface MismatchDescriptionProvider<T> {

    void describe(Matcher<? super T> matcher, T object, Description targetDescription);

}
