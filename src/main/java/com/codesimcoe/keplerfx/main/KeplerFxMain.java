package com.codesimcoe.keplerfx.main;

import com.codesimcoe.keplerfx.configuration.Configuration;
import com.codesimcoe.keplerfx.ui.KeplerUI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class KeplerFxMain extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {

    KeplerUI ui = new KeplerUI();

    BorderPane root = new BorderPane();
    root.setCenter(ui.getNode());

    Scene scene = new Scene(root, Configuration.UI_WIDTH, Configuration.UI_HEIGHT);
    primaryStage.setScene(scene);
    primaryStage.show();

    EventHandler<ActionEvent> update = _ -> {
      ui.update();
      ui.draw();
    };

    //
    double fps = Configuration.FPS;
    double frameDurationMs = 1_000 / fps;

    Duration duration = Duration.millis(frameDurationMs);
    Animation loop = new Timeline(new KeyFrame(duration, update));
    loop.setCycleCount(Animation.INDEFINITE);
    loop.play();

    primaryStage.setOnCloseRequest(_ -> {
      loop.stop();
      System.exit(0);
    });
  }
}