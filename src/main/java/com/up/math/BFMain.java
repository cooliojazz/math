package com.up.math;

import com.up.math.matrix.BigFixedMatrix3;
import com.up.math.matrix.Matrix3;
import com.up.math.number.*;
import com.up.math.vector.BigFixedPoint2;
import com.up.math.vector.Point2;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.math.BigInteger;

public class BFMain {

    // new Complex(1.9, 0.1), new Point2(-0.06612147911913462, -0.2277348267646851)
    // new Complex(4, 2), new Point2(-0.08350828339576327, -0.0015708583190908029)
    // new Complex(2)

    public static void main(String[] args) {
        
        Frame f = new Frame("Fractals");
        f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        f.setSize(1000, 800);
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.32467038152740657, -0.32267018065619124), new Complex(2, 0), 100)));
        f.add(new StochasticFractalDrawer<>(new FractalParameters(1.4, new Point2(-0.32467038152740657, -0.32267018065619124), new Complex(2, 0), 100), IntFixed.class));
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

    private record FractalParameters(double zoom, Point2 offset, Complex factor, double bound) {}

    private static class StochasticFractalDrawer<T extends BigFixed<T>> extends Canvas {
        
        private Class<T> type;

        private Gradient grad = new Gradient(new Color[] {Color.blue.darker(), Color.cyan.darker(), Color.yellow.darker().darker(), Color.red.darker(), Color.magenta.darker(), Color.blue.darker()});

        private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        double pointSize = 10;

        private Matrix3 screen = Matrix3.identity();
        private BigFixedMatrix3<T> zoom;
        private BigFixedMatrix3<T> offset;
        private ComplexBigFixed<T> factor;
        private double bound;

        private Point2 last = new Point2(0, 0);

        private final int threads = Runtime.getRuntime().availableProcessors() * 3 / 4;
        private boolean pause = false;
        private boolean ui = true;
        private boolean recording = true;

        public StochasticFractalDrawer(FractalParameters params, Class<T> type) {
            this.type = type;
            
            zoom = BigFixedMatrix3.scale(BigFixed.fromDouble(params.zoom, type));
            offset = BigFixedMatrix3.offset(BigFixedPoint2.fromPoint2(params.offset, type));
            factor = ComplexBigFixed.fromComplex(params.factor, type);
            bound = params.bound;

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
                        BigFixedPoint2<T> ptl = worldMatrix().linearMap().apply(BigFixedPoint2.fromPoint2(screen.inverse().linearMap().apply(p.to(last)), type));
                        offset = BigFixedMatrix3.offset(ptl).compose(offset);
                        reuseCanvas(Matrix3.offset(last.to(p)));
                        last = p;
                    }
                });
            addMouseWheelListener(e -> {
                    double speed = e.isShiftDown() ? 1.1 : 2;
                    Matrix3 ds = Matrix3.scale(Math.pow(speed, e.getPreciseWheelRotation()));
                    zoom = BigFixedMatrix3.fromMatrix3(ds, type).compose(zoom);
                    reuseCanvas(ds.inverse());
                });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == ',') {
                        factor = factor.subtract(new ComplexBigFixed<>(BigFixed.fromDouble(0.1, type)));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == '.') {
                        factor = factor.add(new ComplexBigFixed<>(BigFixed.fromDouble(0.1, type)));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == ';') {
                        factor = factor.subtract(new ComplexBigFixed<>(BigFixed.fromDouble(0, type), BigFixed.fromDouble(0.1, type)));
                        resetCanvas();
                    }
                    if (e.getKeyChar() == '\'') {
                        factor = factor.add(new ComplexBigFixed<>(BigFixed.fromDouble(0, type), BigFixed.fromDouble(0.1, type)));
                        resetCanvas();
                    }

                    if (e.getKeyChar() == '[') {
                        if (pointSize > 1) pointSize--;
                    }
                    if (e.getKeyChar() == ']') {
                        pointSize++;
                    }

                    if (e.getKeyChar() == 'p') pause = !pause;
                    if (e.getKeyChar() == 'u') ui = !ui;
                    if (e.getKeyChar() == 'r') recording = !recording;
                }
            });
            for (int i = 0; i < threads; i++) new Thread(this::calculate).start();
//            new Thread (() -> {
//                    BigFixedMatrix3 scale = BigFixedMatrix3.scale(BigFixed.fromDouble(0.999));
//                    while (true) {
//                        if (zoom.determinant().compareTo(BigFixed.fromDouble(1e-30)) < 0 || zoom.determinant().compareTo(BigFixed.fromDouble(2)) > 0) scale = scale.inverse();
//                        zoom = scale.compose(zoom);
////                        factor = factor.add(new Complex(0.001));
////                        bound += 0.1;
//                        try {Thread.sleep(100);} catch (Exception e) {}
//                    }
//                }).start();
//            new Thread (() -> {
//                    try {
//                            try {Thread.sleep(1000);} catch (Exception e) {}
////                        File f = new File("out.ppm");
////                        FileOutputStream os = new FileOutputStream(f);
//                        File f = new File("recording.mov");
////                        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(f, 10);
//                        SequenceEncoder enc = new SequenceEncoder(NIOUtils.writableChannel(f), Rational.R(20, 1), Format.MOV, Codec.PNG, (Codec)null);
//                        while (recording) {
////                            PPMImage.write(os, buffer);
////                            enc.encodeImage(buffer);
//                            enc.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(buffer));
//                            try {Thread.sleep(200);} catch (Exception e) {}
//                        }
//                        enc.finish();
////                        os.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }).start();
        }

        private BigFixedMatrix3<T> worldMatrix() {
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
                int esc = fractalCheck(ComplexBigFixed.fromComplex(new Complex(0, 0), type), worldMatrix().apply(BigFixedPoint2.fromPoint2(p, type)).asComplex(), factor, BigFixed.fromDouble(bound, type));
//                int esc = fractalCheck(worldMatrix().apply(BigFixedPoint2.fromPoint2(p, IntFixed.class)).asComplex(), ComplexBigFixed.fromComplex(new Complex(0.25, -0.5), IntFixed.class), factor, IntFixed.fromDouble(bound));
//                synchronized (buffer) {
                    if (esc < maxEscape) {
                        Color c = grad.get(Math.log10(esc) % 1);
//                        Color x = grad.get(Math.log(esc) / Math.log(maxEscape));
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 63));
                    } else {
                        g.setColor(Color.black);
                    }
//                Point2 sp = p.mul(new Point2(getWidth(), getHeight()));
                    Point2 sp = screen.apply(p);
//                g.drawRect((int)Math.round(sp.x), (int)Math.round(sp.y), 0, 0);
                    g.fillOval((int)Math.round(sp.x - (pointSize - 1) / 2d), (int)Math.round(sp.y - (pointSize - 1) / 2d), (int)pointSize, (int)pointSize);
//                }
                mspd = mspd * 0.999 + (System.nanoTime() - time) / 1000000d * 0.001;
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
            Matrix3 mm = Matrix3.offset(getWidth() / 2d, getHeight() / 2d).compose(m.compose(Matrix3.offset(-getWidth() / 2d, -getHeight() / 2d)));
            Point2 tp = mm.apply(new Point2(0, 0));
            Point2 te = mm.apply(new Point2(getWidth(), getHeight()));
            BufferedImage back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = back.createGraphics();
            g.setBackground(Color.black);
            g.clearRect(0, 0, getWidth(), getHeight());
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

            if (ui) {
//                fg.setColor(Color.magenta);
//                Matrix3 worldToScreen = screen.compose(worldMatrix().inverse());
//                Point2 lb = worldToScreen.apply(new Point2(-1, -1));
//                Point2 ub = worldToScreen.apply(new Point2(1, 1));
//                int size = (int)(lb.to(ub).length() / 25);
//                fg.drawOval((int)Math.round(lb.getX()), (int)Math.round(lb.getY()), size, size);
//                fg.drawOval((int)Math.round(ub.getX()), (int)Math.round(ub.getY()), size, size);

                fg.setColor(Color.white);
                drawOutlineString(fg, "Offset: (" + offset.c().toDouble() + ", " + offset.f().toDouble() + ")", 5, 15);
                drawOutlineString(fg, "Factor: " + factor, 5, 30);
                drawOutlineString(fg, "Zoom: " + zoom.a().toDouble(), 5, 45);
                drawOutlineString(fg, "Draw Size: " + pointSize, 5, 60);
                drawOutlineString(fg, "Ms/c: " + (int)(mspd * 10000) / 10000d, 5, 75);
                drawOutlineString(fg, "D/s: " + (int)(1000 / mspd * threads * 10) / 10d, 5, 90);
                fg.drawOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);
            }

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
            if (width % 2 != 0) width++;
            if (height % 2 != 0) height++;
            screen = Matrix3.scale(new Point2(width / 2d, height / 2d)).compose(Matrix3.offset(1, 1));
            buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resetCanvas();
        }
    }

    private static int maxEscape = 10000;

    /**
     * Ignores exp for now since ComplexBigFixed is missing pow
     */
    private static <T extends BigFixed<T>> int fractalCheck(ComplexBigFixed<T> z, ComplexBigFixed<T> c, ComplexBigFixed<T> exp, BigFixed<T> bound) {
        int i;
        for (i = 0; i < maxEscape && z.magnitudeSq().compareTo(bound.square()) < 0; i++) {
//            z = z.pow(exp).add(c);
            z = z.multiply(z).add(c);
        }
        return i;
    }
}