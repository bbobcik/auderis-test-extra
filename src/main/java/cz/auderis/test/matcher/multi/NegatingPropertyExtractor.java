package cz.auderis.test.matcher.multi;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class NegatingPropertyExtractor<T> implements PropertyExtractor<T, Boolean> {

    private final PropertyExtractor<T, Boolean> delegate;

    public NegatingPropertyExtractor(PropertyExtractor<T, Boolean> delegate) {
        if (null == delegate) {
            throw new NullPointerException();
        }
        this.delegate = delegate;
    }

    @Override
    public Boolean extract(T obj) {
        final Boolean result = delegate.extract(obj);
        return (null != result) ? !result : null;
    }

    @Override
    public String toString() {
        return "negation of " + delegate.toString();
    }

}
