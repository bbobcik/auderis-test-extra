package cz.auderis.test.support.editor;

import java.beans.PropertyEditorSupport;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public abstract class AbstractTextEditorSupport extends PropertyEditorSupport {

    public AbstractTextEditorSupport() {
        super();
    }

    public AbstractTextEditorSupport(Object source) {
        super(source);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text.isEmpty()) {
            setValue(null);
        } else {
            setValue(text);
        }
    }

}
