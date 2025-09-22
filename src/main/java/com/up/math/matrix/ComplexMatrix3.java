package com.up.math.matrix;

import com.up.math.number.Complex;

public record ComplexMatrix3(Complex a, Complex b, Complex c,
                             Complex d, Complex e, Complex f,
                             Complex g, Complex h, Complex i) {

    public Complex determinant() {
        return a.multiply(e).multiply(i).add(b.multiply(f).multiply(g)).add(c.multiply(d).multiply(h)).subtract(
               c.multiply(e).multiply(g).add(b.multiply(d).multiply(i)).add(a.multiply(f).multiply(h)));
    }

    public ComplexMatrix3 transpose() {
        return new ComplexMatrix3(a, d, g, b, e, h, c, f, i);
    }

    public ComplexMatrix3 inverse() {
        if (determinant().equals(new Complex(0))) return null;
        ComplexMatrix3 cof = new ComplexMatrix3(new ComplexMatrix2(e, f, h, i).determinant(), new ComplexMatrix2(d, f, g, i).determinant().negate(), new ComplexMatrix2(d, e, g, h).determinant(),
                                                new ComplexMatrix2(b, c, h, i).determinant().negate(), new ComplexMatrix2(a, c, g, i).determinant(), new ComplexMatrix2(a, b, g, h).determinant().negate(),
                                                new ComplexMatrix2(b, c, e, f).determinant(), new ComplexMatrix2(a, c, d, f).determinant().negate(), new ComplexMatrix2(a, b, d, e).determinant());
        return cof.transpose().multiply(determinant().inverse());
    }

//    public ComplexMatrix3 multiply(ComplexMatrix3 m) {
//        return new ComplexMatrix3(a * m.a, b * m.b, c * m.c, f * m.f, e * m.e, f * m.f, g * m.g, h * m.h, i * m.i);
//    }

    public ComplexMatrix3 multiply(Complex s) {
        return new ComplexMatrix3(a.multiply(s), b.multiply(s), c.multiply(s), d.multiply(s), e.multiply(s), f.multiply(s), g.multiply(s), h.multiply(s), i.multiply(s));
    }

//    public ComplexMatrix3 compose(ComplexMatrix3 m) {
//        return new ComplexMatrix3(m.a * a + m.f * b + m.g * c, m.b * a + m.e * b + m.h * c, m.c * a + m.f * b + m.i * c,
//                           m.a * f + m.f * e + m.g * f, m.b * f + m.e * e + m.h * f, m.c * f + m.f * e + m.i * f,
//                           m.a * g + m.f * h + m.g * i, m.b * g + m.e * h + m.h * i, m.c * g + m.f * h + m.i * i);
//    }
//
//    public Point3 apply(Point3 p) {
//        return new Point3(p.x() * a + p.y() * b + p.z() * c, p.x() * f + p.y() * e + p.z() * f, p.x() * g + p.y() * h + p.z() * i);
//    }
//
//    public Point2 apply(Point2D p) {
//        return new Point2(p.getX() * a + p.getY() * b + c, p.getX() * f + p.getY() * e + f);
//    }

    public ComplexMatrix2 linearMap() {
        return new ComplexMatrix2(a, b, d, e);
    }
    
    public ComplexAffineMatrix2 asAffine() {
        return new ComplexAffineMatrix2(a, b, d, e, c, f);
    }

    public static ComplexMatrix3 identity() {
        return new ComplexMatrix3(new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(1));
    }

    public static ComplexMatrix3 promote(ComplexMatrix2 mat) {
        return new ComplexMatrix3(mat.a(), mat.b(), new Complex(0), mat.c(), mat.d(), new Complex(0), new Complex(0), new Complex(0), new Complex(1));
    }

//    public static ComplexMatrix3 scale(double s) {
//        return new ComplexMatrix3(s, 0, 0, 0, s, 0, 0, 0, 1);
//    }
//
//    public static ComplexMatrix3 scale(double xs, double ys) {
//        return new ComplexMatrix3(xs, 0, 0, 0, ys, 0, 0, 0, 1);
//    }
//
//    public static ComplexMatrix3 scale(Point2 s) {
//        return new ComplexMatrix3(s.x, 0, 0, 0, s.y, 0, 0, 0, 1);
//    }
//
//    public static ComplexMatrix3 offset(double x, double y) {
//        return new ComplexMatrix3(1, 0, x, 0, 1, y, 0, 0, 1);
//    }
//
//    public static ComplexMatrix3 offset(Point2 p) {
//        return new ComplexMatrix3(1, 0, p.x, 0, 1, p.y, 0, 0, 1);
//    }
//
//    public static ComplexMatrix3 rotate(double a) {
//        return new ComplexMatrix3(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
//    }
}


