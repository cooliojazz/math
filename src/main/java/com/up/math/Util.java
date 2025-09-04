package com.up.math;

public class Util {
    
    public static boolean roundedEquals(double d1, double d2, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(d1 * factor) == Math.round(d2 * factor);
    }
    
    public static double roundTo(double d, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(d * factor) / factor;
    }
    
}
