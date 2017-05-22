package org.test.temp.bean;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public class SimpleBeanPropertyDelegate {

    public String getX(SimpleBean bean) {
        return bean.getX();
    }

    public void setX(SimpleBean bean, String x) {
        final String x2 = '~' + x + '~';
        bean.setX(x2);
    }

    public int getY(SimpleBean bean) {
        return bean.getY();
    }

    public void setY(SimpleBean bean, int y) {
        final int y2 = y + 1;
        bean.setY(y2);
    }

    public int getZ(SimpleBean bean) {
        return bean.getY();
    }

    public void setZ(SimpleBean bean, String zValue) {
        final int z2 = 10 * Integer.parseInt(zValue);
        bean.setY(z2);
    }

    public static String getStaticX(SimpleBean bean) {
        return bean.getX();
    }

    public static void setStaticX(SimpleBean bean, String x) {
        final String x2 = '$' + x + '$';
        bean.setX(x2);
    }

}
