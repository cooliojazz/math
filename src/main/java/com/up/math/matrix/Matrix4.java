package com.up.math.matrix;

public record Matrix4(double a, double b, double c, double d,
                      double e, double f, double g, double h,
                      double i, double j, double k, double l,
                      double m, double n, double o, double p) {

//    public double determinant() {
//        return a * e * i + b * f * g + c * d * h - c * e * g - b * d * i - a * f * h;
//    }

    public Matrix4 transpose() {
        return new Matrix4(a, e, i, m, b, f, j, n, c, g, k, o, d, h, l, p);
    }

//    private static final Matrix4 CO_SIGNS = new Matrix4(1, -1, 1, -1, 1, -1, 1, -1, 1);
//
//    public Matrix4 inverse() {
//        Matrix4 other = new Matrix4(new Matrix2(e, f, h, i).determinant(), new Matrix2(d, f, g, i).determinant(), new Matrix2(d, e, g, h).determinant(),
//                                    new Matrix2(b, c, h, i).determinant(), new Matrix2(a, c, g, i).determinant(), new Matrix2(a, b, g, h).determinant(),
//                                    new Matrix2(b, c, e, f).determinant(), new Matrix2(a, c, d, f).determinant(), new Matrix2(a, b, d, e).determinant());
//        Matrix4 cof = other.multiply(CO_SIGNS);
//        return cof.transpose().multiply(1 / determinant());
//    }

//    public Matrix4 multiply(Matrix4 m) {
//        return new Matrix4(a * m.a, b * m.b, c * m.c, d * m.d, e * m.e, f * m.f, g * m.g, h * m.h, i * m.i);
//    }
//
//    public Matrix4 multiply(double s) {
//        return new Matrix4(a * s, b * s, c * s, d * s, e * s, f * s, g * s, h * s, i * s);
//    }
//
//    public Matrix4 compose(Matrix4 m) {
//        return new Matrix4(m.a * a + m.d * b + m.g * c, m.b * a + m.e * b + m.h * c, m.c * a + m.f * b + m.i * c,
//                           m.a * d + m.d * e + m.g * f, m.b * d + m.e * e + m.h * f, m.c * d + m.f * e + m.i * f,
//                           m.a * g + m.d * h + m.g * i, m.b * g + m.e * h + m.h * i, m.c * g + m.f * h + m.i * i);
//    }

//    public Point2 apply(Point4 p) {
//        return new Point4(p.getX() * a + p.getY() * b + c, p.getX() * d + p.getY() * e + f);
//    }

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
//
//    public static Matrix4 offset(Point2 p) {
//        return new Matrix4(1, 0, p.x, 0, 1, p.y, 0, 0, 1);
//    }
//
//    public static Matrix4 rotate(double a) {
//        return new Matrix4(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
//    }
}


