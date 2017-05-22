package org.test.temp.bean;

/**
 * @author Boleslav Bobcik
 * @version 1.0.0
 */
public final class SimpleBeanStaticPropertyDelegate {

    public static String getX(SimpleBean bean) {
        return bean.getX();
    }

    public static void setX(SimpleBean bean, String x) {
        final String x2 = '#' + x + '#';
        bean.setX(x2);
    }

    public static int getY(SimpleBean bean) {
        return bean.getY();
    }

    public static void setY(SimpleBean bean, int y) {
        final int y2 = y + 2;
        bean.setY(y2);
    }

    private SimpleBeanStaticPropertyDelegate() {
        throw new AssertionError("only static methods");
    }

}
