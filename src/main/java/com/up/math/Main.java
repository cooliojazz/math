package com.up.math;

import com.up.math.matrix.Matrix3;
import com.up.math.vector.Point2;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Main {
    
    public static void main(String[] args) {
        Frame f = new Frame("Fractals");
        f.setSize(1000, 800);
        f.add(new StochasticFractalDrawer());
        f.setVisible(true);
    }
    
    private record Gradient(Color[] stops) {
        
        public Color get(double t) {
            double v = Math.max(0, Math.min(1, t)) * (stops.length - 1);
            Color c1 = stops[(int)Math.floor(v)];
            Color c2 = stops[(int)Math.ceil(v)];
            return new Color((int)lerp(c1.getRed(), c2.getRed(), v - (int)v), (int)lerp(c1.getGreen(), c2.getGreen(), v - (int)v), (int)lerp(c1.getBlue(), c2.getBlue(), v - (int)v));
        }
        
        private double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }
    }
    
    private static class StochasticFractalDrawer extends Canvas {
        
        private Gradient grad = new Gradient(new Color[] {Color.blue.darker(), Color.cyan.darker(), Color.yellow.darker().darker(), Color.red.darker(), Color.magenta.darker(), Color.blue.darker()});
        
        private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        double pointSize = 8;
        
        private Matrix3 screen = Matrix3.identity();
//        private Matrix3 zoom = Matrix3.identity();
        private Matrix3 zoom = Matrix3.scale(2);
//        private Matrix3 offset = Matrix3.identity();
private Matrix3 offset = Matrix3.offset(new Point2(-0.06612147911913462, -0.2277348267646851));
//        private Complex factor = new Complex(1.9, 0.1);
        private Complex factor = new Complex(4, 2);
//        private Complex factor = new Complex(2);
        private double bound = 2;
        
        private Point2 last = new Point2(0, 0);
        
        private final int threads = 6;
        private boolean pause = false;
        
        public StochasticFractalDrawer() {
            addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        last = new Point2(e.getPoint());
                    }
                });
            addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        Point2 p = new Point2(e.getPoint());
//                        Point2 ip = screen.inverse().apply(p);
//                        Point2 il = screen.inverse().apply(last);
//                        Point2 wp = worldMatrix().apply(ip);
//                        Point2 wl = worldMatrix().apply(il);
//                        offset = Matrix3.offset(wp.to(wl)).compose(offset);
                        Point2 ptl = worldMatrix().linearMap().apply(screen.inverse().linearMap().apply(p.to(last)));
                        offset = Matrix3.offset(ptl).compose(offset);
                        reuseCanvas(Matrix3.offset(last.to(p)));
                        last = p;
                    }
                });
            addMouseWheelListener(e -> {
    //                Matrix3 ds = Matrix3.scale(Math.pow(2, e.getPreciseWheelRotation()));
                    Matrix3 ds = Matrix3.scale(Math.pow(2, e.getPreciseWheelRotation()));
//                    view = Matrix3.offset(ds.apply(new Point2(view.c(), view.f()))).compose(ds.compose(view.linearMap().promote()));
                    zoom = ds.compose(zoom);
                    reuseCanvas(ds.inverse());
                });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == ',') {
                        factor = factor.subtract(new Complex(0.1));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == '.') {
                        factor = factor.add(new Complex(0.1));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == ';') {
                        factor = factor.subtract(new Complex(0, 0.1));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == '\'') {
                        factor = factor.add(new Complex(0, 0.1));
                        resetCanvas();
                    }
                    
                    if (e.getKeyChar() == '[') {
                        if (pointSize > 1) pointSize--;
                    }
                    if (e.getKeyChar() == ']') {
                        pointSize++;
                    }
                    
                    if (e.getKeyChar() == 'p') pause = !pause;
                }
            });
            for (int i = 0; i < threads; i++) new Thread(this::calculate).start();
            new Thread (() -> {
                    Matrix3 scale = Matrix3.scale(0.99);
                    while (true) {
//                        if (zoom.determinant() < 1e-30 || zoom.determinant() > 1) scale = scale.inverse();
//                        zoom = scale.compose(zoom);
//                        factor = factor.add(new Complex(0.001));
                        bound += 0.1;
                        try {Thread.sleep(500);} catch (Exception e) {}
                    }
                }).start();
        }
        
        private Matrix3 worldMatrix() {
            return offset.compose(zoom);
        }
        
        private static double mspd = 0;
        
        private void calculate() {
            while (true) {
                while (pause) {
                    try {Thread.sleep(10);} catch (Exception e) {}
                }
                long time = System.nanoTime();
                Graphics2D g = buffer.createGraphics();
                Point2 p = new Point2(Math.random() * 2 - 1, Math.random() * 2 - 1);
                int esc = fractalCheck(worldMatrix().apply(p).asComplex(), new Complex(0.25, -0.5), factor, bound);
//                synchronized (buffer) {
                    if (esc < maxEscape) {
                        Color c = grad.get(Math.log10(esc) % 1);
//                        Color c = grad.get(Math.log(esc) / Math.log(maxEscape));
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 63));
                    } else {
                        g.setColor(Color.black);
                    }
//                Point2 sp = p.mul(new Point2(getWidth(), getHeight()));
                    Point2 sp = screen.apply(p);
//                g.drawRect((int)Math.round(sp.x), (int)Math.round(sp.y), 0, 0);
                    g.fillOval((int)Math.round(sp.x - (pointSize - 1) / 2d), (int)Math.round(sp.y - (pointSize - 1) / 2d), (int)pointSize, (int)pointSize);
//                }
                mspd = mspd * 0.9999 + (System.nanoTime() - time) / 1000000d * 0.0001;
                repaint();
            }
        }
        
        private void resetCanvas() {
            Graphics2D g = buffer.createGraphics();
            g.setBackground(Color.black);
            g.clearRect(0, 0, getWidth(), getHeight());
            repaint();
        }
        
        private void reuseCanvas(Matrix3 m) {
//            int[] samples = buffer.getRaster().getPixels(0, 0, buffer.getWidth(), buffer.getHeight(), (int[])null);
//            Graphics2D g = buffer.createGraphics();
//            g.setBackground(Color.black);
//            g.clearRect(0, 0, getWidth(), getHeight());
//            Point2 ts = m.apply(new Point2(4, 4));
//            for (int x = 0; x < getWidth(); x += 4) {
//                for (int y = 0; y < getHeight(); y += 4) {
//                    Point2 tp = m.apply(new Point2(x, y));
//                    g.setColor(new Color(samples[(y * getWidth() + x) * 4], samples[(y * getWidth() + x) * 4 + 1], samples[(y * getWidth() + x) * 4 + 2], 255));
//                    g.fillRect((int)tp.getX(), (int)tp.getY(), (int)ts.getX(), (int)ts.getY());
//                }
//            }
            Matrix3 mm = Matrix3.offset(getWidth() / 2d, getHeight() / 2d).compose(m.compose(Matrix3.offset(-getWidth() / 2d, -getHeight() / 2d)));
            Point2 tp = mm.apply(new Point2(0, 0));
//            Point2 ts = mm.linearMap().apply(new Point2(getWidth(), getHeight()));
            Point2 te = mm.apply(new Point2(getWidth(), getHeight()));
            BufferedImage back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = back.createGraphics();
            g.setBackground(Color.black);
            g.clearRect(0, 0, getWidth(), getHeight());
//            g.drawImage(buffer, (int)tp.getX(), (int)tp.getY(), (int)ts.getX(), (int)ts.getY(), null);
            g.drawImage(buffer, (int)tp.getX(), (int)tp.getY(), (int)te.getX(), (int)te.getY(), 0, 0, getWidth (), getHeight(),null);
            buffer = back;
            
            repaint();
        }
        
        @Override
        public void paint(Graphics g) {
//            g.clearRect(0, 0, getWidth(), getHeight());
//            g.drawImage(buffer, 0, 0, null);
            
            BufferedImage frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D fg = frame.createGraphics();
            fg.drawImage(buffer, 0, 0, null);
            
            fg.setColor(Color.magenta);
            Matrix3 worldToScreen = screen.compose(worldMatrix().inverse());
            Point2 lb = worldToScreen.apply(new Point2(-1, -1));
            Point2 ub = worldToScreen.apply(new Point2(1, 1));
            int size = (int)(lb.to(ub).length() / 25);
            fg.drawOval((int)Math.round(lb.getX()), (int)Math.round(lb.getY()), size, size);
            fg.drawOval((int)Math.round(ub.getX()), (int)Math.round(ub.getY()), size, size);
            
            fg.setColor(Color.white);
            drawOutlineString(fg, "Factor: " + factor, 5, 15);
            drawOutlineString(fg, "Draw Size: " + pointSize, 5, 30);
            drawOutlineString(fg, "Ms/d: " + (int)(mspd * 10000) / 10000d, 5, 45);
            drawOutlineString(fg, "D/s: " + (int)(1000 / mspd * threads * 10) / 10d, 5, 60);
            fg.drawOval(getWidth() / 2 - 5, getHeight() /  2 - 5, 10, 10);
            
//            g.clearRect(0, 0, getWidth(), getHeight());
//            synchronized (buffer) {
//                g.drawImage(buffer, 0, 0, null);
//            }
            g.drawImage(frame, 0, 0, null);
        }
        
        private static void drawOutlineString(Graphics g, String s, int x, int y) {
            g.setColor(Color.black);
            for (int sx = -1; sx < 2; sx++) {
                for (int sy = -1; sy < 2; sy++) {
                    g.drawString(s, x + sx, y + sy);
                }
            }
            g.setColor(Color.white);
            g.drawString(s, x, y);
        }
        
        @Override
        public void update(Graphics g) {
            paint(g);
        }
        
        @Override
        public void reshape(int x, int y, int width, int height) {
            super.reshape(x, y, width, height);
//            int sMax = Math.max(width, height);
//            screen = Matrix3.scale(new Point2(sMax / 2d, sMax / 2d)).compose(Matrix3.offset(1, 1));
            screen = Matrix3.scale(new Point2(width / 2d, height / 2d)).compose(Matrix3.offset(1, 1));
//            screen = Matrix3.offset(new Point2(-sMax / 2d, -sMax / 2d)).compose(Matrix3.scale(new Point2(sMax, sMax)));
            buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resetCanvas();
        }
    }
    
    private static int maxEscape = 10000;
    
    private static int fractalCheck(Complex z, Complex c, Complex exp, double bound) {
        int i;
        for (i = 0; i < maxEscape && z.magnitude() < bound; i++) {
            z = z.pow(exp).add(c);
        }
        return i;
    }
}