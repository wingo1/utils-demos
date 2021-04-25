package com.wingo1.example.sftp;

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

public class BatchSftpApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("BATCH_SFTP");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("BatchSftp.fxml"));
		AnchorPane anchorPane = loader.load();
		Scene scene = new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResource("sftp_icon.png").toString()));

		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));

		// 一个缩放特效
//		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), anchorPane);
//		scaleTransition.setFromX(10f);
//		scaleTransition.setFromY(10f);
//		scaleTransition.setToX(1f);
//		scaleTransition.setToY(1f);
//		scaleTransition.play();
		anchorPane.setCache(true);
		anchorPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		anchorPane.setOpacity(0);
		primaryStage.show();
		Timeline timeline = new Timeline();
		KeyFrame kf = new KeyFrame(Duration.ZERO, new KeyValue(anchorPane.scaleXProperty(), 10),
				new KeyValue(anchorPane.scaleYProperty(), 10), new KeyValue(anchorPane.rotateProperty(), 180));
		KeyFrame kf1 = new KeyFrame(Duration.seconds(1), new KeyValue(anchorPane.scaleXProperty(), 0.1),
				new KeyValue(anchorPane.scaleYProperty(), 0.1), new KeyValue(anchorPane.opacityProperty(), 1));
		KeyFrame kf2 = new KeyFrame(Duration.seconds(1.8), new KeyValue(anchorPane.scaleXProperty(), 1),
				new KeyValue(anchorPane.scaleYProperty(), 1), new KeyValue(anchorPane.rotateProperty(), 360));
		timeline.getKeyFrames().addAll(kf, kf1, kf2);
		timeline.setOnFinished(e -> {
			anchorPane.setCacheHint(CacheHint.QUALITY);
		});
		timeline.play();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
