package com.wingo1.demo.javafx.spring;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wingo1.demo.javafx.spring.utils.FXMLLoaderUtils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SpringMain extends Application {

	@Override
	public void init() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("javafx/spring/spring-mybatis.xml");
		SpringContextHolder.setContext(context);
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		BorderPane pane = FXMLLoaderUtils.load(getClass().getResource("view/Root1.fxml"));
		Scene scene = new Scene(pane);
		scene.setUserData("root1");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
