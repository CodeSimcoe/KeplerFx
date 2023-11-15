package com.codesimcoe.spacefx.configuration;

import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;

public final class Configuration {

  // Frame rate and game speed
  public static final double FPS = 120;
  public static final double GAME_SPEED = 0.25;

  // UI
  public static final double CANVAS_WIDTH = 1_500;
  public static final double CANVAS_HEIGHT = 900;

  public static final double UI_WIDTH = 1500;
  public static final double UI_HEIGHT = 900;

  // Data
  public static final int PREDICTION_ITERATIONS = 1000;
  public static final double MIN_GRAVITY_OBJECT_RADIUS = 5;

  // Particle rendering
  public static final double GRAVITY_OBJECT_FILL_OPACITY = 0.35;
  public static final double GRAVITY_OBJECT_STROKE_OPACITY = 0.75;
  public static final int PARTICLE_POSITION_HISTORY_COUNT = 20;
  public static final Effect PARTICLE_EFFECT = new GaussianBlur(3);

  public static final double DEFAULT_LINE_WIDTH = 1.6;
  public static final double THICK_LINE_WIDTH = 8;

  private Configuration() {
    // Non-instantiable
  }
}