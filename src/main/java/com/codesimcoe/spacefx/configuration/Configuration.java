package com.codesimcoe.spacefx.configuration;

public final class Configuration {

  // Frame rate and game speed
  public static final double FPS = 40;

  // UI
  public static final double CANVAS_WIDTH = 1500;
  public static final double CANVAS_HEIGHT = 900;

  public static final double UI_WIDTH = 1500;
  public static final double UI_HEIGHT = 900;

  // Data
  public static final int PREDICTION_ITERATIONS = 250;
  public static final double MIN_GRAVITY_OBJECT_RADIUS = 5;

  // Particle rendering
  public static final double PARTICLE_RADIUS = 6;
  public static final double GRAVITY_OBJECT_FILL_OPACITY = 0.35;
  public static final double GRAVITY_OBJECT_STROKE_OPACITY = 0.75;
  public static final int PARTICLE_POSITION_HISTORY_COUNT = 20;
  public static final double PARTICLE_OUT_OF_BOUNDS_ARROW_LENGTH = 10;

  public static final double DEFAULT_LINE_WIDTH = 1.6;
  public static final double THICK_LINE_WIDTH = 4;

  private Configuration() {
    // Non-instantiable
  }
}