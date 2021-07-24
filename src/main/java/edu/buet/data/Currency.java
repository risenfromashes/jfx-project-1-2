package edu.buet.data;

import java.text.DecimalFormat;

public class Currency {
    private String str;
    private int quantity;
    private static final DecimalFormat format1 = new DecimalFormat("00.0");
    private  static final DecimalFormat format2 = new DecimalFormat("0.00");
    public Currency(String str) {
    }
    private int toInt(String str) {
        return Integer.parseInt( str.replace(",", "")
                                 .replace("B", "MK")
                                 .replace("M", "KK")
                                 .replace("K", "000"));
    }
    private String toString(int v) {
        var sb = new StringBuilder();
        float f = v;
        if (v >= 1e9) {
            sb.append('B');
            f /= 1e9;
        } else if (v >= 1e6) {
            sb.append('M');
            f /= 1e6;
        } else if (v >= 1e3) {
            sb.append('K');
            f /= 1e3;
        }
        if (v >= 100.0) {
            sb.insert(0, (int)v);
        } else if (v >= 10.0) {
            int iv = Math.round(v * 10);
            if (iv % 10 == 0) {
                sb.insert(0, iv / 10);
            } else
                sb.insert(0, format1.format(iv / 10.0));
        } else {
            int iv = Math.round(v * 10);
            if (iv % 100 == 0) {
                sb.insert(0, iv / 100);
            } else
                sb.insert(0, format2.format(iv / 100.0));
        }
        sb.insert(0,'€');
        return sb.toString();
    }
}
