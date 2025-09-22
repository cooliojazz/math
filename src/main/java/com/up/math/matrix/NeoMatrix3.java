package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.number.Real;
import com.up.math.vector.NeoPoint2;

import java.util.function.Supplier;

public record NeoMatrix3<T extends Real<T>>(T a, T b, T c,
                                            T d, T e, T f,
                                            T g, T h, T i) {

    public T determinant() {
        return a.mult(e).mult(i).add(b.mult(f).mult(g)).add(c.mult(d).mult(h))
                .sub(c.mult(e).mult(g).add(b.mult(d).mult(i)).add(a.mult(f).mult(h)));
    }

    public NeoMatrix3<T> transpose() {
        return new NeoMatrix3<T>(a, d, g, b, e, h, c, f, i);
    }
    public NeoMatrix3<T> inverse() {
        if (determinant().equals(a.zero())) return null;
        NeoMatrix3<T> cof = new NeoMatrix3<>(new NeoMatrix2<>(e, f, h, i).determinant(), new NeoMatrix2<>(d, f, g, i).determinant().negate(), new NeoMatrix2<>(d, e, g, h).determinant(),
                                             new NeoMatrix2<>(b, c, h, i).determinant().negate(), new NeoMatrix2<>(a, c, g, i).determinant(), new NeoMatrix2<>(a, b, g, h).determinant().negate(),
                                             new NeoMatrix2<>(b, c, e, f).determinant(), new NeoMatrix2<>(a, c, d, f).determinant().negate(), new NeoMatrix2<>(a, b, d, e).determinant());
        return cof.transpose().multiply(determinant().inverse());
    }

    public NeoMatrix3<T> multiply(NeoMatrix3<T> m) {
        return new NeoMatrix3<>(a.mult(m.a), b.mult(m.b), c.mult(m.c), d.mult(m.d), e.mult(m.e), f.mult(m.f), g.mult(m.g), h.mult(m.h), i.mult(m.i));
    }

    public NeoMatrix3<T> multiply(T s) {
        return new NeoMatrix3<>(a.mult(s), b.mult(s), c.mult(s), d.mult(s), e.mult(s), f.mult(s), g.mult(s), h.mult(s), i.mult(s));
    }

    public NeoMatrix3<T> compose(NeoMatrix3<T> m) {
        return new NeoMatrix3<>(m.a.mult(a).add(m.d.mult(b)).add(m.g.mult(c)), m.b.mult(a).add(m.e.mult(b)).add(m.h.mult(c)), m.c.mult(a).add(m.f.mult(b)).add(m.i.mult(c)),
                                m.a.mult(d).add(m.d.mult(e)).add(m.g.mult(f)), m.b.mult(d).add(m.e.mult(e)).add(m.h.mult(f)), m.c.mult(d).add(m.f.mult(e)).add(m.i.mult(f)),
                                m.a.mult(g).add(m.d.mult(h)).add(m.g.mult(i)), m.b.mult(g).add(m.e.mult(h)).add(m.h.mult(i)), m.c.mult(g).add(m.f.mult(h)).add(m.i.mult(i)));
    }
//
//    public Point3 apply(Point3 p) {
//        return new Point3(p.x().mult(a).add(p.y().mult(b)).add(p.z().mult(c),) p.x().mult(f).add(p.y().mult(e)).add(p.z().mult(f),) p.x().mult(g).add(p.y().mult(h)).add(p.z().mult(i));)
//    }
//
    public NeoPoint2<T> apply(NeoPoint2<T> p) {
        return new NeoPoint2<>(p.x().mult(a).add(p.y().mult(b)).add(c), p.x().mult(d).add(p.y().mult(e)).add(f));
    }

    public NeoMatrix2<T> linearMap() {
        return new NeoMatrix2<>(a, b, d, e);
    }

//    public AffineMatrix2 asAffine() {
//        return new AffineMatrix2(a, b, f, e, c, f);
//    }
    
//    public static <T extends Real<T>> NeoMatrix3<T> fromMatrix3(Matrix3 m, Class<T> type) {
//        return new NeoMatrix3<>(BigFixed.fromDouble(m.a(), type), BigFixed.fromDouble(m.b(), type), BigFixed.fromDouble(m.c(), type),
//                                BigFixed.fromDouble(m.f(), type), BigFixed.fromDouble(m.e(), type), BigFixed.fromDouble(m.f(), type),
//                                BigFixed.fromDouble(m.g(), type), BigFixed.fromDouble(m.h(), type), BigFixed.fromDouble(m.i(), type));
//    }

    // TODO: These should be cleaned up to be relevant to 3d and relevant ones moved to affine classes
    public static <T extends Real<T>> NeoMatrix3<T> identity(Supplier<T> ts) {
        T z = ts.get();
        return new NeoMatrix3<>(z.one(), z, z, z, z.one(), z, z, z, z.one());
    }

    public static <T extends Real<T>> NeoMatrix3<T> scale(T s) {
        return new NeoMatrix3<>(s, s.zero(), s.zero(), s.zero(), s, s.zero(), s.zero(), s.zero(), s.one());
    }
    
    public static <T extends Real<T>> NeoMatrix3<T> scale(T xs, T ys) {
        return new NeoMatrix3<T>(xs, xs.zero(), xs.zero(), xs.zero(), ys, xs.zero(), xs.zero(), xs.zero(), xs.one());
    }

    public static <T extends Real<T>> NeoMatrix3<T> scale(NeoPoint2<T> s) {
        T x = s.x();
        return new NeoMatrix3<T>(x, x.zero(), x.zero(), x.zero(), s.y(), x.zero(), x.zero(), x.zero(), x.one());
    }

    public static <T extends Real<T>> NeoMatrix3<T> offset(T x, T y) {
        return new NeoMatrix3<T>(x.one(), x.zero(), x, x.zero(), x.one(), y, x.zero(), x.zero(), x.one());
    }

    public static <T extends Real<T>> NeoMatrix3<T> offset(NeoPoint2<T> p) {
        T x = p.x();
        return new NeoMatrix3<>(x.one(), x.zero(), x, x.zero(), x.one(), p.y(), x.zero(), x.zero(), x.one());
    }

    public static <T extends Real<T>> NeoMatrix3<T> rotate(T a) {
        return new NeoMatrix3<>(a.cos(), a.sin().negate(), a.zero(), a.sin(), a.cos(), a.zero(), a.zero(), a.zero(), a.one());
    }
}


