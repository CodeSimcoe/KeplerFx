package com.codesimcoe.spacefx.drawing;

import javafx.scene.canvas.GraphicsContext;

public class DrawingUtil {

    private DrawingUtil() {
        // Non-instantiable
    }

    public static void fillCircle(
        final double centerX,
        final double centerY,
        final double radius,
        final GraphicsContext graphicsContext) {

        double halfRadius = radius / 2;

        double cx = centerX - halfRadius;
        double cy = centerY - halfRadius;

        graphicsContext.fillOval(cx, cy, radius, radius);
    }

    public static void drawCircle(
        final double centerX,
        final double centerY,
        final double radius,
        final double fillOpacity,
        final double strokeOpacity,
        final GraphicsContext graphicsContext) {

        double halfRadius = radius / 2;

        double cx = centerX - halfRadius;
        double cy = centerY - halfRadius;

        graphicsContext.setGlobalAlpha(fillOpacity);
        graphicsContext.fillOval(cx, cy, radius, radius);
        graphicsContext.setGlobalAlpha(strokeOpacity);
        graphicsContext.strokeOval(cx, cy, radius, radius);
    }
}