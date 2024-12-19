package com.codesimcoe.keplerfx.ui;

import javafx.scene.paint.Color;

public class RandomColorgenerator implements ColorGenerator {
  @Override
  public Color generateColor() {
    return Color.hsb(
      Math.random() * 360,
      1,
      1
    );
  }
}
