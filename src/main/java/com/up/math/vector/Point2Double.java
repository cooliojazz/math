package com.up.math.vector;

import com.up.math.Util;
import com.up.math.number.Complex;
import com.up.math.number.DoubleReal;
import com.up.math.shape.Rectangle2;

import java.awt.geom.Point2D;

// TODO: Should these be renamed VectorXs to better match the matrix/math conventions?
public class Point2Double extends NeoPoint2<DoubleReal> {
    
    public Point2Double(double x, double y) {
        super(new DoubleReal(x), new DoubleReal(y));
    }

    public Point2Double(double e) {
        this(e, e);
    }
    
    public Point2Double(Point2D p) {
        this(p.getX(), p.getY());
    }

    public Point2Double sum(Point2D p) {
        return new Point2Double(x().d() + p.getX(), y().d() + p.getY());
    }

    public Point2Double sub(Point2D p) {
        return new Point2Double(x().d() - p.getX(), y().d() - p.getY());
    }

    public Point2Double mul(Point2D p) {
        return new Point2Double(x().d() * p.getX(), y().d() * p.getY());
    }

    public Point2Double to(Point2D p) {
        return new Point2Double(p.getX() - x().d(), p.getY() - y().d());
    }

    public double dot(Point2D p) {
        return x().d() * p.getX() + y().d() * p.getY();
    }

    public Point2Double scale(double s) {
        return new Point2Double(x().d() * s, y().d() * s);
    }

    public Point2Double constrain(Rectangle2 rect) {
        return new Point2Double(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x().d())), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y().d())));
    }
}
