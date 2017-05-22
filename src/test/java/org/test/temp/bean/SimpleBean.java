package org.test.temp.bean;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class SimpleBean {

    private String x;
    private int y;
    private long fieldA;

    public SimpleBean() {
        x = "";
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getA() {
        return fieldA;
    }

    public void setA(long a) {
        this.fieldA = a;
    }

}
