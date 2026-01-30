package com.up.math.number;

import java.util.function.Function;

public record DoubleReal(double d) implements Real<DoubleReal> {
    
    public DoubleReal() {
        this(0);
    }
    
    @Override
    public boolean sign() {
        return d >= 0;
    }
    
    @Override
    public DoubleReal abs() {
        return new DoubleReal(Math.abs(d));
    }
    
    @Override
    public DoubleReal add(DoubleReal b) {
        return new DoubleReal(d + b.d);
    }
    
    @Override
    public DoubleReal sub(DoubleReal b) {
        return new DoubleReal(d - b.d);
    }
    
    @Override
    public DoubleReal mult(DoubleReal b) {
        return new DoubleReal(d * b.d);
    }
    
    @Override
    public DoubleReal div(DoubleReal b) {
        return new DoubleReal(d / b.d);
    }
    
    @Override
    public DoubleReal negate() {
        return new DoubleReal(-d);
    }
    
    @Override
    public DoubleReal inverse() {
        return new DoubleReal(1 / d);
    }
    
    @Override
    public DoubleReal sqrt() {
        return new DoubleReal(Math.sqrt(d));
    }
    
    @Override
    public DoubleReal square() {
        return new DoubleReal(d * d);
    }
    
    @Override
    public DoubleReal pow(int p) {
        return new DoubleReal(Math.pow(d, p));
    }
    
    @Override
    public DoubleReal pow(DoubleReal p) {
        return new DoubleReal(Math.pow(d, p.d));
    }
    
    @Override
    public DoubleReal sin() {
        return new DoubleReal(Math.sin(d));
    }
    
    @Override
    public DoubleReal sinh() {
        return new DoubleReal(Math.sinh(d));
    }
    
    @Override
    public DoubleReal cos() {
        return new DoubleReal(Math.cos(d));
    }
    
    @Override
    public DoubleReal cosh() {
        return new DoubleReal(Math.cosh(d));
    }
    
    @Override
    public DoubleReal atan() {
        return new DoubleReal(Math.atan(d));
    }
    
    @Override
    public DoubleReal exp() {
        return new DoubleReal(Math.exp(d));
    }
    
    @Override
    public DoubleReal log() {
        return new DoubleReal(Math.log(d));
    }
    
    @Override
    public DoubleReal mod(DoubleReal n) {
        return new DoubleReal(d % n.d);
    }
    
    @Override
    public DoubleReal floor() {
        return new DoubleReal(Math.floor(d));
    }
    
    @Override
    public DoubleReal ceil() {
        return new DoubleReal(Math.ceil(d));
    }
    
    // TODO: Should replace these with constants
    @Override
    public DoubleReal zero() {
        return new DoubleReal(0);
    }
    
    @Override
    public DoubleReal one() {
        return new DoubleReal(1);
    }
    
    @Override
    public DoubleReal two() {
        return new DoubleReal(2);
    }
    
    @Override
    public DoubleReal pi() {
        return new DoubleReal(Math.PI);
    }
    
    @Override
    public DoubleReal e() {
        return new DoubleReal(Math.E);
    }
    
    @Override
    public DoubleReal sqrt2() {
        return new DoubleReal(Math.sqrt(2));
    }
    
    public static DoubleReal fromDouble(double d) {
        return new DoubleReal(d);
    }
    
    @Override
    public double toDouble() {
        return d;
    }
    
    @Override
    public int compareTo(DoubleReal o) {
        return Double.compare(d, o.d);
    }
    
    @Override
    public String toString() {
        return "" + d;
    }
    
    @Override
    public Function<Double, DoubleReal> getConverter() {
        return DoubleReal::fromDouble;
    }
}
