package com.up.math.matrix;

import com.up.math.number.BigFixed;
import com.up.math.number.Complex;
import com.up.math.vector.BigFixedPoint2;
import com.up.math.vector.Point2;
import com.up.math.vector.Point3;

import java.awt.geom.Point2D;

public record BigFixedMatrix3(BigFixed a, BigFixed b, BigFixed c,
                              BigFixed d, BigFixed e, BigFixed f,
                              BigFixed g, BigFixed h, BigFixed i) {

    public BigFixed determinant() {
        return a.mult(e).mult(i).add(b.mult(f).mult(g)).add(c.mult(d).mult(h))
                .sub(c.mult(e).mult(g).add(b.mult(d).mult(i)).add(a.mult(f).mult(h)));
    }

    public BigFixedMatrix3 transpose() {
        return new BigFixedMatrix3(a, d, g, b, e, h, c, f, i);
    }
    public BigFixedMatrix3 inverse() {
        if (determinant().equals(new BigFixed())) return null;
        BigFixedMatrix3 cof = new BigFixedMatrix3(new BigFixedMatrix2(e, f, h, i).determinant(), new BigFixedMatrix2(d, f, g, i).determinant().negate(), new BigFixedMatrix2(d, e, g, h).determinant(),
                                                new BigFixedMatrix2(b, c, h, i).determinant().negate(), new BigFixedMatrix2(a, c, g, i).determinant(), new BigFixedMatrix2(a, b, g, h).determinant().negate(),
                                                new BigFixedMatrix2(b, c, e, f).determinant(), new BigFixedMatrix2(a, c, d, f).determinant().negate(), new BigFixedMatrix2(a, b, d, e).determinant());
        return cof.transpose().multiply(determinant().inverse());
    }

    public BigFixedMatrix3 multiply(BigFixedMatrix3 m) {
        return new BigFixedMatrix3(a.mult(m.a), b.mult(m.b), c.mult(m.c), d.mult(m.d), e.mult(m.e), f.mult(m.f), g.mult(m.g), h.mult(m.h), i.mult(m.i));
    }

    public BigFixedMatrix3 multiply(BigFixed s) {
        return new BigFixedMatrix3(a.mult(s), b.mult(s), c.mult(s), d.mult(s), e.mult(s), f.mult(s), g.mult(s), h.mult(s), i.mult(s));
    }

    public BigFixedMatrix3 compose(BigFixedMatrix3 m) {
        return new BigFixedMatrix3(m.a.mult(a).add(m.d.mult(b)).add(m.g.mult(c)), m.b.mult(a).add(m.e.mult(b)).add(m.h.mult(c)), m.c.mult(a).add(m.f.mult(b)).add(m.i.mult(c)),
                           m.a.mult(d).add(m.d.mult(e)).add(m.g.mult(f)), m.b.mult(d).add(m.e.mult(e)).add(m.h.mult(f)), m.c.mult(d).add(m.f.mult(e)).add(m.i.mult(f)),
                           m.a.mult(g).add(m.d.mult(h)).add(m.g.mult(i)), m.b.mult(g).add(m.e.mult(h)).add(m.h.mult(i)), m.c.mult(g).add(m.f.mult(h)).add(m.i.mult(i)));
    }
//
//    public Point3 apply(Point3 p) {
//        return new Point3(p.x().mult(a).add(p.y().mult(b)).add(p.z().mult(c),) p.x().mult(d).add(p.y().mult(e)).add(p.z().mult(f),) p.x().mult(g).add(p.y().mult(h)).add(p.z().mult(i));)
//    }
//
    public BigFixedPoint2 apply(BigFixedPoint2 p) {
        return new BigFixedPoint2(p.getX().mult(a).add(p.getY().mult(b)).add(c), p.getX().mult(d).add(p.getY().mult(e)).add(f));
    }

    public BigFixedMatrix2 linearMap() {
        return new BigFixedMatrix2(a, b, d, e);
    }

//    public AffineMatrix2 asAffine() {
//        return new AffineMatrix2(a, b, d, e, c, f);
//    }
    
    public static BigFixedMatrix3 fromMatrix3(Matrix3 m) {
        return new BigFixedMatrix3(BigFixed.fromDouble(m.a()), BigFixed.fromDouble(m.b()), BigFixed.fromDouble(m.c()),
                                   BigFixed.fromDouble(m.d()), BigFixed.fromDouble(m.e()), BigFixed.fromDouble(m.f()),
                                   BigFixed.fromDouble(m.g()), BigFixed.fromDouble(m.h()), BigFixed.fromDouble(m.i()));
    }

    public static BigFixedMatrix3 identity() {
        return new BigFixedMatrix3(BigFixed.fromInt(1), new BigFixed(), new BigFixed(), new BigFixed(), BigFixed.fromInt(1), new BigFixed(), new BigFixed(), new BigFixed(), BigFixed.fromInt(1));
    }

    public static BigFixedMatrix3 scale(BigFixed s) {
        return new BigFixedMatrix3(s, new BigFixed(), new BigFixed(), new BigFixed(), s, new BigFixed(), new BigFixed(), new BigFixed(), BigFixed.fromInt(1));
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
    public static BigFixedMatrix3 offset(BigFixedPoint2 p) {
        return new BigFixedMatrix3(BigFixed.fromInt(1), new BigFixed(), p.getX(), new BigFixed(), BigFixed.fromInt(1), p.getY(), new BigFixed(), new BigFixed(), BigFixed.fromInt(1));
    }
//
//    public static BigFixedMatrix3 rotate(double a) {
//        return new BigFixedMatrix3(Math.cos(a), -Math.sin(a), 0, Math.sin(a), Math.cos(a), 0, 0, 0, 1);
//    }
}


