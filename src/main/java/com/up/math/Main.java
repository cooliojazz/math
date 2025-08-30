package com.up.math;

import com.up.math.matrix.ComplexMatrix4;
import com.up.math.matrix.Matrix2;
import com.up.math.matrix.Matrix3;
import com.up.math.matrix.Matrix4;
import com.up.math.shape.Rectangle2;
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
        
        private Gradient grad = new Gradient(new Color[]{Color.blue, Color.cyan, Color.yellow, Color.red});
        
        private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        
        private Matrix3 screen = Matrix3.identity();
        private Matrix3 view = Matrix3.identity();
        private Complex factor = new Complex(2);
        
        private Point2 last = new Point2(0, 0);
        
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
                        Point2 ip = screen.inverse().apply(p);
                        Point2 il = screen.inverse().apply(last);
                        view = Matrix3.offset(view.linearMap().apply(ip).to(view.linearMap().apply(il))).compose(view);
                        reuseCanvas(Matrix3.offset(last.to(p)));
                        last = p;
                    }
                });
            addMouseWheelListener(e -> {
    //                Matrix3 ds = Matrix3.scale(Math.pow(2, e.getPreciseWheelRotation()));
                    Matrix3 ds = Matrix3.scale(Math.pow(2, e.getPreciseWheelRotation()));
//                    view = Matrix3.offset(ds.apply(new Point2(view.c(), view.f()))).compose(ds.compose(view.linearMap().promote()));
                    view = ds.compose(view);
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
                    if (e.getKeyChar() == 'p') pause = !pause;
                }
            });
            for (int i = 0; i < 4; i++) new Thread(this::calculate).start();
        }
        
        private void calculate() {
            while (true) {
                while (pause) {
                    try {Thread.sleep(10);} catch (Exception e) {}
                }
                Graphics2D g = buffer.createGraphics();
                Point2 p = new Point2(Math.random() * 2 - 1, Math.random() * 2 - 1);
                int esc = fractalCheck(view.apply(p).asComplex(), new Complex(0.25, -0.5), factor);
                if (esc < maxEscape) {
                    Color c = grad.get(Math.log(esc) / Math.log(maxEscape));
                    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 63));
                } else {
                    g.setColor(Color.black);
                }
//                Point2 sp = p.mul(new Point2(getWidth(), getHeight()));
                Point2 sp = screen.apply(p);
//                g.drawRect((int)Math.round(sp.x), (int)Math.round(sp.y), 0, 0);
                g.fillOval((int)Math.round(sp.x) - 3, (int)Math.round(sp.y) - 3, 7, 7);
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
            Matrix3 mm = Matrix3.offset(getWidth() / 2, getHeight() / 2).compose(m.compose(Matrix3.offset(-getWidth() / 2, -getHeight() / 2)));
            Point2 tp = mm.apply(new Point2(0, 0));
            Point2 ts = mm.linearMap().apply(new Point2(getWidth(), getHeight()));
            BufferedImage back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = back.createGraphics();
            g.setBackground(Color.black);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(buffer, (int)tp.getX(), (int)tp.getY(), (int)ts.getX(), (int)ts.getY(), null);
            buffer = back;
            
            repaint();
        }
        
        @Override
        public void paint(Graphics g) {
//            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(buffer, 0, 0, null);
            g.setColor(Color.green);
            g.drawString("Factor: " + factor, 5, 15);
        }
        
        @Override
        public void update(Graphics g) {
            paint(g);
        }
        
        @Override
        public void reshape(int x, int y, int width, int height) {
            super.reshape(x, y, width, height);
            int sMax = Math.max(width, height);
            screen = Matrix3.scale(new Point2(sMax / 2d, sMax / 2d)).compose(Matrix3.offset(1, 1));
//            screen = Matrix3.offset(new Point2(-sMax / 2d, -sMax / 2d)).compose(Matrix3.scale(new Point2(sMax, sMax)));
            buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resetCanvas();
        }
    }
    
    private static int maxEscape = 10000;
    
    private static int fractalCheck(Complex z, Complex c, Complex exp) {
//        Complex z = new Complex(0, 0);
        int i;
//        for (i = 0; i < maxEscape && z.magnitude() < 2; i++) {
//            z = z.pow(exp).add(c);
//        }
        for (i = 0; i < maxEscape && z.magnitude() < 2; i++) {
            z = z.pow(exp).add(c);
        }
        return i;
    }
}