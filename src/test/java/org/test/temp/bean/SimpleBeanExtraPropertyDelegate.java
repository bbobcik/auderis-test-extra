package org.test.temp.bean;

public class SimpleBeanExtraPropertyDelegate {

    private void setExtraProperty(SimpleBean bean, String propertyName, String propertyValue) {
        switch (propertyName) {
            case "x2":
                bean.setX('~' + propertyValue + '~');
                break;
            case "y2":
                bean.setY(Integer.parseInt(propertyValue) + 1);
                break;
            case "z2":
                bean.setY(Integer.parseInt(propertyValue) * 10);
                break;
            default:
                throw new IllegalArgumentException("Unknown property " + propertyName);
        }
    }

}
