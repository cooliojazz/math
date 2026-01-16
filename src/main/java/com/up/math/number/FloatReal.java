package com.up.math.number;

public record FloatReal(float f) implements Real<FloatReal> {
    
    public FloatReal() {
        this(0);
    }
    
    public FloatReal(double d) {
        this((float)d);
    }
    
    @Override
    public boolean sign() {
        return f >= 0;
    }
    
    @Override
    public FloatReal abs() {
        return new FloatReal(Math.abs(f));
    }
    
    @Override
    public FloatReal add(FloatReal b) {
        return new FloatReal(f + b.f);
    }
    
    @Override
    public FloatReal sub(FloatReal b) {
        return new FloatReal(f - b.f);
    }
    
    @Override
    public FloatReal mult(FloatReal b) {
        return new FloatReal(f * b.f);
    }
    
    @Override
    public FloatReal div(FloatReal b) {
        return new FloatReal(f / b.f);
    }
    
    @Override
    public FloatReal negate() {
        return new FloatReal(-f);
    }
    
    @Override
    public FloatReal inverse() {
        return new FloatReal(1 / f);
    }
    
    @Override
    public FloatReal sqrt() {
        return new FloatReal((float)Math.sqrt(f));
    }
    
    @Override
    public FloatReal square() {
        return new FloatReal(f * f);
    }
    
    @Override
    public FloatReal pow(int p) {
        return new FloatReal((float)Math.pow(f, p));
    }
    
    @Override
    public FloatReal pow(FloatReal p) {
        return new FloatReal((float)Math.pow(f, p.f));
    }
    
    @Override
    public FloatReal sin() {
        return new FloatReal((float)Math.sin(f));
    }
    
    @Override
    public FloatReal sinh() {
        return new FloatReal((float)Math.sinh(f));
    }
    
    @Override
    public FloatReal cos() {
        return new FloatReal((float)Math.cos(f));
    }
    
    @Override
    public FloatReal cosh() {
        return new FloatReal((float)Math.cosh(f));
    }
    
    @Override
    public FloatReal atan() {
        return new FloatReal((float)Math.atan(f));
    }
    
    @Override
    public FloatReal exp() {
        return new FloatReal((float)Math.exp(f));
    }
    
    @Override
    public FloatReal log() {
        return new FloatReal((float)Math.log(f));
    }
    
    @Override
    public FloatReal mod(FloatReal n) {
        return new FloatReal(f % n.f);
    }
    
    @Override
    public FloatReal floor() {
        return new FloatReal((float)Math.floor(f));
    }
    
    @Override
    public FloatReal ceil() {
        return new FloatReal((float)Math.ceil(f));
    }
    
    // TODO: Should replace these with constants
    @Override
    public FloatReal zero() {
        return new FloatReal(0);
    }
    
    @Override
    public FloatReal one() {
        return new FloatReal(1);
    }
    
    @Override
    public FloatReal two() {
        return new FloatReal(2);
    }
    
    @Override
    public FloatReal pi() {
        return new FloatReal((float)Math.PI);
    }
    
    @Override
    public FloatReal e() {
        return new FloatReal((float)Math.E);
    }
    
    @Override
    public FloatReal sqrt2() {
        return new FloatReal((float)Math.sqrt(2));
    }
    
    public static FloatReal fromDouble(double d) {
        return new FloatReal((float)d);
    }
    
    @Override
    public double toDouble() {
        return f;
    }
    
    @Override
    public int compareTo(FloatReal o) {
        return Float.compare(f, o.f);
    }
    
    @Override
    public String toString() {
        return "" + f;
    }
}
