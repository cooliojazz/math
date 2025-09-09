package com.up.math.number;

import com.up.math.Util;
import com.up.math.matrix.BigFixedMatrix3;
import com.up.math.vector.BigFixedPoint2;
import com.up.math.vector.Point2;

public record ComplexBigFixed(BigFixed real, BigFixed imag) {
    
    public static final ComplexBigFixed ZERO = new ComplexBigFixed(BigFixed.fromInt(0));
    
    public ComplexBigFixed(BigFixed real) {
        this(real, BigFixed.fromInt(0));
    }
    
    public static ComplexBigFixed fromPolar(BigFixed radius, BigFixed theta) {
        return new ComplexBigFixed(radius.mult(theta.cos()), radius.mult(theta.sin()));
    }
    
    public ComplexBigFixed add(ComplexBigFixed c) {
        return new ComplexBigFixed(real.add(c.real), imag.add(c.imag));
    }
    
    public ComplexBigFixed subtract(ComplexBigFixed c) {
        return new ComplexBigFixed(real.sub(c.real), imag.sub(c.imag));
    }
    
    public ComplexBigFixed multiply(BigFixed d) {
        return new ComplexBigFixed(real.mult(d), imag.mult(d));
    }
    
    public ComplexBigFixed multiply(ComplexBigFixed c) {
        return new ComplexBigFixed(real.mult(c.real).sub(imag.mult(c.imag)), real.mult(c.imag).add(imag.mult(c.real)));
    }
    
    public ComplexBigFixed conjugate() {
        return new ComplexBigFixed(real, imag.negate());
    }

    public BigFixed magnitude() {
        return real.mult(real).add(imag.mult(imag)).sqrt();
    }
    
    public BigFixed magnitudeSq() {
        return real.mult(real).add(imag.mult(imag));
    }
    
    public ComplexBigFixed negate() {
        return new ComplexBigFixed(real.negate(), imag.negate());
    }
    
    public ComplexBigFixed inverse() {
        BigFixed dSq = real.mult(real).add(imag.mult(imag));
        return new ComplexBigFixed(real.mult(dSq.inverse()), imag.negate().mult(dSq.inverse()));
    }
    
    public ComplexBigFixed sqrt() {
        BigFixed dist = magnitude();
        return new ComplexBigFixed(dist.add(real).sqrt(), sign(imag).mult(dist.sub(real).sqrt())).multiply(BigFixed.fromDouble(Math.sqrt(2)).inverse());
    }
    
//    public ComplexBigFixed exp() {
//        return new ComplexBigFixed(Math.exp(real) * Math.cos(imag), Math.exp(real) * Math.sin(imag));
//    }
//    
//    public ComplexBigFixed log() {
//        return new ComplexBigFixed(Math.log(magnitude()), Math.atan2(imag, real));
//    }
    
    /**
     * Replaces Math.signum which returns 0 for 0, breaking the complex square root
     * @param d
     * @return
     */
    private static BigFixed sign(BigFixed d) {
        return d.sign ? BigFixed.fromInt(-1) : BigFixed.fromInt(1);
    }
    
    public BigFixed azimuth() {
        return BigFixed.atan2(imag, real);
    }
    
//    /**
//     * Returns the principal root for this raised to the real d
//     * @param d
//     * @return
//     */
//    public ComplexBigFixed pow(BigFixed d) {
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

    public ComplexBigFixed cos() {
        return new ComplexBigFixed(real.cos().mult(imag.cosh()), real.sin().negate().mult(imag.sinh()));
    }

    public ComplexBigFixed sin() {
        return new ComplexBigFixed(real.sin().mult(imag.cosh()), real.cos().mult(imag.sinh()));
    }

    public BigFixedPoint2 asPoint() {
        return new BigFixedPoint2(real, imag);
    }
    
    public static ComplexBigFixed fromComplex(Complex c) {
        return new ComplexBigFixed(BigFixed.fromDouble(c.real()), BigFixed.fromDouble(c.imag()));
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
        return real.toDouble() + (imag.compareTo(BigFixed.fromInt(0)) < 0 ? "" : "+") + imag.toDouble() + "i";
    }
    
}
