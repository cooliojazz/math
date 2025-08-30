package com.up.math;

import java.awt.geom.Point2D;

public record Matrix3(double a, double b, double c,
                      double d, double e, double f,
                      double g, double h, double i) {

    public double determinant() {
        return a * e * i + b * f * g + c * d * h -
               (c * e * g + b * d * i + a * f * h);
    }

    private static final Matrix3 CO_SIGNS = new Matrix3(1, -1, 1, -1, 1, -1, 1, -1, 1);

    public Matrix3 transpose() {
        return new Matrix3(a, d, g, b, e, h, c, f, i);
    }

    public Matrix3 inverse() {
        Matrix3 other = new Matrix3(new Matrix2(e, f, h, i).determinant(), new Matrix2(d, f, g, i).determinant(), new Matrix2(d, e, g, h).determinant(),
                                    new Matrix2(b, c, h, i).determinant(), new Matrix2(a, c, g, i).determinant(), new Matrix2(a, b, g, h).determinant(),
                                    new Matrix2(b, c, e, f).determinant(), new Matrix2(a, c, d, f).determinant(), new Matrix2(a, b, d, e).determinant());
        Matrix3 cof = other.multiply(CO_SIGNS);
        return cof.transpose().multiply(1 / determinant());
    }

    public Matrix3 multiply(Matrix3 m) {
        return new Matrix3(a * m.a, b * m.b, c * m.c, d * m.d, e * m.e, f * m.f, g * m.g, h * m.h, i * m.i);
    }

    public Matrix3 multiply(double s) {
        return new Matrix3(a * s, b * s, c * s, d * s, e * s, f * s, g * s, h * s, i * s);
    }

    public Matrix3 compose(Matrix3 m) {
        return new Matrix3(m.a * a + m.d * b + m.g * c, m.b * a + m.e * b + m.h * c, m.c * a + m.f * b + m.i * c,
                           m.a * d + m.d * e + m.g * f, m.b * d + m.e * e + m.h * f, m.c * d + m.f * e + m.i * f,
                           m.a * g + m.d * h + m.g * i, m.b * g + m.e * h + m.h * i, m.c * g + m.f * h + m.i * i);
    }

    public Point2 apply(Point2D p) {
        return new Point2(p.getX() * a + p.getY() * b + c, p.getX() * d + p.getY() * e + f);
    }

    public Matrix2 linearMap() {
        return new Matrix2(a, b, d, e);
    }

    public static Matrix3 identity() {
        return new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    public static Matrix3 promote(Matrix2 mat) {
        return new Matrix3(mat.a(), mat.b(), 0, mat.c(), mat.d(), 0, 0, 0, 1);
    }

    public static Matrix3 scale(double s) {
        return new Matrix3(s, 0, 0, 0, s, 0, 0, 0, 1);
    }

    public static Matrix3 scale(double xs, double ys) {
        return new Matrix3(xs, 0, 0, 0, ys, 0, 0, 0, 1);
    }

    public static Matrix3 scale(Point2 s) {
        return new Matrix3(s.x, 0, 0, 0, s.y, 0, 0, 0, 1);
    }

    public static Matrix3 offset(double x, double y) {
        return new Matrix3(1, 0, x, 0, 1, y, 0, 0, 1);
    }

    public static Matrix3 offset(Point2 p) {
        return new Matrix3(1, 0, p.x, 0, 1, p.y, 0, 0, 1);
    }

    public static Matrix3 rotate(double a) {
        return new Matrix3(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
    }
}


