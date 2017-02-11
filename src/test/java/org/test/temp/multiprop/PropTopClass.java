package org.test.temp.multiprop;

import java.math.BigDecimal;

public class PropTopClass extends PropMiddleClass {

    private final long longField;

    public PropTopClass(int intValue, String textValue, BigDecimal decimalValue, long longValue) {
        super(intValue, textValue, decimalValue);
        this.longField = longValue;
    }

    public long getLongProperty() {
        return longField;
    }

}
