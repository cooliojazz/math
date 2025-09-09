package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.number.Complex;
import com.up.math.vector.BigFixedPoint2;
import com.up.math.vector.ComplexPoint2;

import java.awt.geom.Point2D;
import java.util.function.Function;

public record BigFixedMatrix2(BigFixed a, BigFixed b,
                              BigFixed c, BigFixed d) {

    public BigFixed determinant() {
        return a.mult(d).sub(b.mult(c));
    }

    /**
     * The sum of the diagonal elements
     * @return
     */
    public BigFixed trace() {
        return a.add(d);
    }

    public BigFixedMatrix2 sum(BigFixedMatrix2 m) {
        return new BigFixedMatrix2(a.add(m.a), b.add(m.b), c.add(m.c), d.add(m.d));
    }

    public BigFixedMatrix2 mult(BigFixed s) {
        return new BigFixedMatrix2(a.mult(s), b.mult(s), c.mult(s), d.mult(s));
    }

    public BigFixedMatrix2 compose(BigFixedMatrix2 m) {
        return new BigFixedMatrix2(m.a.mult(a).add(m.c.mult(b)), m.b.mult(a).add(m.d.mult(b)), m.a.mult(c).add(m.c.mult(d)), m.b.mult(c).add(m.d.mult(d)));
    }
    
    public BigFixedPoint2 apply(BigFixedPoint2 p) {
        return new BigFixedPoint2(a.mult(p.getX()).add(b.mult(p.getY())), c.mult(p.getX()).add(d.mult(p.getY())));
    }
    
    public BigFixedMatrix2 adjoint() {
        return new BigFixedMatrix2(d, b.negate(), c.negate(), a);
    }
    
    public BigFixedMatrix2 inverse() {
        if (determinant().equals(Complex.ZERO)) return null;
        return adjoint().mult(determinant().inverse());
    }
    
//    public BigFixedMatrix2 exp() {
//        Complex[] es = eigenvalues();
//        return cayleyHamilton(es[0], es[1], Complex::exp);
//    }
//    
//    public BigFixedMatrix2 log() {
//        Complex[] es = eigenvalues();
//        return cayleyHamilton(es[0], es[1], Complex::log);
//    }
//    
//    /**
//     * Takes the eigenvalues and the function to apply
//     * @return
//     */
//    private BigFixedMatrix2 cayleyHamilton(Complex e1, Complex e2, Function<Complex, Complex> fn) {
//        Complex fe1 = fn.apply(e1);
//        Complex fe2 = fn.apply(e2);
//        Complex denom = e1.subtract(e2).inverse();
//        Complex a = e1.mult(fe2).subtract(e2.mult(fe1)).mult(denom);
//        Complex b = fe1.subtract(fe2).mult(denom);
//        
//        return BigFixedMatrix2.identity().mult(a).sum(mult(b));
//    }
//    
//    private Complex[] eigenvalues() {
//        Complex s = trace();
//        Complex d = determinant();
//        
//        Complex inner = s.mult(s).mult(0.25).subtract(d).sqrt();
//        return new Complex[] {s.mult(0.5).add(inner), s.mult(0.5).subtract(inner)};
//    }

    public static BigFixedMatrix2 identity() {
        return new BigFixedMatrix2(BigFixed.fromInt(1), new BigFixed(), new BigFixed(), BigFixed.fromInt(1));
    }
    
//    public ComplexAffineMatrix2 toAffine(ComplexPoint2 offset) {
//        return new ComplexAffineMatrix2(a, b, c, d, offset.getX(), offset.getY());
//    }
//    
//    public Matrix2 real() {
//        return new Matrix2(a.real(), b.real(), c.real(), d.real());
//    }
//    
//    public Matrix2 imag() {
//        return new Matrix2(a.imag(), b.imag(), c.imag(), d.imag());
//    }
//    
//    public Matrix2 magnitude() {
//        return new Matrix2(a.magnitude(), b.magnitude(), c.magnitude(), d.magnitude());
//    }
//    
//    @Override
//    public String toString() {
//        return "[[" + a + ", " + b + "][" + c + ", " + d + "]";
//    }
//    
//    public boolean roundedEquals(BigFixedMatrix2 m, int precision) {
//        return a.roundedEquals(m.a, precision) && b.roundedEquals(m.b, precision) && c.roundedEquals(m.c, precision) && d.roundedEquals(m.d, precision);
//    }
}
