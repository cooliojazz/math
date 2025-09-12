package com.up.math.vector;

import com.up.math.number.BigFixed;
import com.up.math.number.ComplexBigFixed;

import java.awt.geom.Point2D;

public class BigFixedPoint2<T extends BigFixed<T>> {

    private final T x;
    private final T y;
    
    public BigFixedPoint2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public BigFixedPoint2(T e) {
        this(e, e);
    }
    
    public T getX() {
        return x;
    }
    
    public T getY() {
        return y;
    }
    
    public BigFixedPoint2<T> sum(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<T>(x.add(p.getX()), y.add(p.getY()));
    }

    public BigFixedPoint2<T> sub(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<>(x.sub(p.getX()), y.sub(p.getY()));
    }

    public BigFixedPoint2<T> mul(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<>(x.mult(p.getX()), y.mult(p.getY()));
    }

    public BigFixedPoint2<T> to(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<>(p.getX().sub(x), p.getY().sub(y));
    }

    public T dot(BigFixedPoint2<T> p) {
        return x.mult(p.getX()).add(y.mult(p.getY()));
    }

    public BigFixedPoint2<T> scale(T s) {
        return new BigFixedPoint2<>(x.mult(s), y.mult(s));
    }

//    public ComplexPoint2 constrain(Rectangle2 rect) {
//        return new ComplexPoint2(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
//    }

    public T length() {
        return x.sqrt().add(y.square()).sqrt();
    }

    public BigFixedPoint2<T> abs() {
        return new BigFixedPoint2<>(x.abs(), y.abs());
    }

    public BigFixedPoint2<T> normalized() {
        T len = length();
        return new BigFixedPoint2<>(x.div(len), y.div(len));
    }
    
    public ComplexBigFixed<T> asComplex() {
        return new ComplexBigFixed<>(x, y);
    }
    
    public Point2 toPoint2() {
        return new Point2(x.toDouble(), y.toDouble());
    }
    
    public static <T extends BigFixed<T>> BigFixedPoint2<T> fromPoint2(Point2D p, Class<T> type) {
        // TODO: Need to figure out how to provide these static methods but in an interface format?
        return new BigFixedPoint2<T>(BigFixed.fromDouble(p.getX(), type), BigFixed.fromDouble(p.getY(), type));
    }
    
//    public boolean roundedEquals(BigFixedPoint2 p, int precision) {
//        return x.roundedEquals(p.x, precision) && y.roundedEquals(p.y, precision);
//    }
}
