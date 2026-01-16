package com.up.math.examples;

import com.up.math.number.*;
import com.up.math.vector.NeoPoint2;
import com.up.math.vector.Point2Double;

import java.awt.*;
import java.awt.event.*;

import static com.up.math.number.Precision.P1_2;

public class BFMain {

    // new Complex(1.9, 0.1), new Point2(-0.06612147911913462, -0.2277348267646851)
    // new Complex(4, 2), new Point2(-0.08350828339576327, -0.0015708583190908029)
    // new Complex(2)

    public static void main(String[] args) {
////        System.out.println(IntFixed.ZERO.fromDouble(4.6).exp2());
////        System.out.println(IntFixed.ZERO.fromDouble(0.125).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(0.25).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(0.5).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(0).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(1).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(2).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(4).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(8).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(16).log2());
////        System.out.println(IntFixed.ZERO.fromDouble(3).pow(IntFixed.ZERO.fromDouble(2.5)));
////        System.out.println(IntFixed.exp2(-1000));
////        System.out.println(IntFixed.TWO.powNew(IntFixed.ZERO.fromDouble(2.5)));
//        System.out.println(new NeoComplex<>(IntFixed.E).pow(new NeoComplex<>(IntFixed.ZERO, IntFixed.PI)));
////        System.out.println(IntFixed.E.pow(IntFixed.PI));
//        if (true) return;
        
        Frame f = new Frame("Fractals");
        f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        f.setSize(1000, 800);
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.32467038152740657, -0.32267018065619124), new Complex(2, 0), 100)));
//        f.add(new StochasticFractalDrawer<>(new FractalParameters(1.4, new Point2(-0.32467038152740657, -0.32267018065619124), new Complex(2, 0), 100), DoubleReal.class));
//        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(new DoubleReal(2.5), new Point2Double(-0.32467038152740657, -0.32267018065619124), new NeoComplex<>(new DoubleReal(2))), new DoubleReal(0), BFMain::drawFractal));
//        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(new DoubleReal(2.5), new Point2Double(-1.765912092029173, 0.04135655056468296), new NeoComplex<>(new DoubleReal(2))), new DoubleReal(0), BFMain::drawFractal));
//        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(IntFixed.fromDouble(1.4), new NeoPoint2<>(IntFixed.fromDouble(-0.32467038152740657), IntFixed.fromDouble(-0.32267018065619124)), new NeoComplex<>(IntFixed.fromInt(2))), IntFixed.ZERO, BFMain::drawFractal));
        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(IntFixed.fromDouble(P1_2, 1.4), new NeoPoint2<>(IntFixed.fromHexString(P1_2, "-00000001.41e573611c9e57ee"), IntFixed.fromHexString(P1_2, "-00000000.68ccdcd44e3a9f56")), new NeoComplex<>(IntFixed.fromInt(P1_2, 2))), IntFixed.fromInt(P1_2, 2), BFMain::drawFractal));
//        f.add(new FunctionDrawer<>(new FunctionDrawer.DrawingParameters<>(ShortFixed.PI().fromDouble(1.4), new NeoPoint2<>(ShortFixed.PI().fromDouble(-0.32467038152740657), ShortFixed.PI().fromDouble(-0.32267018065619124)), new NeoComplex<>(ShortFixed.fromInt(2))), ShortFixed.PI(), BFMain::drawFractal));
        f.setVisible(true);
    }
    
    private static Gradient grad = new Gradient(new Color[] {Color.blue.darker(), Color.cyan.darker(), Color.yellow.darker().darker(), Color.red.darker(), Color.magenta.darker(), Color.blue.darker()});
//    private static Gradient grad = new Gradient(new Color[] {Color.blue.darker().darker(), Color.green.darker().darker()});
//    static int pointSize = 8;
    
    public static <T extends Real<T>> Runnable drawFractal(FunctionDrawer<T> drawer) {
        T t = drawer.factor().real();
        return () -> {
                while (true) {
        //                while (pause) {
        //                    try {Thread.sleep(10);} catch (Exception e) {}
        //                }
                    long time = System.nanoTime();
                    Graphics2D g = drawer.getBuffer().createGraphics();
//                    Point2Double p = new Point2Double(Math.random() * 2 - 1, Math.random() * 2 - 1);
                    NeoPoint2<T> p = new NeoPoint2<>(Real.fromDouble(t, Math.random() * 2 - 1), Real.fromDouble(t, Math.random() * 2 - 1));
                    NeoComplex<T> wp = drawer.worldMatrix().apply(p).asComplex();
                    double esc = fractalCheck(new NeoComplex<>(t.zero()), wp, drawer.factor(), Real.fromDouble(t, 100));
//                    System.out.println("polar times");
//                    System.out.println(NeoComplex.pTime / 1000 / 1000d);
//                    System.out.println(NeoComplex.tTime / 1000 / 1000d);
//                    System.out.println(NeoComplex.cTime / 1000 / 1000d);
//                    double esc = Math.abs(wp.pow((T)IntFixed.TWO).magnitude().toDouble() - new Complex(wp.imag().toDouble(), wp.real().toDouble()).pow(2).magnitude()) * 10000;
//                    double esc = Math.abs(wp.pow(new NeoComplex<>((T)IntFixed.TWO)).magnitude().toDouble() - new Complex(wp.imag().toDouble(), wp.real().toDouble()).pow(2).magnitude()) * 10;
//                    double esc = Math.abs(wp.real().pow(wp.imag()).toDouble() - Math.pow(wp.real().toDouble(), wp.imag().toDouble())) * 10000;
//                    double esc = Math.abs(Real.atan2(wp.real(), wp.imag()).toDouble() - Math.atan2(wp.imag().toDouble(), wp.real().toDouble())) * 10000;
//                    double esc = Math.abs(wp.imag().toDouble() / wp.real().toDouble()) * 1;
//                    double esc = (wp.real().sqrt().toDouble() - Math.sqrt(wp.real().toDouble())) * 10000;
//                    double esc = wp.real().abs().toDouble() < 1 ? Math.abs(wp.real().atan().toDouble() - Math.atan(wp.real().toDouble())) : -1;
//                    double esc = Math.abs(wp.real().sinh().toDouble() - Math.sinh(wp.real().toDouble())) * 1000000;
//                    double esc = Math.abs(((BigFixed)wp.real()).exp2().toDouble() - Math.exp(wp.real().toDouble() * Math.log(2))) * 10000;
//                    double esc = (int)((wp.real().toDouble() * wp.imag().toDouble() - wp.real().mult(wp.imag()).toDouble()) * 1e12);
//                    double esc = wp.multiply(wp).subtract(wp.pow(new NeoComplex<>((T)new DoubleReal(2)))).magnitude().toDouble();
//                    double esc = wp.pow((T)new DoubleReal(2)).subtract(wp.multiply(wp)).magnitude().toDouble();
//                    double esc = wp.pow((T)new DoubleReal(2)).subtract(wp.pow(new NeoComplex<>((T)new DoubleReal(2)))).magnitude().toDouble();
                    
                    Color c;
                    if (esc == 0) c = Color.green;
                    else if (Double.isInfinite(esc)) c = Color.yellow;
                    else if (Double.isNaN(esc)) c = Color.red;
                    else if (esc < maxEscape) {
                        c = grad.get(Math.log10(esc) % 1);
//                        for (int i = 0; i < Math.log10(esc); i++) c = c.darker();
//                        Color c = grad.get((esc * 1) % 1);
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
    
//    private final CopyOnWriteArrayList<Point2> queue = new CopyOnWriteArrayList<>();
//    private int blockLevel = 1;
//    
//    private void alternateRenderManager() {
//        while (true) {
//            while (pause || !queue.isEmpty()) {
//                try {Thread.sleep(10);} catch (Exception e) {}
//            }
//            blockLevel++;
//            int blocks = 1 << blockLevel;
//            Point2 blockSize = new Point2(getWidth(), getHeight()).scale(1d / blocks);
//            Matrix3 si = screen.inverse();
//            for (int x = 0; x < blocks; x++) {
//                for (int y = 0; y < blocks; y++) {
//                    queue.add(si.apply(blockSize.mul(new Point2(x, y))));
//                }
//            }
//        }
//    }
//    
//    private void alternateRender() {
//        while (true) {
//            while (pause || queue.isEmpty()) {
//                try {Thread.sleep(10);} catch (Exception e) {}
//            }
//            Point2 p;
//            try {
//                p = queue.removeFirst();
//            } catch (NoSuchElementException ex) {
//                continue;
//            }
//            long time = System.nanoTime();
//            Graphics2D g = buffer.createGraphics();
//            int esc = fractalCheck(ComplexBigFixed.fromComplex(new Complex(0, 0), type), worldMatrix().apply(BigFixedPoint2.fromPoint2(p, type)).asComplex(), factor, BigFixed.fromDouble(bound, type));
//            if (esc < maxEscape) {
//                Color c = grad.get(Math.log10(esc) % 1);
//                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 63));
//            } else {
//                g.setColor(Color.black);
//            }
//            Point2 sp = screen.apply(p);
//            Point2 blockSize = new Point2(getWidth(), getHeight()).scale(1d / (1 << blockLevel));
//            g.fillRect((int)Math.round(sp.x), (int)Math.round(sp.y), (int)Math.round(blockSize.x), (int)Math.round(blockSize.y));
//            mspd = mspd * 0.999 + (System.nanoTime() - time) / 1000000d * 0.001;
//            repaint();
//        }
//    }

    private static int maxEscape = 100;

    /**
     * Ignores exp for now since most Reals are missing pow
     */
    private static <T extends Real<T>> double fractalCheck(NeoComplex<T> z, NeoComplex<T> c, NeoComplex<T> exp, T bound) {
        int i;
        for (i = 0; i < maxEscape && z.magnitudeSq().compareTo(bound.square()) < 0; i++) {
//            z = z.abs();
//            z = z.multiply(z).add(c);
//            z = z.pow(bound.two()).add(c);
//            z = z.pow((T)new DoubleReal(2)).add(c);
            z = z.pow(exp).add(c);
        }
//        double partial = 0;
        double partial = z.magnitude().toDouble() > 1 ? 1 - Math.log(Math.log(z.magnitude().toDouble()) / Math.log(2)) / Math.log(exp.magnitude().toDouble()) : 0;
        return i + partial;
//        return i;
    }
}