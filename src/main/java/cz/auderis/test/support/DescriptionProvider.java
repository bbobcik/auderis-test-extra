package cz.auderis.test.support;

import org.hamcrest.Description;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public interface DescriptionProvider<T> {

    void describe(T object, Description targetDescription);

}
