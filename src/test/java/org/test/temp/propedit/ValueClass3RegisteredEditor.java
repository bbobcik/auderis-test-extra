package org.test.temp.propedit;

public class ValueClass3RegisteredEditor extends ValueClass1Editor {

    @Override
    protected ValueClass1 createResult() {
        return new ValueClass3();
    }

}
