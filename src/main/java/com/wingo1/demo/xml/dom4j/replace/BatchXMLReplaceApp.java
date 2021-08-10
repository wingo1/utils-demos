package com.wingo1.demo.xml.dom4j.replace;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class BatchXMLReplaceApp extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("BATCH_XML_REPLACE");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("BatchXMLReplace.fxml"));
		AnchorPane anchorPane = loader.load();
		Scene scene = new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
