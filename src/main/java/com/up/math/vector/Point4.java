package com.up.math.vector;

import com.up.math.Util;

public record Point4(double x, double y, double z, double w) {

    public Point4(double e) {
        this(e, e, e, e);
    }

    public Point4 sum(Point4 p) {
        return new Point4(x + p.x(), y + p.y(), z + p.z(), w + p.w());
    }

    public Point4 sub(Point4 p) {
        return new Point4(x - p.x(), y - p.y(), z - p.z(), w - p.w());
    }

    public Point4 mul(Point4 p) {
        return new Point4(x * p.x(), y * p.y(), z * p.z(), w * p.w());
    }

    public Point4 to(Point4 p) {
        return new Point4(p.x() - x, p.y() - y, p.z() - z, p.w() - w);
    }

    public double dot(Point4 p) {
        return x * p.x() + y * p.y() + z * p.z() + w * p.w();
    }

    public Point4 scale(double s) {
        return new Point4(x * s, y * s, z * s, w * s);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Point4 abs() {
        return new Point4(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w));
    }

    public Point4 normalized() {
        double len = length();
        return new Point4(x / len, y / len, z / len, w / len);
    }
    
    public Point3 toPoint3() {
        return new Point3(x, y, z);
    }
    
    public boolean roundedEquals(Point4 p, int precision) {
        return Util.roundedEquals(x, p.x, precision) && Util.roundedEquals(y, p.y, precision) && Util.roundedEquals(z, p.z, precision) && Util.roundedEquals(w, p.w, precision);
    }
}
