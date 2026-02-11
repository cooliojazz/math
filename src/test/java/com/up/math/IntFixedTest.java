package com.up.math;

import com.up.math.number.IntFixed;
import com.up.math.number.Precision;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;

public class IntFixedTest {
    
    private static Random r = new Random();
    
    public static void main(String[] args) {
        int iter = 100000;
        String[] tests = new String[] {"toDouble", "add", "mult", "div", "exp2", "exp", "sqrt", "pow", "sin", "cos"};
        for (String test : tests) {
            int fails = 0;
            for (int i = 0; i < iter; i++) {
                try {
//                    test.accept(Precision.P1_2, r);
                    IntFixedTest.class.getMethod(test, Precision.class, Random.class).invoke(null, Precision.P2_2, r);
                } catch (NoSuchMethodException | IllegalAccessException  e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getCause().getMessage());
                    fails++;
                }
            }
            System.out.println((iter - fails) + "/" + iter + " passed for " + test);
        }
    }
    
    public static <P extends Precision> void toDouble(P p, Random r) {
        double d = r.nextDouble(maxValue(p));
        IntFixed<P> a = IntFixed.fromDouble(p, d);
        assert a.toDouble() == d : "Error converting too/from double for " + d;
    }
    
    public static <P extends Precision> void add(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r);
        IntFixed<P> b = IntFixed.random(p, r);
        
        IntFixed<P> ans = a.add(b);
        double ansD = inBounds(p, a.toDouble() + b.toDouble());
        assert approxEqual(ans.toDouble(), ansD) : "Error adding numbers: " + ans.toDouble() + " different from " + ansD + " for " + a + " and " + b;
    }
    
    public static <P extends Precision> void mult(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r);
        IntFixed<P> b = IntFixed.random(p, r);
        
        IntFixed<P> ans = a.mult(b);
        double ansD = inBounds(p, a.toDouble() * b.toDouble());
        assert approxEqual(ans.toDouble(), ansD) : "Error multiplying numbers: " + ans.toDouble() + " different from " + ansD + " for " + a + " and " + b;
    }
    
    public static <P extends Precision> void div(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r);
        IntFixed<P> b = IntFixed.random(p, r);
        
        IntFixed<P> ans = a.div(b);
        double ansD = inBounds(p, a.toDouble() / b.toDouble());
        assert approxEqual(ans.toDouble(), ansD) : "Error dividing numbers: " + ans.toDouble() + " different from " + ansD + " for " + a + " and " + b;
    }
    
    public static <P extends Precision> void exp2(P p, Random r) {
        IntFixed<P> a = IntFixed.fromDouble(p, r.nextDouble(-p.getFractionalSize() * 32 + 16, p.getIntegralSize() * 32 - 1));
        
        IntFixed<P> ans = a.exp2();
        double ansD = inBounds(p, Math.exp(a.toDouble() * Math.log(2)));
        assert approxEqual(ans.toDouble(), ansD) : "Error exponentiating 2 number: " + ans.toDouble() + " different from " + ansD + " for " + a;
    }
    
    public static <P extends Precision> void exp(P p, Random r) {
        IntFixed<P> a = IntFixed.fromDouble(p, r.nextDouble(-(p.getFractionalSize() * 32 + 16) / 1.44269504, (p.getIntegralSize() * 32 - 1) / 1.44269504));
        
        IntFixed<P> ans = a.exp();
        double ansD = inBounds(p, Math.exp(a.toDouble()));
        assert approxEqual(ans.toDouble(), ansD) : "Error exponentiating number: " + ans.toDouble() + " different from " + ansD + " for " + a;
    }
    
    public static <P extends Precision> void sqrt(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r);
        
        IntFixed<P> ans = a.sqrt();
        double ansD = inBounds(p, Math.sqrt(a.toDouble()));
        assert approxEqual(ans.toDouble(), ansD) : "Error in sqrt number: " + ans.toDouble() + " different from " + ansD + " for " + a;
    }
    
    public static <P extends Precision> void pow(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r);
        IntFixed<P> b = IntFixed.random(p, r);
        
        IntFixed<P> ans = a.pow(b);
        double ansD = inBounds(p, Math.pow(a.toDouble(), b.toDouble()));
        assert approxEqual(ans.toDouble(), ansD) : "Error in pow number: " + ans.toDouble() + " different from " + ansD + " for " + a + " and " + b;
    }
    
    public static <P extends Precision> void sin(P p, Random r) {
        // Need to keep within the doubles integer range or it computes sin wrong, invalidating the test
        IntFixed<P> a = IntFixed.random(p, r).mod(IntFixed.fromDouble(p, 1e15));
        
        IntFixed<P> ans = a.sin();
        double ansD = inBounds(p, Math.sin(a.toDouble()));
        assert approxEqual(ans.toDouble(), ansD) : "Error in sin number: " + ans.toDouble() + " different from " + ansD + " for " + a;
    }
    
    public static <P extends Precision> void cos(P p, Random r) {
        IntFixed<P> a = IntFixed.random(p, r).mod(IntFixed.fromDouble(p, 1e15));
        
        IntFixed<P> ans = a.cos();
        double ansD = inBounds(p, Math.cos(a.toDouble()));
        assert approxEqual(ans.toDouble(), ansD) : "Error in sin number: " + ans.toDouble() + " different from " + ansD + " for " + a;
    }
    
    public static double inBounds(Precision p, double d) {
        if (Math.abs(d) < Math.pow(2, -p.getFractionalSize() * 32 - 1)) return 0;
        return d % maxValue(p);
    }
    
    public static double maxValue(Precision p) {
        return Math.pow(2, p.getIntegralSize() * 32);
    }
    
    public static boolean approxEqual(double a, double b) {
        if (a == b) return true;
        if (Double.isNaN(a) && Double.isNaN(b)) return true;
        return Math.abs(a - b) / Math.max(a, b) < 1e-12;
    }
    
}
