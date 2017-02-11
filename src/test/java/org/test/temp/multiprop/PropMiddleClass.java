package org.test.temp.multiprop;

import java.math.BigDecimal;

public class PropMiddleClass extends PropBaseClass {

    protected final BigDecimal decimalField;

    public PropMiddleClass(int intValue, String textValue, BigDecimal decimalValue) {
        super(intValue, textValue);
        this.decimalField = decimalValue;
    }

    protected BigDecimal getDecimalProperty() {
        return decimalField;
    }

    void getNullProperty() {
        // This is a trap: the method must be ignored during getter introspection as it has invalid return type
        throw new AssertionError("bad getter - has void return type");
    }

}
