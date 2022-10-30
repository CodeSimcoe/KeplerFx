package com.codesimcoe.physicsfx.configuration;

public class Configuration {

    // UI
    public static final double CANVAS_WIDTH = 1500;
    public static final double CANVAS_HEIGHT = 900;

//    public static final double CONFIG_WIDTH = 300;

    public static final double UI_WIDTH = 1500;
    public static final double UI_HEIGHT = 900;

    // Physics

    // Eagerly instantiated singleton
    private static final Configuration instance = new Configuration();
    public static Configuration getInstance() {
        return instance;
    }

    private Configuration() {
        //
    }
}