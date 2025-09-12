package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.vector.BigFixedPoint2;

import java.util.function.Supplier;

public record BigFixedMatrix3<T extends BigFixed<T>>(T a, T b, T c,
                                                  T d, T e, T f,
                                                  T g, T h, T i) {

    public T determinant() {
        return a.mult(e).mult(i).add(b.mult(f).mult(g)).add(c.mult(d).mult(h))
                .sub(c.mult(e).mult(g).add(b.mult(d).mult(i)).add(a.mult(f).mult(h)));
    }

    public BigFixedMatrix3<T> transpose() {
        return new BigFixedMatrix3<T>(a, d, g, b, e, h, c, f, i);
    }
    public BigFixedMatrix3<T> inverse() {
        if (determinant().equals(a.zero())) return null;
        BigFixedMatrix3<T> cof = new BigFixedMatrix3<>(new BigFixedMatrix2<>(e, f, h, i).determinant(), new BigFixedMatrix2<>(d, f, g, i).determinant().negate(), new BigFixedMatrix2<>(d, e, g, h).determinant(),
                                                new BigFixedMatrix2<>(b, c, h, i).determinant().negate(), new BigFixedMatrix2<>(a, c, g, i).determinant(), new BigFixedMatrix2<>(a, b, g, h).determinant().negate(),
                                                new BigFixedMatrix2<>(b, c, e, f).determinant(), new BigFixedMatrix2<>(a, c, d, f).determinant().negate(), new BigFixedMatrix2<>(a, b, d, e).determinant());
        return cof.transpose().multiply(determinant().inverse());
    }

    public BigFixedMatrix3<T> multiply(BigFixedMatrix3<T> m) {
        return new BigFixedMatrix3<>(a.mult(m.a), b.mult(m.b), c.mult(m.c), d.mult(m.d), e.mult(m.e), f.mult(m.f), g.mult(m.g), h.mult(m.h), i.mult(m.i));
    }

    public BigFixedMatrix3<T> multiply(T s) {
        return new BigFixedMatrix3<>(a.mult(s), b.mult(s), c.mult(s), d.mult(s), e.mult(s), f.mult(s), g.mult(s), h.mult(s), i.mult(s));
    }

    public BigFixedMatrix3<T> compose(BigFixedMatrix3<T> m) {
        return new BigFixedMatrix3<>(m.a.mult(a).add(m.d.mult(b)).add(m.g.mult(c)), m.b.mult(a).add(m.e.mult(b)).add(m.h.mult(c)), m.c.mult(a).add(m.f.mult(b)).add(m.i.mult(c)),
                           m.a.mult(d).add(m.d.mult(e)).add(m.g.mult(f)), m.b.mult(d).add(m.e.mult(e)).add(m.h.mult(f)), m.c.mult(d).add(m.f.mult(e)).add(m.i.mult(f)),
                           m.a.mult(g).add(m.d.mult(h)).add(m.g.mult(i)), m.b.mult(g).add(m.e.mult(h)).add(m.h.mult(i)), m.c.mult(g).add(m.f.mult(h)).add(m.i.mult(i)));
    }
//
//    public Point3 apply(Point3 p) {
//        return new Point3(p.x().mult(a).add(p.y().mult(b)).add(p.z().mult(c),) p.x().mult(d).add(p.y().mult(e)).add(p.z().mult(f),) p.x().mult(g).add(p.y().mult(h)).add(p.z().mult(i));)
//    }
//
    public BigFixedPoint2<T> apply(BigFixedPoint2<T> p) {
        return new BigFixedPoint2<>(p.getX().mult(a).add(p.getY().mult(b)).add(c), p.getX().mult(d).add(p.getY().mult(e)).add(f));
    }

    public BigFixedMatrix2<T> linearMap() {
        return new BigFixedMatrix2<>(a, b, d, e);
    }

//    public AffineMatrix2 asAffine() {
//        return new AffineMatrix2(a, b, d, e, c, f);
//    }
    
    public static <T extends BigFixed<T>> BigFixedMatrix3<T> fromMatrix3(Matrix3 m, Class<T> type) {
        return new BigFixedMatrix3<>(BigFixed.fromDouble(m.a(), type), BigFixed.fromDouble(m.b(), type), BigFixed.fromDouble(m.c(), type),
                                     BigFixed.fromDouble(m.d(), type), BigFixed.fromDouble(m.e(), type), BigFixed.fromDouble(m.f(), type),
                                     BigFixed.fromDouble(m.g(), type), BigFixed.fromDouble(m.h(), type), BigFixed.fromDouble(m.i(), type));
    }

    public static <T extends BigFixed<T>> BigFixedMatrix3<T> identity(Supplier<T> ts) {
        T z = ts.get();
        return new BigFixedMatrix3<>(z.one(), z, z, z, z.one(), z, z, z, z.one());
    }

    public static <T extends BigFixed<T>> BigFixedMatrix3<T> scale(T s) {
        return new BigFixedMatrix3<>(s, s.zero(), s.zero(), s.zero(), s, s.zero(), s.zero(), s.zero(), s.one());
    }
    
//    public static BigFixedMatrix3 scale(double xs, double ys) {
//        return new BigFixedMatrix3(xs, 0, 0, 0, ys, 0, 0, 0, 1);
//    }
//
//    public static BigFixedMatrix3 scale(Point2 s) {
//        return new BigFixedMatrix3(s.x, 0, 0, 0, s.y, 0, 0, 0, 1);
//    }
//
//    public static BigFixedMatrix3 offset(double x, double y) {
//        return new BigFixedMatrix3(1, 0, x, 0, 1, y, 0, 0, 1);
//    }
//
    public static <T extends BigFixed<T>> BigFixedMatrix3<T> offset(BigFixedPoint2<T> p) {
        T x = p.getX();
        return new BigFixedMatrix3<>(x.one(), x.zero(), x, x.zero(), x.one(), p.getY(), x.zero(), x.zero(), x.one());
    }
//
//    public static BigFixedMatrix3 rotate(double a) {
//        return new BigFixedMatrix3(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
//    }
}


