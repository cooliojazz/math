package com.up.math.number;

import java.util.function.Supplier;

public abstract class BigFixed<T extends BigFixed<T>> implements Comparable<T>, Cloneable {
    
    abstract public boolean sign();
    
    abstract T expand();

    abstract T lshBF(int amount);

    abstract T rshBF(int amount);

    abstract public T lshParts(int amount);

    abstract public T rshParts(int amount);
    
    abstract public T abs();
    
    abstract public T reduce();

//    public static T fromDouble(double a);
//
//    public static T fromInt(int a);
//
//    public static T fromShort(short a);
//
//    public static T fromBitString(String s);

    abstract public T add(T b);
    abstract public T sub(T b);

    abstract public T mult(T b);
    abstract public T div(T b);

    abstract public T negate();
    abstract public T inverse();
    
    abstract public T sqrt();
    abstract public T square();

    abstract public T pow(int p);
    
    abstract public T sin();
    abstract public T sinh();
    
    abstract public T cos();
    abstract public T cosh();

    abstract public T atan();
    
    abstract public T exp2();
    abstract public T log2();

    public static <T extends BigFixed<T>> T atan2(T x, T y) {
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
                return x.pi().rshBF(1);
            } else if (y0 < 0) {
                return x.pi().rshBF(1).negate();
            } else {
                return null;
            }
        }
    }
    
    abstract public T zero();
    
    abstract public T one();
    
    abstract public T pi();

    abstract public double toDouble();
    
//    abstract public T fromDouble(double d);
//    
//    abstract public T fromInt(int i);
    
    public static <T extends BigFixed<T>> T fromInt(int i, Class<T> type) {
        if (type == IntFixed.class) {
            return (T)IntFixed.fromInt(i);
        } else if (type == ShortFixed.class) {
            return (T)ShortFixed.fromInt(i);
        }
        return null;
    }
    
    public static <T extends BigFixed<T>> T fromDouble(double d, Class<T> type) {
        if (type == IntFixed.class) {
            return (T)IntFixed.fromDouble(d);
        } else if (type == ShortFixed.class) {
            return (T)ShortFixed.fromDouble(d);
        }
        return null;
    }
    
}
