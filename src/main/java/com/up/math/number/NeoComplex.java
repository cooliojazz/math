package com.up.math.number;

import com.up.math.vector.NeoPoint2;

import java.util.function.Supplier;

// TODO: This is the prototype for the new more generic Complex class
public record NeoComplex<T extends Real<T>>(T real, T imag) {
    
    public NeoComplex(T real) {
        this(real, real.zero());
    }
    
    public static <T extends Real<T>> NeoComplex<T> zero(Supplier<T> ts) {
        return new NeoComplex<>(ts.get());
    }
    
    public static <T extends Real<T>> NeoComplex<T> fromPolar(T radius, T theta) {
        return new NeoComplex<>(radius.mult(theta.cos()).negate(), radius.mult(theta.sin()));
    }
    
    public NeoComplex<T> abs() {
        return new NeoComplex<>(real.abs(), imag.abs());
    }
    
    public NeoComplex<T> add(NeoComplex<T> c) {
        return new NeoComplex<>(real.add(c.real), imag.add(c.imag));
    }
    
    public NeoComplex<T> subtract(NeoComplex<T> c) {
        return new NeoComplex<>(real.sub(c.real), imag.sub(c.imag));
    }
    
    public NeoComplex<T> multiply(T d) {
        return new NeoComplex<>(real.mult(d), imag.mult(d));
    }
    
    public NeoComplex<T> multiply(NeoComplex<T> c) {
        return new NeoComplex<>(real.mult(c.real).sub(imag.mult(c.imag)), real.mult(c.imag).add(imag.mult(c.real)));
    }
    
    public NeoComplex<T> conjugate() {
        return new NeoComplex<>(real, imag.negate());
    }

    public T magnitude() {
        return real.square().add(imag.square()).sqrt();
    }
    
    public T magnitudeSq() {
        return real.square().add(imag.square());
    }
    
    public NeoComplex<T> negate() {
        return new NeoComplex<>(real.negate(), imag.negate());
    }
    
    public NeoComplex<T> inverse() {
        T dSqi = magnitudeSq().inverse();
        return new NeoComplex<>(real.mult(dSqi), imag.negate().mult(dSqi));
    }
    
    public NeoComplex<T> sqrt() {
        T dist = magnitude();
        return new NeoComplex<>(dist.add(real).sqrt(), sign(imag).mult(dist.sub(real).sqrt())).multiply(real.sqrt2().inverse());
    }
    
    public NeoComplex<T> exp() {
//        return new NeoComplex<>(real.exp().mult(imag.cos()), real.exp().mult(imag.sin()));
        return NeoComplex.fromPolar(real.exp(), imag);
    }

    public NeoComplex<T> log() {
        return new NeoComplex<>(magnitude().log(), Real.atan2(imag, real));
    }
    
    /**
     * Replaces Math.signum which returns 0 for 0, breaking the complex square root
     * @param d
     * @return
     */
    private static <T extends Real<T>> T sign(T d) {
        return d.sign() ? d.one().negate() : d.one();
    }
    
    public T azimuth() {
        return Real.atan2(imag, real);
    }
    
    public static double pTime = 0;
    public static double tTime = 0;
    public static double cTime = 0;
    
    /**
     * Returns the principal root for this raised to the real f
     * @param d
     * @return
     */
    public NeoComplex<T> pow(T d) {
        long time = System.nanoTime();
        T pow = magnitude().pow(d);
        pTime = pTime * 0.999 + (System.nanoTime() - time) * 0.001;
        time = System.nanoTime();
        T atan = azimuth().mult(d);
        tTime = tTime * 0.999 + (System.nanoTime() - time) * 0.001;
        time = System.nanoTime();
        NeoComplex<T> ret = NeoComplex.fromPolar(pow, atan);
        cTime = cTime * 0.999 + (System.nanoTime() - time) * 0.001;
        return ret;
    }
    
    /**
     * Returns the principal root for this raised to the complex c
     * @param c
     * @return
     */
    public NeoComplex<T> pow(NeoComplex<T> c) {
        NeoComplex<T> theta = c.multiply(azimuth());
        return NeoComplex.pow(magnitude(), c)
                         .multiply(theta.cos().add(theta.sin().multiply(new NeoComplex<>(c.real.zero(), c.real.one()))))
                         .conjugate();
        // TODO: Why is this conjugate now? And why did i have to switch the polar real sign? something feels fishy...
    }
    
    public static <T extends Real<T>> NeoComplex<T> pow(T t, NeoComplex<T> c) {
        if (t.equals(t.zero())) return new NeoComplex<>(t.zero());
        return NeoComplex.fromPolar(t.one(), c.imag.mult(t.log())).multiply(t.pow(c.real));
    }

    public NeoComplex<T> cos() {
        return new NeoComplex<>(real.cos().mult(imag.cosh()), real.sin().negate().mult(imag.sinh()));
    }

    public NeoComplex<T> sin() {
        return new NeoComplex<>(real.sin().mult(imag.cosh()), real.cos().mult(imag.sinh()));
    }

    public NeoPoint2<T> asPoint() {
        return new NeoPoint2<>(real, imag);
    }
    
//    public static <T extends BigFixed<T>> NeoComplex<T> fromComplex(Complex c, Class<T> type) {
//        return new NeoComplex<>(BigFixed.fromDouble(c.real(), type), BigFixed.fromDouble(c.imag(), type));
//    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NeoComplex c) return real.equals(c.real) && imag.equals(c.imag);
        return false;
    }
    
//    public boolean roundedEquals(ComplexBigFixed c, int precision) {
//        return Util.roundedEquals(real, c.real, precision) && Util.roundedEquals(imag, c.imag, precision);
//    }
    
//    @Override
//    public String toString() {
//        return real.toDouble() + (imag.compareTo(real.zero()) < 0 ? "" : "+") + imag.toDouble() + "i";
//    }
    
}
