package com.codesimcoe.keplerfx.ui;

import javafx.scene.paint.Color;

public class RollColorGenerator implements ColorGenerator {

  private final double offset;
  private double hue = 0;

  public RollColorGenerator(final double offset) {
    this.offset = offset;
  }

  @Override
  public Color generateColor() {
    Color color = Color.hsb(
      this.hue,
      1,
      1
    );

    this.hue += this.offset;

    return color;
  }
}
