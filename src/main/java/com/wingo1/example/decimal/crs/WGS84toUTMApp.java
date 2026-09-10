package com.wingo1.example.decimal.crs;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WGS84toUTMApp extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("WGS84toUTM");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("WGS84toUTM.fxml"));
		AnchorPane anchorPane = loader.load();
		Scene scene = new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		anchorPane.setCache(true);
		anchorPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		anchorPane.setOpacity(0);
		primaryStage.show();
		Timeline timeline = new Timeline();
		KeyFrame kf = new KeyFrame(Duration.ZERO, new KeyValue(anchorPane.scaleXProperty(), 5),
				new KeyValue(anchorPane.opacityProperty(), 0), new KeyValue(anchorPane.scaleYProperty(), 5));
		KeyFrame kf1 = new KeyFrame(Duration.seconds(1), new KeyValue(anchorPane.scaleXProperty(), 1),
				new KeyValue(anchorPane.opacityProperty(), 1), new KeyValue(anchorPane.scaleYProperty(), 1));
		timeline.getKeyFrames().addAll(kf, kf1);
		timeline.setOnFinished(e -> {
			anchorPane.setCacheHint(CacheHint.QUALITY);
		});
		timeline.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
