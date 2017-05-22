package cz.auderis.test.support.editor;

import java.math.BigInteger;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class BigIntegerEditor extends AbstractTextEditorSupport {

    public BigIntegerEditor() {
        super();
    }

    public BigIntegerEditor(Object source) {
        super(source);
    }

    @Override
    public Object getValue() {
        final String txt = getAsText();
        if (null == txt) {
            return null;
        }
        return new BigInteger(txt);
    }

}
