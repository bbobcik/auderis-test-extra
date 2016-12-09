package org.test.temp.factory;

public class ValueClass {

    final int x;

    public static ValueClass valueOf(String txt) {
        return new ValueClass(Integer.valueOf(txt));
    }

    public static ValueClass countX(String txt) {
        int count = 0;
        if (null != txt) {
            int idx = -1;
            do {
                idx = txt.indexOf('x', idx + 1);
                if (idx >= 0) {
                    ++count;
                }
            } while (idx >= 0);
        }
        return new ValueClass(count);
    }

    private ValueClass(int num) {
        x = num;
    }

    public int getValue() {
        return x;
    }

}
