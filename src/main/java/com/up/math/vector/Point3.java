package com.up.math.vector;

import com.up.math.Util;

public record Point3(double x, double y, double z) {

    public Point3(double e) {
        this(e, e, e);
    }

    public Point3 sum(Point3 p) {
        return new Point3(x + p.x(), y + p.y(), z + p.z());
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

    // TODO: implement
    public double cross(Point3 p) {
        return Double.NaN;
    }

    public Point3 scale(double s) {
        return new Point3(x * s, y * s, z * s);
    }

    // TODO: Needs cube object for bounds
//    public Point3 constrain(Rectangle2 rect) {
//        return new Point3(Math.max(rect.getStart().x, Math.min(rect.getEnd().x, x)), Math.max(rect.getStart().y, Math.min(rect.getEnd().y, y)));
//    }

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
    
    public boolean roundedEquals(Point3 p, int precision) {
        return Util.roundedEquals(x, p.x, precision) && Util.roundedEquals(y, p.y, precision) && Util.roundedEquals(z, p.z, precision);
    }
}
