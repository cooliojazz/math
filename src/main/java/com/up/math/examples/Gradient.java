package com.up.math.examples;

import java.awt.*;

record Gradient(Color[] stops) {
    
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
