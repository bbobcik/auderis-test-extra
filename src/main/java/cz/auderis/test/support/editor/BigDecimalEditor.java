package cz.auderis.test.support.editor;

import java.math.BigDecimal;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class BigDecimalEditor extends AbstractTextEditorSupport {

    public BigDecimalEditor() {
        super();
    }

    public BigDecimalEditor(Object source) {
        super(source);
    }

    @Override
    public Object getValue() {
        final String txt = getAsText();
        if (null == txt) {
            return null;
        }
        return new BigDecimal(txt);
    }

}
