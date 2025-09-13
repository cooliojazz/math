package com.up.math.number;

public abstract class Computable<T extends Computable<T>> implements Comparable<T>, Cloneable {
    
    abstract public T abs();

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
    
    abstract public T pow(T t);
    
    abstract public T exp();
    abstract public T log();

    public static <T extends Computable<T>> T atan2(T x, T y) {
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
    
}
