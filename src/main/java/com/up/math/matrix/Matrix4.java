package com.up.math.matrix;

import com.up.math.vector.Point3;
import com.up.math.vector.Point4;

public record Matrix4(double a, double b, double c, double d,
                      double e, double f, double g, double h,
                      double i, double j, double k, double l,
                      double m, double n, double o, double p) {

    // TODO: All 4d matrix operators
    
    public double determinant() {
        return a * new Matrix3(f, g, h, j, k, l, n, o, p).determinant() -
               e * new Matrix3(b, c, d, j, k, l, n, o, p).determinant() + 
               i * new Matrix3(b, c, d, f, g, h, n, o, p).determinant() - 
               m * new Matrix3(b, c, d, f, g, h, j, k, l).determinant();
    }

    public Matrix4 transpose() {
        return new Matrix4(a, e, i, m, b, f, j, n, c, g, k, o, d, h, l, p);
    }
    
    /**
     * 
     * @return the inverted matrix or null if it can't be inverted
     */
    public Matrix4 inverse() {
        double det = determinant();
        if (det == 0) return null;
        Matrix4 cof = new Matrix4(new Matrix3(f, g, h, j, k, l, n, o, p).determinant(), -new Matrix3(e, g, h, i, k, l, m, o, p).determinant(),  new Matrix3(e, f, h, i, j, l, m, n, p).determinant(), -new Matrix3(e, f, g, i, j, k, m, n, o).determinant(),
                                 -new Matrix3(b, c, d, j, k, l, n, o, p).determinant(),  new Matrix3(a, c, d, i, k, l, m, o, p).determinant(), -new Matrix3(a, b, d, i, j, l, m, n, p).determinant(),  new Matrix3(a, b, c, i, j, k, m, n, o).determinant(),
                                  new Matrix3(b, c, d, f, g, h, n, o, p).determinant(), -new Matrix3(a, c, d, e, g, h, m, o, p).determinant(),  new Matrix3(a, b, d, e, f, h, m, n, p).determinant(), -new Matrix3(a, b, c, e, f, g, m, n, o).determinant(),
                                 -new Matrix3(b, c, d, f, g, h, j, k, l).determinant(),  new Matrix3(a, c, d, e, g, h, i, k, l).determinant(), -new Matrix3(a, b, d, e, f, h, i, j, l).determinant(),  new Matrix3(a, b, c, e, f, g, i, j, k).determinant());
        return cof.transpose().multiply(1 / det);
    }

//    public Matrix4 multiply(Matrix4 m) {
//        return new Matrix4(a * m.a, b * m.b, c * m.c, d * m.d, e * m.e, f * m.f, g * m.g, h * m.h, i * m.i);
//    }
//
    public Matrix4 multiply(double s) {
        return new Matrix4(a * s, b * s, c * s, d * s, e * s, f * s, g * s, h * s, i * s, j * s, k * s, l * s, m * s, n * s, o * s, p * s);
    }

    public Matrix4 compose(Matrix4 m4) {
        return new Matrix4(m4.a * a + m4.e * b + m4.i * c + m4.m * d, m4.b * a + m4.f * b + m4.j * c + m4.n * d, m4.c * a + m4.g * b + m4.k * c + m4.o * d, m4.d * a + m4.h * b + m4.l * c + m4.p * d,
                           m4.a * e + m4.e * f + m4.i * g + m4.m * h, m4.b * e + m4.f * f + m4.j * g + m4.n * h, m4.c * e + m4.g * f + m4.k * g + m4.o * h, m4.d * e + m4.h * f + m4.l * g + m4.p * h,
                           m4.a * i + m4.e * j + m4.i * k + m4.m * l, m4.b * i + m4.f * j + m4.j * k + m4.n * l, m4.c * i + m4.g * j + m4.k * k + m4.o * l, m4.d * i + m4.h * j + m4.l * k + m4.p * l,
                           m4.a * m + m4.e * n + m4.i * o + m4.m * p, m4.b * m + m4.f * n + m4.j * o + m4.n * p, m4.c * m + m4.g * n + m4.k * o + m4.o * p, m4.d * m + m4.h * n + m4.l * o + m4.p * p);
    }

    public Point4 apply(Point4 p4) {
        return new Point4(p4.x() * a + p4.y() * b + p4.z() * c + p4.w() * d, p4.x() * e + p4.y() * f + p4.z() * g + p4.w() * h, p4.x() * i + p4.y() * j + p4.z() * k + p4.w() * l, p4.x() * m + p4.y() * n + p4.z() * o + p4.w() * p);
    }

    public static Matrix4 identity() {
        return new Matrix4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

//    public static Matrix4 promote(Matrix2 mat) {
//        return new Matrix4(mat.a(), mat.b(), 0, mat.c(), mat.d(), 0, 0, 0, 1);
//    }
//
//    public static Matrix4 scale(double s) {
//        return new Matrix4(s, 0, 0, 0, s, 0, 0, 0, 1);
//    }
//
//    public static Matrix4 scale(double xs, double ys) {
//        return new Matrix4(xs, 0, 0, 0, ys, 0, 0, 0, 1);
//    }
//
//    public static Matrix4 scale(Point2 s) {
//        return new Matrix4(s.x, 0, 0, 0, s.y, 0, 0, 0, 1);
//    }
//
//    public static Matrix4 offset(double x, double y) {
//        return new Matrix4(1, 0, x, 0, 1, y, 0, 0, 1);
//    }

    public static Matrix4 offset(Point3 p) {
        return new Matrix4(1, 0, 0, p.x(), 0, 1, 0, p.y(), 0, 0, 1, p.z(), 0, 0, 0, 1);
    }
//
//    public static Matrix4 rotate(double a) {
//        return new Matrix4(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
//    }
    
    
    @Override
    public String toString() {
        return "[[" + a + ", " + b + ", " + c + ", " + d + "][" + e + ", " + f + ", " + g + ", " + h + "][" + i + ", " + j + ", " + k + ", " + l + "][" + m + ", " + n + ", " + o + ", " + p + "]]";
    }
}


