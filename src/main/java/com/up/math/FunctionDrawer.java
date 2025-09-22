package com.up.math;

import com.up.math.matrix.NeoMatrix3;
import com.up.math.matrix.Matrix3;
import com.up.math.number.*;
import com.up.math.vector.NeoPoint2;
import com.up.math.vector.Point2;
import com.up.math.vector.Point2Double;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class FunctionDrawer<T extends Real<T>> extends Canvas {

    public record DrawingParameters<T extends Real<T>>(T zoom, NeoPoint2<T> offset, NeoComplex<T> factor) {}
        
//    private Class<T> type;
    
    private BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    double pointSize = 10;

    private NeoMatrix3<DoubleReal> screen = NeoMatrix3.identity(DoubleReal::new);
    private NeoMatrix3<T> zoom;
    private NeoMatrix3<T> offset;
    // TODO: This probably should be removed as its specific to the fractals?
    private NeoComplex<T> factor;

    private Point2Double last = new Point2Double(0);

    private final int threads = Runtime.getRuntime().availableProcessors() / 2;
    private boolean pause = false;
    private boolean ui = true;
    private boolean recording = true;

    public FunctionDrawer(DrawingParameters<T> params, T example, Function<FunctionDrawer<T>, Runnable> drawer) {
        T zero = example.zero();
        
        zoom = NeoMatrix3.scale(params.zoom);
        offset = NeoMatrix3.offset(params.offset);
        factor = params.factor;

        addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    last = new Point2Double(e.getPoint());
                }
            });
        addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Point2Double p = new Point2Double(e.getPoint());
                    NeoPoint2<T> ptl = worldMatrix().linearMap().apply(screen.inverse().linearMap().apply(p.to(last)).toType(d -> Real.fromDouble(zero, d.d())));
                    offset = NeoMatrix3.offset(ptl).compose(offset);
                    reuseCanvas(NeoMatrix3.offset(last.to(p)));
                    last = p;
                }
            });
        addMouseWheelListener(e -> {
                double speed = e.isShiftDown() ? 1.1 : 2;
//            NeoMatrix3<T> ds = NeoMatrix3.scale(zero.fromDouble(Math.pow(speed, e.getPreciseWheelRotation())));
//            zoom = ds.compose(zoom);
//            reuseCanvas(ds.inverse());
                zoom = NeoMatrix3.scale(Real.fromDouble(zero, Math.pow(speed, e.getPreciseWheelRotation()))).compose(zoom);
                reuseCanvas(NeoMatrix3.scale(new DoubleReal(Math.pow(speed, e.getPreciseWheelRotation())).inverse()));
            });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                T pointOne = Real.fromDouble(zero, 0.1);
                if (e.getKeyChar() == ',') {
                    factor = factor.subtract(new NeoComplex<>(pointOne));
                    resetCanvas();
                }
                if (e.getKeyChar() == '.') {
                    factor = factor.add(new NeoComplex<>(pointOne));
                    resetCanvas();
                }
                if (e.getKeyChar() == ';') {
                    factor = factor.subtract(new NeoComplex<>(zero, pointOne));
                    resetCanvas();
                }
                if (e.getKeyChar() == '\'') {
                    factor = factor.add(new NeoComplex<>(zero, pointOne));
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
        for (int i = 0; i < threads; i++) new Thread(drawer.apply(this)).start();
    }

    public NeoMatrix3<T> worldMatrix() {
        return offset.compose(zoom);
    }
    
    public NeoMatrix3<DoubleReal> screenMatrix() {
        return screen;
    }
    
    public NeoComplex<T> factor() {
        return factor;
    }

    public double mspd = 0;

    private void resetCanvas() {
        Graphics2D g = buffer.createGraphics();
        g.setBackground(Color.black);
        g.clearRect(0, 0, getWidth(), getHeight());
        repaint();
    }

    private void reuseCanvas(NeoMatrix3<DoubleReal> m) {
        NeoMatrix3<DoubleReal> mm = NeoMatrix3.offset(new Point2Double(getWidth() / 2d, getHeight() / 2d)).compose(m.compose(NeoMatrix3.offset(new Point2Double(-getWidth() / 2d, -getHeight() / 2d))));
        NeoPoint2<DoubleReal> tp = mm.apply(new Point2Double(0, 0));
        NeoPoint2<DoubleReal> te = mm.apply(new Point2Double(getWidth(), getHeight()));
        BufferedImage back = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = back.createGraphics();
        g.setBackground(Color.black);
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(buffer, (int)tp.x().d(), (int)tp.y().d(), (int)te.x().d(), (int)te.y().d(), 0, 0, getWidth (), getHeight(),null);
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
            drawOutlineString(fg, "Offset: (" + offset.c() + ", " + offset.f() + ")", 5, 15);
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
        screen = NeoMatrix3.scale(new DoubleReal(width / 2d), new DoubleReal(height / 2d)).compose(NeoMatrix3.offset(new Point2Double(1, 1)));
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resetCanvas();
    }
    
    public BufferedImage getBuffer() {
        return buffer;
    }
}
