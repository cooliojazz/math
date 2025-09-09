package com.up.math.matrix;

import com.up.math.number.Complex;

public record ComplexMatrix4(Complex a, Complex b, Complex c, Complex d,
                             Complex e, Complex f, Complex g, Complex h,
                             Complex i, Complex j, Complex k, Complex l,
                             Complex m, Complex n, Complex o, Complex p) {

    // TODO: All 4d matrix operators
    
    public Complex determinant() {
        return a.multiply(new ComplexMatrix3(f, g, h, j, k, l, n, o, p).determinant())
            .subtract(e.multiply(new ComplexMatrix3(b, c, d, j, k, l, n, o, p).determinant()))
            .add(i.multiply(new ComplexMatrix3(b, c, d, f, g, h, n, o, p).determinant()))
            .subtract(m.multiply(new ComplexMatrix3(b, c, d, f, g, h, j, k, l).determinant()));
    }

    public ComplexMatrix4 transpose() {
        return new ComplexMatrix4(a, e, i, m, b, f, j, n, c, g, k, o, d, h, l, p);
    }
    
    /**
     * 
     * @return the inverted matrix or null if it can't be inverted
     */
    public ComplexMatrix4 inverse() {
        Complex det = determinant();
        if (det.equals(new Complex(0))) return null;
        ComplexMatrix4 cof = new ComplexMatrix4(new ComplexMatrix3(f, g, h, j, k, l, n, o, p).determinant(), new ComplexMatrix3(e, g, h, i, k, l, m, o, p).determinant().negate(), new ComplexMatrix3(e, f, h, i, j, l, m, n, p).determinant(), new ComplexMatrix3(e, f, g, i, j, k, m, n, o).determinant().negate(),
                                                new ComplexMatrix3(b, c, d, j, k, l, n, o, p).determinant().negate(), new ComplexMatrix3(a, c, d, i, k, l, m, o, p).determinant(), new ComplexMatrix3(a, b, d, i, j, l, m, n, p).determinant().negate(), new ComplexMatrix3(a, b, c, i, j, k, m, n, o).determinant(),
                                                new ComplexMatrix3(b, c, d, f, g, h, n, o, p).determinant(), new ComplexMatrix3(a, c, d, e, g, h, m, o, p).determinant().negate(), new ComplexMatrix3(a, b, d, e, f, h, m, n, p).determinant(), new ComplexMatrix3(a, b, c, e, f, g, m, n, o).determinant().negate(),
                                                new ComplexMatrix3(b, c, d, f, g, h, j, k, l).determinant().negate(), new ComplexMatrix3(a, c, d, e, g, h, i, k, l).determinant(), new ComplexMatrix3(a, b, d, e, f, h, i, j, l).determinant().negate(), new ComplexMatrix3(a, b, c, e, f, g, i, j, k).determinant());
        return cof.transpose().multiply(det.inverse());
    }

//    public Matrix4 multiply(Matrix4 m) {
//        return new Matrix4(a * m.a, b * m.b, c * m.c, d * m.d, e * m.e, f * m.f, g * m.g, h * m.h, i * m.i);
//    }
//
    public ComplexMatrix4 multiply(Complex s) {
        return new ComplexMatrix4(a.multiply(s), b.multiply(s), c.multiply(s), d.multiply(s), e.multiply(s), f.multiply(s), g.multiply(s), h.multiply(s), i.multiply(s), j.multiply(s), k.multiply(s), l.multiply(s), m.multiply(s), n.multiply(s), o.multiply(s), p.multiply(s));
    }
//
//    public Matrix4 compose(Matrix4 m) {
//        return new Matrix4(m.a * a + m.d * b + m.g * c, m.b * a + m.e * b + m.h * c, m.c * a + m.f * b + m.i * c,
//                           m.a * d + m.d * e + m.g * f, m.b * d + m.e * e + m.h * f, m.c * d + m.f * e + m.i * f,
//                           m.a * g + m.d * h + m.g * i, m.b * g + m.e * h + m.h * i, m.c * g + m.f * h + m.i * i);
//    }

//    public Point2 apply(Point4 p) {
//        return new Point4(p.getX() * a + p.getY() * b + c, p.getX() * d + p.getY() * e + f);
//    }

//    public static ComplexMatrix4 identity() {
//        return new ComplexMatrix4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
//    }

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
    
    
    @Override
    public String toString() {
        return "[[" + a + ", " + b + ", " + c + ", " + d + "][" + e + ", " + f + ", " + g + ", " + h + "][" + i + ", " + j + ", " + k + ", " + l + "][" + m + ", " + n + ", " + o + ", " + p + "]]";
    }
}


