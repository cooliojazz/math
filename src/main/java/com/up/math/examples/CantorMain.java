package com.up.math.examples;

import com.up.math.number.DoubleReal;
import com.up.math.number.NeoComplex;
import com.up.math.number.Real;
import com.up.math.vector.NeoPoint2;
import com.up.math.vector.Point2Double;

import java.awt.*;
import java.awt.event.*;

public class CantorMain {
    
    public static void main(String[] args) {
        Frame f = new Frame("Cantor Set");
        f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        f.setSize(1000, 800);
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.3246703815274639, -0.32267018065624486), new Complex(2, 0), 100)));
        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(DoubleReal.fromDouble(0.15), new Point2Double(0.2, 0.5), new NeoComplex<>(new DoubleReal(2), new DoubleReal())), new DoubleReal(), CantorMain::drawFractal));
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.08350828339576327, -0.0015708583190908029), new Complex(4, 2), 100)));
        f.setVisible(true);
    }
    
    private static <T extends Real<T>> double evaluate(NeoComplex<T> p) {
//        if (p.imag().sign()) return -1;
        
        T t = p.real();
        T three = t.two().add(t.one());
        // Convert real to base 3 to amount of digits specified by some factor (log?) of the imag
        
//        T log3 = three.log();
//        T base3 = p.real().log().div(log3);
        T degree = p.imag().log().negate().mult(three);
        for (int i = 1; i < degree.toDouble(); i++) {
            T digit = p.real().div(three.pow(Real.fromDouble(t, i).negate())).mod(three).floor();
            if (digit.equals(t.one())) {
//                return i;
                return i;
            }
        }
        for (int d = 0 ; d < 25; d++) {
            for (int i = 1; i < (degree.toDouble() - Math.floor(degree.toDouble())) * Math.pow(3, d); i++) {
                T digit = p.real().div(three).div(three.pow(Real.fromDouble(t, i).negate())).mod(three).floor();
                if (digit.equals(t.one())) {
                    return Math.floor(degree.toDouble()) + i / Math.pow(3, d);
                }
            }
        }
        return maxEscape;
    }
    
    private static final int maxEscape = 100;
    private static Gradient grad = new Gradient(new Color[] {Color.blue.darker(), Color.cyan.darker(), Color.yellow.darker().darker(), Color.red.darker(), Color.magenta.darker(), Color.blue.darker()});
    
    public static <T extends Real<T>> Runnable drawFractal(FunctionDrawer<T> drawer) {
        T t = drawer.factor().real();
        return () -> {
                while (true) {
        //                while (pause) {
        //                    try {Thread.sleep(10);} catch (Exception e) {}
        //                }
                    long time = System.nanoTime();
                    Graphics2D g = drawer.getBuffer().createGraphics();
                    NeoPoint2<T> p = new NeoPoint2<>(Real.fromDouble(t, Math.random() * 2 - 1), Real.fromDouble(t, Math.random() * 2 - 1));
                    NeoComplex<T> wp = drawer.worldMatrix().apply(p).asComplex();
//                    double esc = evaluate(new NeoComplex<>(t.zero()), wp, drawer.factor(), Real.fromDouble(t, 100));
                    double esc = evaluate(wp);
                    
                    Color c;
                    if (esc == 0) c = Color.green;
                    else if (Double.isInfinite(esc)) c = Color.yellow;
                    else if (Double.isNaN(esc)) c = Color.red;
                    else if (esc < maxEscape) {
                        c = grad.get(Math.log10(esc) % 1);
                    } else {
                        c = Color.black;
                    }
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 63));
                    
                    NeoPoint2<DoubleReal> sp = drawer.screenMatrix().apply(new Point2Double(p.x().toDouble(), p.y().toDouble()));
                    g.fillOval((int)Math.round(sp.x().d() - (drawer.pointSize - 1) / 2d), (int)Math.round(sp.y().d() - (drawer.pointSize - 1) / 2d), (int)drawer.pointSize, (int)drawer.pointSize);
                    drawer.mspd = drawer.mspd * 0.999 + (System.nanoTime() - time) / 1000000d * 0.001;
                    drawer.repaint();
                }
            };
    }
}