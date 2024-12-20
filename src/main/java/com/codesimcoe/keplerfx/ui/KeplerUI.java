package com.codesimcoe.keplerfx.ui;

import com.codesimcoe.keplerfx.configuration.Configuration;
import com.codesimcoe.keplerfx.domain.GravityObject;
import com.codesimcoe.keplerfx.domain.Particle;
import com.codesimcoe.keplerfx.domain.Particle.Position;
import com.codesimcoe.keplerfx.drawing.DrawingUtil;
import com.codesimcoe.keplerfx.geometry.GeometryUtil;
import com.codesimcoe.keplerfx.model.Model;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KeplerUI {

  private final Model model = Model.getInstance();
  private final ColorGenerator colorGenerator = new RollColorGenerator(17);
  private Color nextColor = this.colorGenerator.generateColor();

  private final Pane root;

  private final GraphicsContext graphicsContext;
  private final Color[] predictionColors;

  private CreationMode creationMode = CreationMode.NONE;

  private double dragStartX;
  private double dragStartY;

  private double mouseX;
  private double mouseY;

  // Trajectory prediction
  private final double[] predictionXPoints = new double[Configuration.PREDICTION_ITERATIONS];
  private final double[] predictionYPoints = new double[Configuration.PREDICTION_ITERATIONS];

  private enum CreationMode {
    NONE,
    PARTICLE,
    GRAVITY_OBJECT
  }

  public KeplerUI() {
    this.root = new Pane();

    Canvas canvas = new Canvas(Configuration.CANVAS_WIDTH, Configuration.CANVAS_HEIGHT);
    this.graphicsContext = canvas.getGraphicsContext2D();
    this.root.getChildren().add(canvas);

    this.graphicsContext.setGlobalBlendMode(BlendMode.SRC_OVER);
    this.graphicsContext.setLineWidth(Configuration.DEFAULT_LINE_WIDTH);
    this.graphicsContext.setFont(Font.font("Arial", 16));

    // Starting gravity object
    this.model.addGravityObject(new GravityObject(600, 400, 30, 30));

    this.root.setOnMousePressed(e -> {

      if (e.getButton() == MouseButton.MIDDLE) {
        // Remove gravity objects under cursor
        this.model.getGravityObjets().removeIf(gravityObject -> {
          double dx = gravityObject.x() - e.getX();
          double dy = gravityObject.y() - e.getY();
          double distance = GeometryUtil.distance(dx, dy);
          return distance < gravityObject.radius();
        });
      } else {
        this.dragStartX = e.getX();
        this.dragStartY = e.getY();
      }
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

    this.root.setOnMouseReleased(_ -> {

      switch (this.creationMode) {

        case GRAVITY_OBJECT:
          double radius = Math.hypot(
            this.dragStartX - this.mouseX,
            this.dragStartY - this.mouseY
          );

          if (radius > Configuration.MIN_GRAVITY_OBJECT_RADIUS) {
            GravityObject gravityObject = new GravityObject(
              this.dragStartX,
              this.dragStartY,
              radius,
              radius // mass
            );
            this.model.addGravityObject(gravityObject);
          }
          break;

        case PARTICLE:
          double factor = 10;
          double vx = (this.dragStartX - this.mouseX) / factor;
          double vy = (this.dragStartY - this.mouseY) / factor;

          // Generate a color
          Color color = this.pickColor();

          Particle particle = new Particle(this.mouseX, this.mouseY, vx, vy, color);
          this.model.addParticle(particle);
          break;

        case NONE:
        default:
          break;

      }

      this.creationMode = CreationMode.NONE;
    });

    this.root.setOnScroll(_ -> this.pickColor());

    // Cache prediction colors
    this.predictionColors = new Color[Configuration.PREDICTION_ITERATIONS];
    for (int i = 0; i < Configuration.PREDICTION_ITERATIONS; i++) {
      double alpha = 1.0 - (1.0 * i / Configuration.PREDICTION_ITERATIONS);
      this.predictionColors[i] = Color.hsb(0, 0, 1.0, alpha);
    }
  }

  private Color pickColor() {
    Color color = this.nextColor;
    this.nextColor = this.colorGenerator.generateColor();
    return color;
  }

  private void computeTrajectoryPrediction() {

    // Predict speed
    double factor = 10;
    double vx = (this.dragStartX - this.mouseX) / factor;
    double vy = (this.dragStartY - this.mouseY) / factor;

    // Predict trajectory
    Particle predictedParticle = new Particle(this.mouseX, this.mouseY, vx, vy, Color.GRAY);

    for (int i = 0; i < Configuration.PREDICTION_ITERATIONS; i++) {

      this.predictionXPoints[i] = predictedParticle.getX();
      this.predictionYPoints[i] = predictedParticle.getY();

      this.applyAttraction(predictedParticle);
    }
  }

  public void update() {

    Iterator<Particle> iterator = this.model.getParticles().iterator();
    while (iterator.hasNext()) {
      Particle particle = iterator.next();

      // Remove particles that are too far away
      if (particle.getX() > 2 * Configuration.CANVAS_WIDTH
        || particle.getX() < -Configuration.CANVAS_WIDTH
        || particle.getY() > 2 * Configuration.CANVAS_HEIGHT
        || particle.getY() < -Configuration.CANVAS_HEIGHT) {

        iterator.remove();
      } else {
        this.applyAttraction(particle);
      }
    }
  }

  private void applyAttraction(final Particle particle) {

    // Acceleration
    double ax = 0;
    double ay = 0;

    for (GravityObject gravityObject : this.model.getGravityObjets()) {

      double dx = particle.getX() - gravityObject.x();
      double dy = particle.getY() - gravityObject.y();

      double gravityStrength = 1_000 * gravityObject.mass();

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
        Configuration.GRAVITY_OBJECT_FILL_OPACITY,
        Configuration.GRAVITY_OBJECT_STROKE_OPACITY,
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
          Configuration.GRAVITY_OBJECT_FILL_OPACITY,
          Configuration.GRAVITY_OBJECT_STROKE_OPACITY,
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
        for (int i = 0; i < Configuration.PREDICTION_ITERATIONS - 1; i++) {
          Color color = this.predictionColors[i];
          this.graphicsContext.setStroke(color);

          // This is used to minimize segment overlapping
          double dx = this.predictionXPoints[i + 1] - this.predictionXPoints[i];
          double dy = this.predictionYPoints[i + 1] - this.predictionYPoints[i];
          double distance = GeometryUtil.distance(dx, dy);

          // 0x5f3759df in the air
          this.graphicsContext.strokeLine(
            this.predictionXPoints[i],
            this.predictionYPoints[i],
            this.predictionXPoints[i + 1] - dx / distance,
            this.predictionYPoints[i + 1] - dy / distance
          );
        }

        break;

      case NONE:
      default:
        break;
    }

    // Draw particles
    this.model.getParticles().forEach(this::drawParticle);

    // Live objects
    this.graphicsContext.setGlobalAlpha(1);
    this.graphicsContext.setFill(Color.GHOSTWHITE);
    this.graphicsContext.fillText("Live objects " + this.model.getParticles().size(), 20, 20);
    this.graphicsContext.fillText("Next color : ", 20, 40);

    // Next color
    this.graphicsContext.setFill(this.nextColor);
    this.graphicsContext.fillOval(115, 30, 10, 10);
  }

  private void drawParticle(final Particle particle) {

    // Check if particle is within bounds (not drawing if not)
    boolean outOfBounds = particle.getX() < 0
      || particle.getX() > Configuration.CANVAS_WIDTH
      || particle.getY() < 0
      || particle.getY() > Configuration.CANVAS_HEIGHT;

    if (!outOfBounds) {
      // History
      this.graphicsContext.setLineWidth(Configuration.THICK_LINE_WIDTH);
      List<Position> positions = particle.getPositions();
      int size = positions.size();

      this.graphicsContext.setEffect(Configuration.PARTICLE_EFFECT);
      this.graphicsContext.setStroke(particle.getColor());
      Position last = positions.getFirst();
      this.graphicsContext.strokeLine(
        particle.getX(),
        particle.getY(),
        last.x(),
        last.y()
      );

      for (int i = 0; i < size - 1; i++) {

        Position p = positions.get(i);
        Position p1 = positions.get(i + 1);

        Color color = particle.getHistoryColor(i);
        this.graphicsContext.setStroke(color);

        this.graphicsContext.strokeLine(
          p.x(),
          p.y(),
          p1.x(),
          p1.y()
        );
      }
      this.graphicsContext.setLineWidth(Configuration.DEFAULT_LINE_WIDTH);
      this.graphicsContext.setEffect(null);
    }
  }

  public Node getNode() {
    return this.root;
  }
}