package com.up.math.matrix;

import com.up.math.number.Complex;

public record AffineMatrix2(double a, double b, double c, double d, double x, double y) {

    public AffineMatrix2 compose(AffineMatrix2 m) {
        return new AffineMatrix2(m.a * a + m.c * b, m.b * a + m.d * b, m.x * a + m.y * b + x,
                           m.a * c + m.c * d, m.b * c + m.d * d, m.x * c + m.y * d + y);
    }
    
    public ComplexAffineMatrix2 exp() {
        return asComplex().exp();
    }
    
    public ComplexAffineMatrix2 log() {
        return asComplex().log();
    }
    
    public Matrix3 toSquare() {
        return new Matrix3(a, b, x, c, d, y, 0, 0, 1);
    }
    
    public ComplexAffineMatrix2 asComplex() {
        return new ComplexAffineMatrix2(new Complex(a), new Complex(b), new Complex(c), new Complex(d), new Complex(x), new Complex(y));
    }
}
