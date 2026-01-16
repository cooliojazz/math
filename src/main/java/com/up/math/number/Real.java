package com.up.math.number;

import java.lang.reflect.InvocationTargetException;

public interface Real<T extends Real<T>> extends Comparable<T> {
    
    // TODO: All the names of the math operator should match the ones in all the NeoX classes, so it needs to be decided which is the better name for each of these methods
    public boolean sign();
    
    public T abs();

    public T add(T b);
    public T sub(T b);

    public T mult(T b);
    public T div(T b);

    public T negate();
    public T inverse();
    
    public T sqrt();
    public T square();

    public T pow(int p);
    
    public T sin();
    public T sinh();
    
    public T cos();
    public T cosh();

    public T atan();
    
    public T pow(T t);
    
    public T exp();
    public T log();
    
    public T mod(T t);
    
    public T floor();
    public T ceil();

    public static <T extends Real<T>> T atan2(T x, T y) {
        int x0 = x.compareTo(x.zero());
        int y0 = y.compareTo(x.zero());
        if (x0 > 0) {
            return y.div(x).atan();
        } else if (x0 < 0) {
            if (y0 >= 0) {
                return y.div(x).atan().add(x.pi());
            } else {
                return y.div(x).atan().sub(x.pi());
            } 
        } else {
            if (y0 > 0) {
                return x.pi().div(x.two());
            } else if (y0 < 0) {
                return x.pi().div(x.two()).negate();
            } else {
                return x.zero();
//                return null;
            }
        }
    }

    public static <T extends Real<T>> T fromDouble(T example, double d) {
        try {
            return ((Class<T>)example.getClass()).getDeclaredConstructor(double.class).newInstance(d);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public T zero();
    public T one();
    public T two();
    
    public T e();
    public T pi();
    public T sqrt2();
    
//    public T fromDouble(double d);
    public double toDouble();
}
