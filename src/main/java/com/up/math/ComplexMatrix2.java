package com.up.math;

import java.awt.geom.Point2D;
import java.util.function.Function;

public record ComplexMatrix2(Complex a, Complex b,
                             Complex c, Complex d) {
    
    public ComplexMatrix2(Matrix2 real) {
        this(new Complex(real.a()), new Complex(real.b()), new Complex(real.c()), new Complex(real.d()));
    }

    public Complex determinant() {
        return a.multiply(d).subtract(b.multiply(c));
    }

    /**
     * The sum of the diagonal elements
     * @return
     */
    public Complex trace() {
        return a.add(d);
    }

    public ComplexMatrix2 sum(ComplexMatrix2 m) {
        return new ComplexMatrix2(a.add(m.a), b.add(m.b), c.add(m.c), d.add(m.d));
    }

    public ComplexMatrix2 multiply(Complex s) {
        return new ComplexMatrix2(a.multiply(s), b.multiply(s), c.multiply(s), d.multiply(s));
    }

    public ComplexMatrix2 compose(ComplexMatrix2 m) {
        return new ComplexMatrix2(m.a.multiply(a).add(m.c.multiply(b)), m.a.multiply(c).add(m.c.multiply(d)), m.b.multiply(a).add(m.d.multiply(b)), m.b.multiply(c).add(m.d.multiply(d)));
    }
    
    public ComplexPoint2 apply(Point2D p) {
        return new ComplexPoint2(a.multiply(p.getX()).add(b.multiply(p.getY())), c.multiply(p.getX()).add(d.multiply(p.getY())));
    }
    
    public ComplexMatrix2 adjoint() {
        return new ComplexMatrix2(d, b.negate(), c.negate(), a);
    }
    
    public ComplexMatrix2 inverse() {
        if (determinant().equals(Complex.ZERO)) return null;
        return adjoint().multiply(determinant().inverse());
    }
    
    public ComplexMatrix2 exp() {
        Complex[] es = eigenvalues();
        return cayleyHamilton(es[0], es[1], Complex::exp);
    }
    
    public ComplexMatrix2 log() {
        Complex[] es = eigenvalues();
        return cayleyHamilton(es[0], es[1], Complex::log);
    }
    
    /**
     * Takes the eigenvalues and the function to apply
     * @return
     */
    private ComplexMatrix2 cayleyHamilton(Complex e1, Complex e2, Function<Complex, Complex> fn) {
        Complex fe1 = fn.apply(e1);
        Complex fe2 = fn.apply(e2);
        Complex denom = e1.subtract(e2).inverse();
        Complex a = e1.multiply(fe2).subtract(e2.multiply(fe1)).multiply(denom);
        Complex b = fe1.subtract(fe2).multiply(denom);
        
        return ComplexMatrix2.identity().multiply(a).sum(multiply(b));
    }
    
    private Complex[] eigenvalues() {
        Complex s = trace();
        Complex d = determinant();
        
        Complex inner = s.multiply(s).multiply(0.25).subtract(d).sqrt();
        return new Complex[] {s.multiply(0.5).add(inner), s.multiply(0.5).subtract(inner)};
    }

    public static ComplexMatrix2 identity() {
        return new ComplexMatrix2(new Complex(1), new Complex(0), new Complex(0), new Complex(1));
    }
    
    public Matrix2 real() {
        return new Matrix2(a.real(), b.real(), c.real(), d.real());
    }
    
    public Matrix2 imag() {
        return new Matrix2(a.imag(), b.imag(), c.imag(), d.imag());
    }
    
    public Matrix2 magnitude() {
        return new Matrix2(a.magnitude(), b.magnitude(), c.magnitude(), d.magnitude());
    }
    
    @Override
    public String toString() {
        return "[[" + a + ", " + b + "][" + c + ", " + d + "]";
    }
}
