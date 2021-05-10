package com.wingo1.demo.javafx.spring.utils;

import java.io.IOException;
import java.net.URL;

import com.wingo1.demo.javafx.spring.SpringContextHolder;

import javafx.fxml.FXMLLoader;

public class FXMLLoaderUtils {

	public static <T> T load(URL url) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setControllerFactory(SpringContextHolder.getContext()::getBean);
		loader.setLocation(url);
		return loader.load();
	}

}
