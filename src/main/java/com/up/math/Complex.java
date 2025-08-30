package com.up.math;

public record Complex(double real, double imag) {
    
    public static final Complex ZERO = new Complex(0);
    
    public Complex(double real) {
        this(real, 0);
    }
    
    public static Complex fromPolar(double radius, double theta) {
        return new Complex(radius * Math.cos(theta), radius * Math.sin(theta));
    }
    
    public Complex add(Complex c) {
        return new Complex(real + c.real, imag + c.imag);
    }
    
    public Complex subtract(Complex c) {
        return new Complex(real - c.real, imag - c.imag);
    }
    
    public Complex multiply(double d) {
        return new Complex(real * d, imag * d);
    }
    
    public Complex multiply(Complex c) {
        return new Complex(real * c.real - imag * c.imag, real * c.imag + imag * c.real);
    }
    
    public Complex conjugate() {
        return new Complex(real, -imag);
    }
    
    public double magnitude() {
        return Math.sqrt(real * real + imag * imag);
    }
    
    public Complex negate() {
        return new Complex(-real, -imag);
    }
    
    public Complex inverse() {
        double dSq = real * real + imag * imag;
        return new Complex(real / dSq, -imag / dSq);
    }
    
    public Complex sqrt() {
        double dist = magnitude();
        return new Complex(Math.sqrt(dist + real), sign(imag) * Math.sqrt(dist - real)).multiply(1 / Math.sqrt(2));
    }
    
    public Complex exp() {
        return new Complex(Math.exp(real) * Math.cos(imag), Math.exp(real) * Math.sin(imag));
    }
    
    public Complex log() {
        return new Complex(Math.log(magnitude()), Math.atan2(imag, real));
    }
    
    /**
     * Replaces Math.signum which returns 0 for 0, breaking the complex square root
     * @param d
     * @return
     */
    private static double sign(double d) {
        return d < 0 ? -1 : 1;
    }
    
    public double azimuth() {
        return Math.atan2(imag, real);
    }
    
    /**
     * Returns the principal root for this raised to the real d
     * @param d
     * @return
     */
    public Complex pow(double d) {
        return Complex.fromPolar(Math.pow(magnitude(), d), azimuth() * d);
    }
    
    /**
     * Returns the principal root for this raised to the complex c
     * @param c
     * @return
     */
    public Complex pow(Complex c) {
        Complex theta = c.multiply(azimuth());
        return Complex.pow(magnitude(), c).multiply(theta.cos().add(theta.sin().multiply(new Complex(0, 1))));
    }
    
    public static Complex pow(double d, Complex c) {
        return Complex.fromPolar(1, c.imag * Math.log(d)).multiply(Math.pow(d, c.real));
    }
    
    public Complex cos() {
        return new Complex(Math.cos(real) * Math.cosh(imag), -Math.sin(real) * Math.sinh(imag));
    }
    
    public Complex sin() {
        return new Complex(Math.sin(real) * Math.cosh(imag), Math.cos(real) * Math.sinh(imag));
    }
    
    public Point2 asPoint() {
        return new Point2(real, imag);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex c) return real == c.real && imag == c.imag;
        return false;
    }
    
    @Override
    public String toString() {
        return real + (imag < 0 ? "" : "+") + imag + "i";
    }
    
}
