package cz.auderis.test.matcher;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public interface PropertyExtractor<T, P> {

    P extract(T obj);

}
