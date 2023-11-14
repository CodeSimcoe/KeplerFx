package com.codesimcoe.spacefx.ui;

import com.codesimcoe.spacefx.configuration.Configuration;
import com.codesimcoe.spacefx.model.Model;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class ConfigurationUI {

  private final VBox root;

  public ConfigurationUI() {

    Model model = Model.getInstance();

//		Label liveParticlesLabel = new Label("Live Particles: ");
//		Label liveParticlesValueLabel = new Label();
//		liveParticlesValueLabel.textProperty().bind(null);
//		HBox liveParticlesBox = new HBox(5, liveParticlesLabel, liveParticlesValueLabel);

//		Label particlesLifeLabel = new Label("Particles Life");
//		Slider particlesLifeSlider = this.newSlider(0, 500, 0, 50, configuration.getParticleLife());
//
//		Label generatedParticlesLabel = new Label("Generated Particles");
//		Slider particlesAmountSlider = this.newSlider(1, 50, 0, 10, configuration.getParticleGenerationAmount());
//
//		Label particlesGenerationPeriodLabel = new Label("Particles Generation Period (ms)");
//		Slider particlesGenerationPeriodSlider = this.newSlider(1, 200, 0, 50, configuration.getParticleGenerationPeriodMs());
//
//		Label gravityStrengthLabel = new Label("Gravity Strength");
//		Slider gravityStrengthSlider = this.newSlider(0, 10_000, 0, 500, configuration.getGravityStrength());
//
//		Label slowStrengthLabel = new Label("Fluidity");
//		Slider slowStrengthSlider = this.newSlider(0.9, 1, 0, 500, configuration.getSlowStrength());
//
//		Label effectsLabel = new Label("Effects");
//		CheckBox gaussianBlurCheckbox = new CheckBox("Blur");
//		gaussianBlurCheckbox.selectedProperty().bindBidirectional(configuration.getGaussianBlurEffect());
//		HBox effectsBox = new HBox(5, gaussianBlurCheckbox);
//
//		Label clearLabel = new Label("Clear");
//		Button clearGravityObjectsButton = new Button("Remove Gravity Objects");
//		clearGravityObjectsButton.setOnAction(e -> {
//			model.clearGravityObjects();
//		});
//		Button clearParticlesButton = new Button("Remove Particles");
//		clearParticlesButton.setOnAction(e -> {
//			model.clearParticles();
//		});
//		HBox clearBox = new HBox(5, clearGravityObjectsButton, clearParticlesButton);

    this.root = new VBox(
//			5,
//			liveParticlesBox,
//			particlesLifeLabel,
//			particlesLifeSlider,
//			generatedParticlesLabel,
//			particlesAmountSlider,
//			particlesGenerationPeriodLabel,
//			particlesGenerationPeriodSlider,
//			gravityStrengthLabel,
//			gravityStrengthSlider,
//			slowStrengthLabel,
//			slowStrengthSlider,
//			effectsLabel,
//			effectsBox,
//			clearLabel,
//			clearBox
    );
//		this.root.setPrefWidth(Configuration.CONFIG_WIDTH);
  }

  private Slider newSlider(
    final double min,
    final double max,
    final int minorTickCount,
    final int majorTickUnit,
    final Property<Number> property) {

    Slider slider = new Slider();

    slider.setMin(min);
    slider.setMax(max);

    slider.setMinorTickCount(minorTickCount);
    slider.setMajorTickUnit(majorTickUnit);

    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);

    slider.valueProperty().bindBidirectional(property);

    return slider;
  }

  public Node getNode() {
    return this.root;
  }
}