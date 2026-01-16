package com.up.math.examples;

import com.up.math.matrix.AffineMatrix2;
import com.up.math.number.Complex;
import com.up.math.vector.Point2;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Main {
    
    // new Complex(1.9, 0.1), new Point2(-0.06612147911913462, -0.2277348267646851)
    // new Complex(4, 2), new Point2(-0.08350828339576327, -0.0015708583190908029)
    // new Complex(2)
    
    public static void main(String[] args) {
//        char[][] out = new char[16][100];
//        for (int i = 0; i < 16; i++) {
//            for (int j = 0; j < 100; j++) {
//                out[i][j] = ' ';
//            }
//        }
//        for (int i = 0; i < 100; i++) {
//            BigFixed bf = BigFixed.fromDouble((i - 0.0) / 20 * Math.PI);
//            int sh = (int)Math.round(bf.sin().toDouble() * 8 + 8);
//            sh = Math.max(0, Math.min(15, sh));
//            out[15 - sh][i] = '*';
//            int ch = (int)Math.round(bf.cos().toDouble() * 8 + 8);
//            ch = Math.max(0, Math.min(15, ch));
//            out[15 - ch][i] = '#';
//        }
//        for (int i = 0; i < 16; i++) System.out.println(String.valueOf(out[i]));
//        if (true) return;
        
        Frame f = new Frame("Fractals");
        f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        f.setSize(1000, 800);
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.3246703815274639, -0.32267018065624486), new Complex(2, 0), 100)));
        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.3246703815274639, -0.32267018065624486), new Complex(2, 0), 100000)));
//        f.add(new StochasticFractalDrawer(new FractalParameters(1.4, new Point2(-0.08350828339576327, -0.0015708583190908029), new Complex(4, 2), 100)));
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
    
    private static class StochasticFractalDrawer extends Canvas {
        
        private Gradient grad = new Gradient(new Color[] {Color.blue.darker(), Color.cyan.darker(), Color.yellow.darker().darker(), Color.red.darker(), Color.magenta.darker(), Color.blue.darker()});
        
        private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        double pointSize = 6;
        
        private AffineMatrix2 screen = AffineMatrix2.identity();
        private AffineMatrix2 zoom;
        private AffineMatrix2 offset;
        private Complex factor;
        private double bound;
        
        private Point2 last = new Point2(0, 0);
        
        private final int threads = 6;
        private boolean pause = false;
        private boolean ui = true;
        private boolean recording = true;
        
        public StochasticFractalDrawer(FractalParameters params) {
            zoom = AffineMatrix2.scale(params.zoom);
            offset = AffineMatrix2.offset(params.offset);
            factor = params.factor;
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
                        Point2 ptl = worldMatrix().linearMap().apply(screen.inverse().linearMap().apply(p.to(last)));
                        offset = AffineMatrix2.offset(ptl).compose(offset);
                        reuseCanvas(AffineMatrix2.offset(last.to(p)));
                        last = p;
                    }
                });
            addMouseWheelListener(e -> {
                double speed = e.isShiftDown() ? 1.1 : 2;
                AffineMatrix2 ds = AffineMatrix2.scale(Math.pow(speed, e.getPreciseWheelRotation()));
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
                    if (e.getKeyChar() == 'u') ui = !ui;
                    if (e.getKeyChar() == 'r') recording = !recording;
                }
            });
            for (int i = 0; i < threads; i++) new Thread(this::calculate).start();
//            new Thread (() -> {
//                    Matrix3 scale = Matrix3.scale(0.999);
//                    while (true) {
//                        if (zoom.determinant() < 1e-30 || zoom.determinant() > 2) scale = scale.inverse();
//                        zoom = scale.compose(zoom);
            // TODO: This should use the reuseCanvas right?
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
        
        private AffineMatrix2 worldMatrix() {
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
//                int esc = fractalCheck(worldMatrix().apply(p).asComplex(), new Complex(0.25, -0.5), factor, bound);
                int esc = fractalCheck(new Complex(0, 0), worldMatrix().apply(p).asComplex(), factor, bound);
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
        
        private void reuseCanvas(AffineMatrix2 m) {
            AffineMatrix2 mm = AffineMatrix2.offset(getWidth() / 2d, getHeight() / 2d).compose(m.compose(AffineMatrix2.offset(-getWidth() / 2d, -getHeight() / 2d)));
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
                fg.setColor(Color.magenta);
                AffineMatrix2 worldToScreen = screen.compose(worldMatrix().inverse());
                Point2 lb = worldToScreen.apply(new Point2(-1, -1));
                Point2 ub = worldToScreen.apply(new Point2(1, 1));
                int size = (int)(lb.to(ub).length() / 25);
                fg.drawOval((int)Math.round(lb.getX()), (int)Math.round(lb.getY()), size, size);
                fg.drawOval((int)Math.round(ub.getX()), (int)Math.round(ub.getY()), size, size);
                
                fg.setColor(Color.white);
                drawOutlineString(fg, "Offset: (" + offset.x() + ", " + offset.y() + ")", 5, 15);
                drawOutlineString(fg, "Factor: " + factor, 5, 30);
                drawOutlineString(fg, "Zoom: " + zoom.a(), 5, 45);
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
            screen = AffineMatrix2.scale(new Point2(width / 2d, height / 2d)).compose(AffineMatrix2.offset(1, 1));
            buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resetCanvas();
        }
    }
    
    private static int maxEscape = 10000;
    
    private static int fractalCheck(Complex z, Complex c, Complex exp, double bound) {
        int i;
        for (i = 0; i < maxEscape && z.magnitude() < bound; i++) {
            z = z.pow(exp).add(c);
//            z = z.multiply(z).add(c);
        }
        return i;
    }
    
//    private static class PPMImage {
//        
//        public static void write(OutputStream os, BufferedImage i) throws IOException {
//            os.write("P6\n".getBytes());
//            os.write((i.getWidth() + " " + i.getHeight() + "\n").getBytes());
//            os.write("255\n".getBytes());
//            for (int y = 0; y < i.getHeight(); y++) {
//                for (int x = 0; x < i.getWidth(); x++) {
//                    int argb = i.getRGB(x, y);
////                    os.write((argb >> 24) & 0xFF);
//                    os.write((argb >> 16) & 0xFF);
//                    os.write((argb >> 8) & 0xFF);
//                    os.write(argb & 0xFF);
////                    os.write(" ".getBytes());
//                }
//            }
//            os.flush();
//        }
//    }
}