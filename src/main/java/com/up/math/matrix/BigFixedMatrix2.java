package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.number.ComplexBigFixed;
import com.up.math.number.IntFixed;
import com.up.math.number.Complex;
import com.up.math.vector.BigFixedPoint2;

import java.util.function.Supplier;

public record BigFixedMatrix2<T extends BigFixed<T>>(T a, T b,
                                                  T c, T d) {

    public T determinant() {
        return a.mult(d).sub(b.mult(c));
    }

    /**
     * The sum of the diagonal elements
     * @return
     */
    public T trace() {
        return a.add(d);
    }

    public BigFixedMatrix2<T> sum(BigFixedMatrix2<T> m) {
        return new BigFixedMatrix2<>(a.add(m.a), b.add(m.b), c.add(m.c), d.add(m.d));
    }

    public BigFixedMatrix2<T> mult(T s) {
        return new BigFixedMatrix2<>(a.mult(s), b.mult(s), c.mult(s), d.mult(s));
    }

    public BigFixedMatrix2<T> compose(BigFixedMatrix2<T> m) {
        return new BigFixedMatrix2<>(m.a.mult(a).add(m.c.mult(b)), m.b.mult(a).add(m.d.mult(b)), m.a.mult(c).add(m.c.mult(d)), m.b.mult(c).add(m.d.mult(d)));
    }
    
    public BigFixedPoint2<T> apply(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<>(a.mult(p.getX()).add(b.mult(p.getY())), c.mult(p.getX()).add(d.mult(p.getY())));
    }
    
    public BigFixedMatrix2<T> adjoint() {
        return new BigFixedMatrix2<>(d, b.negate(), c.negate(), a);
    }
    
    public BigFixedMatrix2<T> inverse() {
        if (determinant().equals(ComplexBigFixed.zero(a::zero))) return null;
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

    public static <T extends BigFixed<T>> BigFixedMatrix2<T> identity(Supplier<T> ts) {
        T z = ts.get();
        return new BigFixedMatrix2<>(z.one(), z, z, z.one());
    }
    
//    public ComplexAffineMatrix2 toAffine(ComplexPoint2 offset) {
//        return new ComplexAffineMatrix2(a, b, c, d, offset.getX(), offset.getY());
//    }
//    
    @Override
    public String toString() {
        return "[[" + a.toDouble() + ", " + b.toDouble() + "][" + c.toDouble() + ", " + d.toDouble() + "]";
    }
//    
//    public boolean roundedEquals(BigFixedMatrix2 m, int precision) {
//        return a.roundedEquals(m.a, precision) && b.roundedEquals(m.b, precision) && c.roundedEquals(m.c, precision) && d.roundedEquals(m.d, precision);
//    }
}
