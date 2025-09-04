package com.up.math.matrix;

import com.up.math.Complex;

public record AffineMatrix2(double a, double b, double c, double d, double x, double y) {
    
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
