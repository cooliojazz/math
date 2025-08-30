package com.up.math;

import java.awt.geom.Point2D;

public class Point2 extends Point2D.Double {

    public Point2(double x, double y) {
        super(x, y);
    }

    public Point2(double e) {
        super(e, e);
    }
    
    public Point2(Point2D p) {
        super(p.getX(), p.getY());
    }

    public Point2 sum(Point2D p) {
        return new Point2(x + p.getX(), y + p.getY());
    }

    public Point2 sub(Point2D p) {
        return new Point2(x - p.getX(), y - p.getY());
    }

    public Point2 mul(Point2D p) {
        return new Point2(x * p.getX(), y * p.getY());
    }

    public Point2 to(Point2D p) {
        return new Point2(p.getX() - x, p.getY() - y);
    }

    public double dot(Point2D p) {
        return x * p.getX() + y * p.getY();
    }

    public Point2 scale(double s) {
        return new Point2(x * s, y * s);
    }

    public Point2 constrain(Rectangle2 rect) {
        return new Point2(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Point2 abs() {
        return new Point2(Math.abs(x), Math.abs(y));
    }

    public Point2 normalized() {
        double len = length();
        return new Point2(x / len, y / len);
    }
    
    public Complex asComplex() {
        return new Complex(x, y);
    }
}
