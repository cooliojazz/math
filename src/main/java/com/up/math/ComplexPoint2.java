package com.up.math;

import java.awt.geom.Point2D;

public class ComplexPoint2 {

    private Complex x;
    private Complex y;
    
    public ComplexPoint2(Complex x, Complex y) {
        this.x = x;
        this.y = y;
    }

//    public ComplexPoint2(Complex e) {
//        super(e, e);
//    }
    
    public Complex getX() {
        return x;
    }
    
    public Complex getY() {
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
//    public ComplexPoint2 abs() {
//        return new ComplexPoint2(Math.abs(x), Math.abs(y));
//    }
//
//    public ComplexPoint2 normalized() {
//        double len = length();
//        return new ComplexPoint2(x / len, y / len);
//    }
}
