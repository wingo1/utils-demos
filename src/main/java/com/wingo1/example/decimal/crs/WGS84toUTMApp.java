package com.wingo1.example.decimal.crs;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
