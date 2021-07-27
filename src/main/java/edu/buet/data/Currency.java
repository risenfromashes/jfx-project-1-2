package edu.buet.data;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Currency implements Serializable {
    private String str;
    private float number;
    private static final DecimalFormat format1 = new DecimalFormat("00.0");
    private  static final DecimalFormat format2 = new DecimalFormat("0.00");
    public Currency(String str) {
        this.number = toNumber(str);
        this.str = toString(number);
    }
    public Currency(float number) {
        this.number = number;
        this.str = toString(number);
    }
    public void add(float other){
        number += other;
        str = toString(number);
    }
    public void add(Currency other){
        add(other.number);
    }
    public void substract(float other){
        add(-other);
    }
    public void substract(Currency other){
        add(-other.number);
    }
    public float getNumber() {
        return number;
    }
    public void setNumber(float number) {
        this.number = number;
        this.str = toString(number);
    }
    public String getString() {
        return str;
    }
    public void setString(String str) {
        this.number = toNumber(str);
        this.str = toString(number);
    }
    private static float toNumber(String str) {
        str = str.strip();
        assert(str.charAt(0) == '€');
        var mult = 1.0;
        var last = str.charAt(str.length() - 1);
        switch (last) {
        case 'B':
            mult = 1e9;
            break;
        case 'M':
            mult = 1e6;
            break;
        case 'K':
            mult = 1e3;
            break;
        default:
            break;
        }
        var after = Float.parseFloat( str
                                      .replace("€", "")
                                      .replace(",", "")
                                      .replace("B", "")
                                      .replace("M", "")
                                      .replace("K", ""));
        after *= mult;
        return after;
    }
    private static String toString(float v) {
        var sb = new StringBuilder();
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
