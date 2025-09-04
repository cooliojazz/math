package com.up.math.matrix;

import com.up.math.Complex;
import com.up.math.vector.Point2;

import java.awt.geom.Point2D;

public record Matrix2(double a, double b,
                      double c, double d) {

    public double determinant() {
        return a * d - b * c;
    }
    
    /**
     * The sum of the diagonal elements
     * @return
     */
    public double trace() {
        return a + d;
    }

    public Matrix2 multiply(double s) {
        return new Matrix2(a * s, b * s, c * s, d * s);
    }

    public Matrix2 compose(Matrix2 m) {
        return new Matrix2(m.a * a + m.c * b, m.b * a + m.d * b, m.a * c + m.c * d, m.b * c + m.d * d);
    }

    public Point2 apply(Point2D p) {
        return new Point2(p.getX() * a + p.getY() * b, p.getX() * c + p.getY() * d);
    }
    
    public Matrix2 adjoint() {
        return new Matrix2(d, -b, -c, a);
    }
    
    public Matrix2 inverse() {
        if (determinant() == 0) return null;
        return adjoint().multiply(1 / determinant());
    }
    
    public Matrix4 kroneker(Matrix2 m) {
        return new Matrix4(a * m.a, a * m.b, b * m.a, b * m.b,
                           a * m.c, a * m.d, b * m.c, b * m.d,
                           c * m.a, c * m.b, d * m.a, d * m.b,
                          c * m.c, c * m.d, d * m.c, d * m.d);
    }

    public Matrix3 promote() {
        return new Matrix3(a, b, 0, c, d, 0, 0, 0, 1);
    }
    
    public ComplexMatrix2 asComplex() {
        return new ComplexMatrix2(new Complex(a), new Complex(b), new Complex(c), new Complex(d));
    }
    
    public AffineMatrix2 toAffine(Point2 offset) {
        return new AffineMatrix2(a, b, c, d, offset.getX(), offset.getY());
    }
    
    @Override
    public String toString() {
        return "[[" + a + ", " + b + "][" + c + ", " + d + "]";
    }
    
    /**
     * Computes d^this
     * @return the exponentiated matrix
     */
    public ComplexMatrix2 exp() {
        return new ComplexMatrix2(this).exp();
    }
    
    public ComplexMatrix2 log() {
        return new ComplexMatrix2(this).log();
    }

    public static Matrix2 identity() {
        return new Matrix2(1, 0, 0, 1);
    }

    public static Matrix2 scale(double s) {
        return new Matrix2(s, 0, 0, s);
    }
    
    public static Matrix2 scale(Point2 s) {
        return new Matrix2(s.x, 0, 0, s.y);
    }
    
    public static Matrix2 scale(double xs, double ys) {
        return new Matrix2(xs, 0, 0, ys);
    }

    public static Matrix2 rotate(double a) {
        return new Matrix2(Math.cos(a), -Math.sin(a), Math.sin(a), Math.cos(a));
    }

    public static Matrix2 shear(Point2 dir) {
        return new Matrix2(1 + dir.x * dir.y, dir.x, dir.y, 1);
    }
}
