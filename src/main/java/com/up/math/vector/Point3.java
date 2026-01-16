package com.up.math.vector;

import com.up.math.Util;
import com.up.math.matrix.Matrix3;

import java.awt.*;

public record Point3(double x, double y, double z) {

    public Point3(double e) {
        this(e, e, e);
    }

    public Point3 sum(Point3 p) {
        return new Point3(x + p.x(), y + p.y(), z + p.z());
    }

    public Point3 sum(double d) {
        return new Point3(x + d, y + d, z + d);
    }

    public Point3 sub(Point3 p) {
        return new Point3(x - p.x(), y - p.y(), z - p.z());
    }

    public Point3 mul(Point3 p) {
        return new Point3(x * p.x(), y * p.y(), z * p.z());
    }

    public Point3 to(Point3 p) {
        return new Point3(p.x() - x, p.y() - y, p.z() - z);
    }

    public double dot(Point3 p) {
        return x * p.x() + y * p.y() + z * p.z();
    }

    public Point3 cross(Point3 p) {
        return new Point3(y() * p.z() - p.y() * z(), z() * p.x() - p.z() * x(), x() * p.y() - p.x() * y());
    }
	
    public Matrix3 crossMatrix() {
        return new Matrix3(0, -z, y, z, 0, -x, -y, x, 0);
    }
	
    public Matrix3 outerProduct(Point3 p) {
        return new Matrix3(x * p.x, x * p.y, x * p.z, y * p.x, y * p.y, y * p.z, z * p.x, z * p.y, z * p.z);
    }

    public Point3 scale(double s) {
        return new Point3(x * s, y * s, z * s);
    }
    
    public Point3 clamp(double min, double max) {
        return new Point3(Math.min(max, Math.max(min, x)), Math.min(max, Math.max(min, y)), Math.min(max, Math.max(min, z)));
    }
    
    public Point3 clampX(double min, double max) {
        return new Point3(Math.min(max, Math.max(min, x)), y, z);
    }
    
    public Point3 clampY(double min, double max) {
        return new Point3(x, Math.min(max, Math.max(min, y)), z);
    }
    
    public Point3 clampZ(double min, double max) {
        return new Point3(x, y, Math.min(max, Math.max(min, z)));
    }

    // TODO: Needs cube object for bounds
//    public Point3 constrain(Rectangle2 rect) {
//        return new Point3(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
//    }
    
    public Point3 negate() {
        return new Point3(-x, -y, -z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Point3 abs() {
        return new Point3(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Point3 normalized() {
        double len = length();
        return new Point3(x / len, y / len, z / len);
    }
    
    public Point4 homogenous() {
        return toPoint4(1);
    }
    
    public Point4 toPoint4(double w) {
        return new Point4(x, y, z, w);
    }
    
    public Point2 toPoint2() {
        return new Point2(x, y);
    }
    
    public static Point3 fromColor(Color c) {
        return new Point3(c.getRed(), c.getGreen(), c.getBlue()).scale(1. / 255);
    }
    
    public Color toColor() {
        return new Color((float)x, (float)y, (float)z);
    }
    
    public boolean roundedEquals(Point3 p, int precision) {
        return Util.roundedEquals(x, p.x, precision) && Util.roundedEquals(y, p.y, precision) && Util.roundedEquals(z, p.z, precision);
    }
}
