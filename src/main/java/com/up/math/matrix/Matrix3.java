package com.up.math.matrix;

import com.up.math.vector.Point2;
import com.up.math.vector.Point3;

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
        if (determinant() == 0) return null;
        Matrix3 other = new Matrix3(new Matrix2(e, f, h, i).determinant(), new Matrix2(d, f, g, i).determinant(), new Matrix2(d, e, g, h).determinant(),
                                    new Matrix2(b, c, h, i).determinant(), new Matrix2(a, c, g, i).determinant(), new Matrix2(a, b, g, h).determinant(),
                                    new Matrix2(b, c, e, f).determinant(), new Matrix2(a, c, d, f).determinant(), new Matrix2(a, b, d, e).determinant());
        Matrix3 cof = other.multiply(CO_SIGNS);
        return cof.transpose().multiply(1 / determinant());
    }

    public Matrix3 add(Matrix3 m) {
        return new Matrix3(a + m.a, b + m.b, c + m.c, d + m.d, e + m.e, f + m.f, g + m.g, h + m.h, i + m.i);
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

    public Point3 apply(Point3 p) {
        return new Point3(p.x() * a + p.y() * b + p.z() * c, p.x() * d + p.y() * e + p.z() * f, p.x() * g + p.y() * h + p.z() * i);
    }

//    public Point2 apply(Point2D p) {
//        return new Point2(p.getX() * a + p.getY() * b + c, p.getX() * d + p.getY() * e + f);
//    }

    public Matrix2 linearMap() {
        return new Matrix2(a, b, d, e);
    }
    
    public AffineMatrix2 asAffine() {
        return new AffineMatrix2(a, b, d, e, c, f);
    }
    
    public Matrix4 homogenous() {
        return new Matrix4(a, b, c, 0, d, e, f, 0, g, h, i, 0, 0, 0, 0, 1);
    }

    public static Matrix3 identity() {
        return new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    public static Matrix3 scale(double s) {
        return new Matrix3(s, 0, 0, 0, s, 0, 0, 0, s);
    }

    public static Matrix3 scale(double xs, double ys, double zs) {
        return new Matrix3(xs, 0, 0, 0, ys, 0, 0, 0, zs);
    }

    public static Matrix3 scale(Point3 s) {
        return new Matrix3(s.x(), 0, 0, 0, s.y(), 0, 0, 0, s.z());
    }
    
    public static Matrix3 rotate(Point3 axis, double a) {
        if (a == 0 || axis.length() == 0) return identity();
        return scale(Math.cos(a))
                .add(axis.crossMatrix().multiply(Math.sin(a)))
                .add(axis.outerProduct(axis).multiply(1 - Math.cos(a)));
    }
    
    public static Matrix3 rotateX(double a) {
        return new Matrix3(1, 0, 0, 0, Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a));
    }
    
    public static Matrix3 rotateY(double a) {
        return new Matrix3(Math.cos(a), 0, Math.sin(a), 0, 1, 0, -Math.sin(a), 0, Math.cos(a));
    }
    
    public static Matrix3 rotateZ(double a) {
        return new Matrix3(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
    }
    
    public static Matrix3 fromAxis(Point3 x, Point3 y, Point3 z) {
        return new Matrix3(x.x(), y.x(), z.x(), x.y(), y.y(), z.y(), x.z(), y.z(), z.z());
    }
}


