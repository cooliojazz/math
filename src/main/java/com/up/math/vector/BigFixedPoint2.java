package com.up.math.vector;

import com.up.math.number.BigFixed;
import com.up.math.number.Complex;
import com.up.math.number.ComplexBigFixed;

import java.awt.geom.Point2D;

public class BigFixedPoint2 {

    private final BigFixed x;
    private final BigFixed y;
    
    public BigFixedPoint2(BigFixed x, BigFixed y) {
        this.x = x;
        this.y = y;
    }

    public BigFixedPoint2(BigFixed e) {
        this(e, e);
    }
    
    public BigFixed getX() {
        return x;
    }
    
    public BigFixed getY() {
        return y;
    }
    
//    public ComplexPoint2 sum(ComplexPoint2 p) {
//        return new ComplexPoint2(x + p.getX(), y + p.getY());
//    }
//
//    public ComplexPoint2 sub(Point2D p) {
//        return new ComplexPoint2(x - p.getX(), y - p.getY());
//    }
//
//    public ComplexPoint2 mul(Point2D p) {
//        return new ComplexPoint2(x * p.getX(), y * p.getY());
//    }
//
//    public ComplexPoint2 to(Point2D p) {
//        return new ComplexPoint2(p.getX() - x, p.getY() - y);
//    }
//
//    public double dot(Point2D p) {
//        return x * p.getX() + y * p.getY();
//    }
//
//    public ComplexPoint2 scale(double s) {
//        return new ComplexPoint2(x * s, y * s);
//    }
//
//    public ComplexPoint2 constrain(Rectangle2 rect) {
//        return new ComplexPoint2(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
//    }
//
//    public double length() {
//        return Math.sqrt(x * x + y * y);
//    }
//
//    public BigFixed abs() {
//        return new ComplexPoint2(Math.abs(x), Math.abs(y));
//    }
//
//    public BigFixed normalized() {
//        double len = length();
//        return new ComplexPoint2(x / len, y / len);
//    }
    
    public ComplexBigFixed asComplex() {
        return new ComplexBigFixed(x, y);
    }
    
    public Point2 toPoint2() {
        return new Point2(x.toDouble(), y.toDouble());
    }
    
    public static BigFixedPoint2 fromPoint2(Point2D p) {
        return new BigFixedPoint2(BigFixed.fromDouble(p.getX()), BigFixed.fromDouble(p.getY()));
    }
    
//    public boolean roundedEquals(BigFixedPoint2 p, int precision) {
//        return x.roundedEquals(p.x, precision) && y.roundedEquals(p.y, precision);
//    }
}
