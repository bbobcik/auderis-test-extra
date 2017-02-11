package org.test.temp.multiprop;

public class PropBaseClass {

    private int intField;
    String textField;

    public PropBaseClass(int intValue, String textValue) {
        this.intField = intValue;
        this.textField = textValue;
    }

    int getIntProperty() {
        return intField;
    }

    private String getTextProperty() {
        return textField;
    }

    private Object getNullProperty() {
        return null;
    }

}
