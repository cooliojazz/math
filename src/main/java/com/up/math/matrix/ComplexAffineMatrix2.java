package com.up.math.matrix;

import com.up.math.number.Complex;
import com.up.math.vector.ComplexPoint2;

public record ComplexAffineMatrix2(Complex a, Complex b, Complex c, Complex d, Complex x, Complex y) {
    
    public Complex determinant() {
        return a.multiply(d).subtract(b.multiply(c));
    }
    
    //TODO: Could this be optimised more for an affine inverse?
    // Should this actually use [0, 0, 0] as the bottom row instead of [0, 0, 1]? does it matter?
    public ComplexMatrix3 inverse() {
        if (determinant().equals(new Complex(0))) return null;
        ComplexMatrix3 cof = new ComplexMatrix3(new ComplexMatrix2(d, y, new Complex(0), new Complex(1)).determinant(), new ComplexMatrix2(c, y, new Complex(0), new Complex(1)).determinant().negate(), new ComplexMatrix2(c, d, new Complex(0), new Complex(0)).determinant(),
                                                new ComplexMatrix2(b, x, new Complex(0), new Complex(1)).determinant().negate(), new ComplexMatrix2(a, x, new Complex(0), new Complex(1)).determinant(), new ComplexMatrix2(a, b, new Complex(0), new Complex(0)).determinant().negate(),
                                                new ComplexMatrix2(b, x, d, y).determinant(), new ComplexMatrix2(a, x, c, y).determinant().negate(), new ComplexMatrix2(a, b, c, d).determinant());
        return cof.transpose().multiply(determinant().inverse());
    }
    
    // This and exp dont seem to have the correct values for the right block yet?
    public ComplexAffineMatrix2 exp() {
        ComplexMatrix2 m = new ComplexMatrix2(a, b, c, d);
        ComplexMatrix2 lExp = m.exp();
        ComplexPoint2 rExp = J(m).apply(new ComplexPoint2(x, y));
        return new ComplexAffineMatrix2(lExp.a(), lExp.b(), lExp.c(), lExp.d(), rExp.getX(), rExp.getY());
    }

    public ComplexAffineMatrix2 log() {
        ComplexMatrix2 m = new ComplexMatrix2(a, b, c, d);
        ComplexMatrix2 lLog = m.log();
        // TODO: This could be simplified by direct substitution at the cost of less clean code than using the J function
        ComplexPoint2 rLog = J(lLog).inverse().apply(new ComplexPoint2(x, y));
        return new ComplexAffineMatrix2(lLog.a(), lLog.b(), lLog.c(), lLog.d(), rLog.getX(), rLog.getY());
    }
    
    // Special transformation for the translation vector from affine to group
    // TODO: Name this better if its staying
    private static ComplexMatrix2 J(ComplexMatrix2 m) {
        ComplexMatrix2 mi = m.inverse();
        if (mi == null) return null;
        return mi.compose(m.exp().sum(ComplexMatrix2.identity().multiply(new Complex(-1))));
    }

    public ComplexMatrix3 toSquare() {
        return new ComplexMatrix3(a, b, x, c, d, y, new Complex(0), new Complex(0), new Complex(1));
    }
    
    public boolean roundedEquals(ComplexAffineMatrix2 m, int precision) {
        return a.roundedEquals(m.a, precision) && b.roundedEquals(m.b, precision) && c.roundedEquals(m.c, precision) && d.roundedEquals(m.d, precision) && x.roundedEquals(m.x, precision) && y.roundedEquals(m.y, precision);
    }
    
}
