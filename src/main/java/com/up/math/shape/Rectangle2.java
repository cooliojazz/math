package com.up.math.shape;

import com.up.math.matrix.AffineMatrix2;
import com.up.math.matrix.Matrix3;
import com.up.math.vector.Point2;

public record Rectangle2(Point2 start, Point2 end) {

    public Rectangle2(double p1x, double p1y, double p2x, double p2y) {
        this(new Point2(Math.min(p1x, p2x), Math.min(p1y, p2y)), new Point2(Math.max(p1x, p2x), Math.max(p1y, p2y)));
    }

    public Point2 getStart() {
        return start;
    }

    public Point2 getEnd() {
        return end;
    }

    public double getWidth() {
        return end.x - start.x;
    }

    public double getHeight() {
        return end.y - start.y;
    }

    public Point2 getDimensions() {
        return new Point2(getWidth(), getHeight());
    }

    public boolean contains(Point2 p) {
        return p.x >= start.x && p.x <= end.x && p.y >= start.y && p.y <= end.y;
    }

    public boolean intersects(Rectangle2 rect) {
        return ((rect.start.x >= start.x && rect.start.x < end.x) || (rect.end.x >= start.x && rect.end.x < end.x) || (start.x >= rect.start.x && start.x < rect.end.x) || (end.x >= rect.start.x && end.x < rect.end.x)) &&
                ((rect.start.y >= start.y && rect.start.y < end.y) || (rect.end.y >= start.y && rect.end.y < end.y) || (start.y >= rect.start.y && start.y < rect.end.y) || (end.y >= rect.start.y && end.y < rect.end.y));
    }

    public Rectangle2 merge(Rectangle2 rect) {
        return new Rectangle2(new Point2(Math.min(start.x, rect.start.x), Math.min(start.y, rect.start.y)), new Point2(Math.max(end.x, rect.end.x), Math.max(end.y, rect.end.y)));
    }

    public Rectangle2 transformAndAlign(AffineMatrix2 mat) {
        Point2 c1 = mat.apply(start);
        Point2 c2 = mat.apply(new Point2(start.x, end.y));
        Point2 c3 = mat.apply(end);
        Point2 c4 = mat.apply(new Point2(end.x, start.y));
        return new Rectangle2(Math.min(c1.x, Math.min(c2.x, Math.min(c3.x, c4.x))), Math.min(c1.y, Math.min(c2.y, Math.min(c3.y, c4.y))),
                              Math.max(c1.x, Math.max(c2.x, Math.max(c3.x, c4.x))), Math.max(c1.y, Math.max(c2.y, Math.max(c3.y, c4.y))));
    }
}
