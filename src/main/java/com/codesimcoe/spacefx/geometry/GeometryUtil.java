package com.codesimcoe.spacefx.geometry;

public final class GeometryUtil {

  private GeometryUtil() {
    //
  }

  // Faster than Math.hypot
  public static double distance(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }
}
