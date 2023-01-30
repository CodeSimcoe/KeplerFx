package com.codesimcoe.spacefx.ui;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.codesimcoe.spacefx.configuration.Configuration;
import com.codesimcoe.spacefx.domain.GravityObject;
import com.codesimcoe.spacefx.domain.Particle;
import com.codesimcoe.spacefx.drawing.DrawingUtil;
import com.codesimcoe.spacefx.model.Model;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SpaceUI {

	private static final double MIN_GRAVITY_OBJECT_RADIUS = 5;

    private final Model model = Model.getInstance();

    private final Pane root;

    private final GraphicsContext graphicsContext;

    private CreationMode creationMode = CreationMode.NONE;

    private double dragStartX;
    private double dragStartY;

    private double mouseX;
    private double mouseY;

    private final Random random = new Random();

    // Trajectory prediction
    private final int predictionIterations = 250;
    private final double[] predictionXPoints = new double[this.predictionIterations];
    private final double[] predictionYPoints = new double[this.predictionIterations];

    private static enum CreationMode {
        NONE,
        PARTICLE,
        GRAVITY_OBJECT
    }

    public SpaceUI() {
        this.root = new Pane();

        Canvas canvas = new Canvas(Configuration.CANVAS_WIDTH, Configuration.CANVAS_HEIGHT);
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.root.getChildren().add(canvas);

        this.graphicsContext.setGlobalBlendMode(BlendMode.SRC_OVER);

        // XXX
        this.model.addGravityObject(new GravityObject(600, 400, 30, 30));

        this.root.setOnMousePressed(e -> {
            this.dragStartX = e.getX();
            this.dragStartY = e.getY();
        });

        this.root.setOnMouseDragged(e -> {

            this.mouseX = e.getX();
            this.mouseY = e.getY();

            switch (e.getButton()) {

                case PRIMARY:
                    this.creationMode = CreationMode.PARTICLE;
                    CompletableFuture.runAsync(this::computeTrajectoryPrediction);
                    break;

                case SECONDARY:
                    this.creationMode = CreationMode.GRAVITY_OBJECT;
                    break;

                default:
                    break;
            }
        });

        this.root.setOnMouseReleased(e -> {

            switch (this.creationMode) {

                case GRAVITY_OBJECT:
                    double radius = Math.hypot(
                        this.dragStartX - this.mouseX,
                        this.dragStartY - this.mouseY
                    );
                    
                    if (radius > MIN_GRAVITY_OBJECT_RADIUS) {
	                    GravityObject gravityObject = new GravityObject(
	                        this.dragStartX,
	                        this.dragStartY,
	                        radius,
	                        radius * 1.0 // mass
	                    );
	                    this.model.addGravityObject(gravityObject);
                    }
                    break;

                case PARTICLE:
                    double factor = 10;
                    double vx = (this.dragStartX - this.mouseX) / factor;
                    double vy = (this.dragStartY - this.mouseY) / factor;

                    // Random color
                    double hue = 360 * this.random.nextDouble();
                    Color color = Color.hsb(hue, 1.0, 0.95, 0.95);

                    Particle particle = new Particle(this.mouseX, this.mouseY, vx, vy, color);
                    this.model.addParticle(particle);
                    break;

                case NONE:
                default:
                    break;

            }

            this.creationMode = CreationMode.NONE;
        });

//        // Configuration update
//        this.configuration.getGaussianBlurEffect().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                // Effects
//                this.graphicsContext.setEffect(new GaussianBlur());
//            } else {
//                this.graphicsContext.setEffect(NO_EFFECT);
//            }
//        });
    }

    private void computeTrajectoryPrediction() {

        // Predict speed
        double factor = 10;
        double vx = (this.dragStartX - this.mouseX) / factor;
        double vy = (this.dragStartY - this.mouseY) / factor;

        // Predict trajectory
        Particle predictedParticle = new Particle(this.mouseX, this.mouseY, vx, vy, Color.GRAY);

        for (int i = 0; i < this.predictionIterations; i++) {

            this.predictionXPoints[i] = predictedParticle.getX();
            this.predictionYPoints[i] = predictedParticle.getY();

            this.applyAttraction(predictedParticle);
        }
    }

    public void update() {

        // Remove particles that are too far away

        Iterator<Particle> iterator = this.model.getParticles().iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();

            if (particle.getX() > 2 * Configuration.CANVAS_WIDTH
                || particle.getX() < -Configuration.CANVAS_WIDTH
                || particle.getY() > 2* Configuration.CANVAS_HEIGHT
                || particle.getY() < - Configuration.CANVAS_HEIGHT) {

                iterator.remove();
            } else {
                this.applyAttraction(particle);
            }
        }

//        this.model.getParticles().forEach(this::applyAttraction);
    }

    private void applyAttraction(final Particle particle) {

        // Acceleration
        double ax = 0;
        double ay = 0;

        for (GravityObject gravityObject : this.model.getGravityObjets()) {

            double dx = particle.getX() - gravityObject.x();
            double dy = particle.getY() - gravityObject.y();

            double gravityStrength = 1000 * gravityObject.mass();

            double squaredDistance = dx * dx + dy * dy;
            double force = Math.min(2, gravityStrength / squaredDistance);
            double direction = Math.atan2(dy, dx);

            // Acceleration contribution
            double dax = force * Math.cos(direction);
            double day = force * Math.sin(direction);

            // Attracts
            ax -= dax;
            ay -= day;
        }

        // Apply calculated acceleration
        particle.setAx(ax);
        particle.setAy(ay);

        // Update particle
        particle.update();
    }

    public void draw() {

        // Clear
        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillRect(0, 0, Configuration.CANVAS_WIDTH, Configuration.CANVAS_HEIGHT);

        // Gravity objects
        this.graphicsContext.setStroke(Color.DARKORANGE);
        this.graphicsContext.setFill(Color.DARKORANGE);

        this.model.getGravityObjets().forEach(gravityObject -> {

            // Gravity representation
            DrawingUtil.drawCircle(
                gravityObject.x(),
                gravityObject.y(),
                gravityObject.radius(),
                0.6,
                0.7,
                this.graphicsContext
            );
        });

        switch (this.creationMode) {

            case GRAVITY_OBJECT:

                double radius = Math.hypot(
                    this.dragStartX - this.mouseX,
                    this.dragStartY - this.mouseY
                );

                DrawingUtil.drawCircle(
                    this.dragStartX,
                    this.dragStartY,
                    radius,
                    0.6,
                    0.7,
                    this.graphicsContext
                );

                break;

            case PARTICLE:

                // Vector
                this.graphicsContext.setStroke(Color.LIGHTBLUE);
                this.graphicsContext.strokeLine(
                    this.dragStartX,
                    this.dragStartY,
                    this.mouseX,
                    this.mouseY
                );

                // Prediction
                this.graphicsContext.setGlobalAlpha(0.4);
                this.graphicsContext.setStroke(Color.LIGHTGRAY);
                this.graphicsContext.strokePolyline(
                    this.predictionXPoints,
                    this.predictionYPoints,
                    this.predictionIterations
                );
                this.graphicsContext.setGlobalAlpha(1);

                break;

            case NONE:
            default:
                break;

        }

        // Draw particles
        this.model.getParticles().forEach(particle -> {
            this.graphicsContext.setFill(particle.getColor());
            this.graphicsContext.fillOval(particle.getX(), particle.getY(), 4, 4);
        });

        // Live objects
        this.graphicsContext.setGlobalAlpha(1);
        this.graphicsContext.setStroke(Color.GHOSTWHITE);
        this.graphicsContext.strokeText("Live objects " + this.model.getParticles().size(), 10, 10);
    }

    public Node getNode() {
        return this.root;
    }
}