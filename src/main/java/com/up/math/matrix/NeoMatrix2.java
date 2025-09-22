package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.number.Real;
import com.up.math.number.NeoComplex;
import com.up.math.vector.NeoPoint2;

import java.util.function.Supplier;

public record NeoMatrix2<T extends Real<T>>(T a, T b,
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

    public NeoMatrix2<T> sum(NeoMatrix2<T> m) {
        return new NeoMatrix2<>(a.add(m.a), b.add(m.b), c.add(m.c), d.add(m.d));
    }

    public NeoMatrix2<T> mult(T s) {
        return new NeoMatrix2<>(a.mult(s), b.mult(s), c.mult(s), d.mult(s));
    }

    public NeoMatrix2<T> compose(NeoMatrix2<T> m) {
        return new NeoMatrix2<>(m.a.mult(a).add(m.c.mult(b)), m.b.mult(a).add(m.d.mult(b)), m.a.mult(c).add(m.c.mult(d)), m.b.mult(c).add(m.d.mult(d)));
    }
    
    public NeoPoint2<T> apply(NeoPoint2<T> p) {
        return new NeoPoint2<>(a.mult(p.x()).add(b.mult(p.y())), c.mult(p.x()).add(d.mult(p.y())));
    }
    
    public NeoMatrix2<T> adjoint() {
        return new NeoMatrix2<>(d, b.negate(), c.negate(), a);
    }
    
    public NeoMatrix2<T> inverse() {
        if (determinant().equals(NeoComplex.zero(a::zero))) return null;
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
//        Complex f = determinant();
//        
//        Complex inner = s.mult(s).mult(0.25).subtract(f).sqrt();
//        return new Complex[] {s.mult(0.5).add(inner), s.mult(0.5).subtract(inner)};
//    }

    public static <T extends BigFixed<T>> NeoMatrix2<T> identity(Supplier<T> ts) {
        T z = ts.get();
        return new NeoMatrix2<>(z.one(), z, z, z.one());
    }
    
//    public ComplexAffineMatrix2 toAffine(ComplexPoint2 offset) {
//        return new ComplexAffineMatrix2(a, b, c, f, offset.getX(), offset.getY());
//    }

//    @Override
//    public String toString() {
//        return "[[" + a.toDouble() + ", " + b.toDouble() + "][" + c.toDouble() + ", " + f.toDouble() + "]";
//    }

//    public boolean roundedEquals(BigFixedMatrix2 m, int precision) {
//        return a.roundedEquals(m.a, precision) && b.roundedEquals(m.b, precision) && c.roundedEquals(m.c, precision) && f.roundedEquals(m.f, precision);
//    }
}
