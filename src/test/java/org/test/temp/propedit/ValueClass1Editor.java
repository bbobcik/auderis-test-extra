package org.test.temp.propedit;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueClass1Editor extends PropertyEditorSupport {

    static final Pattern PARSER = Pattern.compile("^(\\d+)-(.*)$");

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        final Matcher matcher = PARSER.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Value does not conform to required pattern");
        }
        final int num = Integer.parseInt(matcher.group(1));
        final String txt = matcher.group(2);
        final ValueClass1 vc = createResult();
        vc.setNumValue(num);
        vc.setTextValue(txt);
        setValue(vc);
    }

    protected ValueClass1 createResult() {
        return new ValueClass1();
    }

}
