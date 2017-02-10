package org.test.temp.multiprop;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class PropertyContainer {

    String text;
    int number;

    public PropertyContainer() {
    }

    public PropertyContainer(String text, int number) {
        this.text = text;
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
