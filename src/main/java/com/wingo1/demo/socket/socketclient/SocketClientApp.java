package com.wingo1.demo.socket.socketclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SocketClientApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("SocketClient");
		AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("SocketClient.fxml"));
		Scene scene = new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
