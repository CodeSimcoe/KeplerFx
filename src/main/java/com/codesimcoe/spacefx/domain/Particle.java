package com.codesimcoe.spacefx.domain;

import javafx.scene.paint.Color;

public class Particle {

    // Position
    private double x;
    private double y;

    // Speed
    private double vx;
    private double vy;

    // Acceleration
    private double ax;
    private double ay;

    private Color color;

    public Particle(
        final double x,
        final double y,
        final double vx,
        final double vy,
        final Color color) {

        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
    }

    public double getX() {
        return this.x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getVx() {
        return this.vx;
    }

    public void setVx(final double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return this.vy;
    }

    public void setVy(final double vy) {
        this.vy = vy;
    }

    public double getAx() {
        return this.ax;
    }

    public void setAx(final double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return this.ay;
    }

    public void setAy(final double ay) {
        this.ay = ay;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public final void update() {

        // Speed update depending on acceleration
        this.vx += this.ax;
        this.vy += this.ay;

        // Position update depending on speed
        this.x += this.vx;
        this.y += this.vy;

//        // Wall bounce
//        if (this.x >= Configuration.CANVAS_WIDTH || this.x <= 0) {
//            this.vx = -this.vx;
//        }
//
//        // Wall bounce
//        if (this.y >= Configuration.CANVAS_HEIGHT || this.y <= 0) {
//            this.vy = -this.vy;
//        }
    }
}