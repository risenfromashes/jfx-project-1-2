package edu.buet.data;

import java.text.DecimalFormat;

public class Currency {
    private String str;
    private int number;
    private static final DecimalFormat format1 = new DecimalFormat("00.0");
    private  static final DecimalFormat format2 = new DecimalFormat("0.00");
    public Currency(String str) {
        this.number = toInt(str);
        this.str = toString(number);
    }
    public Currency(int number){
        this.number = number;
        this.str = toString(number);
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
        this.str = toString(number);
    }
    public String getString() {
        return str;
    }
    public void setString(String str) {
        this.number = toInt(str);
        this.str = toString(number);
    }
    private static int toInt(String str) {
        str = str.strip();
        assert(str.charAt(0) == '€');
        return Integer.parseInt( str
                                 .replace("€", "")
                                 .replace(",", "")
                                 .replace("B", "MK")
                                 .replace("M", "KK")
                                 .replace("K", "000"));
    }
    private static String toString(int i) {
        var sb = new StringBuilder();
        float v = i;
        if (v >= 1e9) {
            sb.append('B');
            v /= 1e9;
        } else if (v >= 1e6) {
            sb.append('M');
            v /= 1e6;
        } else if (v >= 1e3) {
            sb.append('K');
            v /= 1e3;
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
            int iv = Math.round(v * 100);
            if (iv % 100 == 0) {
                sb.insert(0, iv / 100);
            } else
                sb.insert(0, format2.format(iv / 100.0));
        }
        sb.insert(0, '€');
        return sb.toString();
    }
}
