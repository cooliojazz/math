package com.up.math.number;

import com.up.math.vector.BigFixedPoint2;

import java.util.function.Supplier;

public record ComplexBigFixed<T extends BigFixed<T>>(T real, T imag) {
    
    public ComplexBigFixed(T real) {
        this(real, real.zero());
    }
    
    public static <T extends BigFixed<T>> ComplexBigFixed<T> zero(Supplier<T> ts) {
        return new ComplexBigFixed<>(ts.get());
    }
    
    public static ComplexBigFixed<IntFixed> fromPolar(IntFixed radius, IntFixed theta) {
        return new ComplexBigFixed<>(radius.mult(theta.cos()), radius.mult(theta.sin()));
    }
    
    public ComplexBigFixed<T> add(ComplexBigFixed<T> c) {
        return new ComplexBigFixed<>(real.add(c.real), imag.add(c.imag));
    }
    
    public ComplexBigFixed<T> subtract(ComplexBigFixed<T> c) {
        return new ComplexBigFixed<>(real.sub(c.real), imag.sub(c.imag));
    }
    
    public ComplexBigFixed<T> multiply(T d) {
        return new ComplexBigFixed<>(real.mult(d), imag.mult(d));
    }
    
    public ComplexBigFixed<T> multiply(ComplexBigFixed<T> c) {
        return new ComplexBigFixed<>(real.mult(c.real).sub(imag.mult(c.imag)), real.mult(c.imag).add(imag.mult(c.real)));
    }
    
    public ComplexBigFixed<T> conjugate() {
        return new ComplexBigFixed<>(real, imag.negate());
    }

    public T magnitude() {
        return real.mult(real).add(imag.mult(imag)).sqrt();
    }
    
    public T magnitudeSq() {
        return real.mult(real).add(imag.mult(imag));
    }
    
    public ComplexBigFixed<T> negate() {
        return new ComplexBigFixed<>(real.negate(), imag.negate());
    }
    
    public ComplexBigFixed<T> inverse() {
        T dSq = real.mult(real).add(imag.mult(imag));
        return new ComplexBigFixed<>(real.mult(dSq.inverse()), imag.negate().mult(dSq.inverse()));
    }
    
    public ComplexBigFixed<T> sqrt() {
        T dist = magnitude();
        return new ComplexBigFixed<>(dist.add(real).sqrt(), sign(imag).mult(dist.sub(real).sqrt())).multiply(BigFixed.fromDouble(Math.sqrt(2), (Class<T>)real.getClass()).inverse());
    }
    
//    public ComplexBigFixed<T> exp() {
//        return new ComplexBigFixed<>(real.exp().mult(imag.cos()), real.exp().mult(imag.sin()));
//    }
//
//    public ComplexBigFixed<T> log() {
//        return new ComplexBigFixed<>(magnitude().log(), T.atan2(imag, real));
//    }
    
    /**
     * Replaces Math.signum which returns 0 for 0, breaking the complex square root
     * @param d
     * @return
     */
    private static <T extends BigFixed<T>> T sign(T d) {
        return d.sign() ? d.one().negate() : d.one();
    }
    
    public T azimuth() {
        return T.atan2(imag, real);
    }
    
//    /**
//     * Returns the principal root for this raised to the real d
//     * @param d
//     * @return
//     */
//    public ComplexBigFixed<T> pow(T d) {
//        return ComplexBigFixed.fromPolar(magnitude().pow(d), azimuth().mult(d));
//    }
    
//    /**
//     * Returns the principal root for this raised to the complex c
//     * @param c
//     * @return
//     */
//    public ComplexBigFixed pow(ComplexBigFixed c) {
//        ComplexBigFixed theta = c.multiply(azimuth());
//        return ComplexBigFixed.pow(magnitude(), c).multiply(theta.cos().add(theta.sin().multiply(new ComplexBigFixed(0, 1))));
//    }
    
//    public static ComplexBigFixed pow(double d, ComplexBigFixed c) {
//        if (d == 0) return new ComplexBigFixed(0);
//        return ComplexBigFixed.fromPolar(1, c.imag * Math.log(d)).multiply(Math.pow(d, c.real));
//    }

    public ComplexBigFixed<T> cos() {
        return new ComplexBigFixed<>(real.cos().mult(imag.cosh()), real.sin().negate().mult(imag.sinh()));
    }

    public ComplexBigFixed<T> sin() {
        return new ComplexBigFixed<>(real.sin().mult(imag.cosh()), real.cos().mult(imag.sinh()));
    }

    public BigFixedPoint2<T> asPoint() {
        return new BigFixedPoint2<>(real, imag);
    }
    
    public static <T extends BigFixed<T>> ComplexBigFixed<T> fromComplex(Complex c, Class<T> type) {
        return new ComplexBigFixed<T>(BigFixed.fromDouble(c.real(), type), BigFixed.fromDouble(c.imag(), type));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexBigFixed c) return real.equals(c.real) && imag.equals(c.imag);
        return false;
    }
    
//    public boolean roundedEquals(ComplexBigFixed c, int precision) {
//        return Util.roundedEquals(real, c.real, precision) && Util.roundedEquals(imag, c.imag, precision);
//    }
    
    @Override
    public String toString() {
        return real.toDouble() + (imag.compareTo(real.zero()) < 0 ? "" : "+") + imag.toDouble() + "i";
    }
    
}
