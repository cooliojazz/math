package com.up.math.matrix;

import com.up.math.number.Complex;
import com.up.math.vector.Point2;

public record AffineMatrix2(double a, double b, double c, double d, double x, double y) {

    public AffineMatrix2 compose(AffineMatrix2 m) {
        return new AffineMatrix2(m.a * a + m.c * b, m.b * a + m.d * b, m.x * a + m.y * b + x,
                           m.a * c + m.c * d, m.b * c + m.d * d, m.x * c + m.y * d + y);
    }
    
    public Point2 apply(Point2 p) {
        return new Point2(p.x * a + p.y * b + x, p.x * c + p.y * d + y);
    }
    
    public AffineMatrix2 inverse() {
        return toSquare().inverse().asAffine();
    }
    
    public ComplexAffineMatrix2 exp() {
        return asComplex().exp();
    }
    
    public ComplexAffineMatrix2 log() {
        return asComplex().log();
    }
    

    public Matrix2 linearMap() {
        return new Matrix2(a, b, c, d);
    }
    
    public Matrix3 toSquare() {
        return new Matrix3(a, b, x, c, d, y, 0, 0, 1);
    }
    
    public ComplexAffineMatrix2 asComplex() {
        return new ComplexAffineMatrix2(new Complex(a), new Complex(b), new Complex(c), new Complex(d), new Complex(x), new Complex(y));
    }
    
    public static AffineMatrix2 identity() {
        return new AffineMatrix2(1, 0, 0, 1, 0, 0);
    }
    
    public static AffineMatrix2 scale(double s) {
        return new AffineMatrix2(s, 0, 0, s, 0, 0);
    }

    public static AffineMatrix2 scale(double xs, double ys) {
        return new AffineMatrix2(xs, 0, 0, ys, 0, 0);
    }

    public static AffineMatrix2 scale(Point2 s) {
        return new AffineMatrix2(s.x, 0, 0, s.y, 0, 0);
    }

    public static AffineMatrix2 offset(double x, double y) {
        return new AffineMatrix2(1, 0, 0, 1, x, y);
    }

    public static AffineMatrix2 offset(Point2 p) {
        return new AffineMatrix2(1, 0, 0, 1, p.x, p.y);
    }

    public static AffineMatrix2 rotate(double a) {
        return new AffineMatrix2(Math.cos(a), -Math.sin(a), Math.sin(a), Math.cos(a), 0, 0);
    }
}
