package com.up.math.vector;

import com.up.math.number.Real;
import com.up.math.number.NeoComplex;

import java.util.function.Function;

public class NeoPoint2<T extends Real<T>> {

    private final T x;
    private final T y;

    public NeoPoint2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public NeoPoint2(T e) {
        this(e, e);
    }
    
    public T x() {
        return x;
    }

    public T y() {
        return y;
    }
    
    public NeoPoint2<T> sum(NeoPoint2<T> p) {
        return new NeoPoint2<T>(x.add(p.x()), y.add(p.y()));
    }

    public NeoPoint2<T> sub(NeoPoint2<T> p) {
        return new NeoPoint2<>(x.sub(p.x()), y.sub(p.y()));
    }

    public NeoPoint2<T> mul(NeoPoint2<T> p) {
        return new NeoPoint2<>(x.mult(p.x()), y.mult(p.y()));
    }

    public NeoPoint2<T> to(NeoPoint2<T> p) {
        return new NeoPoint2<>(p.x().sub(x), p.y().sub(y));
    }

    public T dot(NeoPoint2<T> p) {
        return x.mult(p.x()).add(y.mult(p.y()));
    }

    public NeoPoint2<T> scale(T s) {
        return new NeoPoint2<>(x.mult(s), y.mult(s));
    }

//    public ComplexPoint2 constrain(Rectangle2 rect) {
//        return new ComplexPoint2(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
//    }

    public T length() {
        return x.sqrt().add(y.square()).sqrt();
    }

    public NeoPoint2<T> abs() {
        return new NeoPoint2<>(x.abs(), y.abs());
    }

    public NeoPoint2<T> normalized() {
        T len = length();
        return new NeoPoint2<>(x.div(len), y.div(len));
    }
    
    public NeoComplex<T> asComplex() {
        return new NeoComplex<>(x, y);
    }
    
    public <U extends Real<U>> NeoPoint2<U> toType(Function<T, U> converter) {
        return new NeoPoint2<>(converter.apply(x), converter.apply(y));
    }
    
//    public Point2 toPoint2() {
//        return new Point2(x.toDouble(), y.toDouble());
//    }
//
//    public static <T extends Real<T>> NeoPoint2<T> fromPoint2(Point2D p, Class<T> type) {
//        // TODO: Need to figure out how to provide these static methods but in an interface format?
//        return new NeoPoint2<T>(BigFixed.fromDouble(p.getX(), type), BigFixed.fromDouble(p.getY(), type));
//    }
    
//    public boolean roundedEquals(BigFixedPoint2 p, int precision) {
//        return x.roundedEquals(p.x, precision) && y.roundedEquals(p.y, precision);
//    }
}
