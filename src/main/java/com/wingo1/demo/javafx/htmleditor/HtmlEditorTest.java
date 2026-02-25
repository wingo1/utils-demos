package com.wingo1.demo.javafx.htmleditor;

import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLElement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * htmlEditor自定义测试
 */
public class HtmlEditorTest extends Application {
	boolean editable = true;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final HTMLEditor htmlEditor = new HTMLEditor();
		htmlEditor.setPrefHeight(245);
		Node toolbarNode = htmlEditor.lookup(".top-toolbar");
		WebView webview = (WebView) htmlEditor.lookup("WebView");
		GridPane.setHgrow(webview, Priority.ALWAYS);
		GridPane.setVgrow(webview, Priority.ALWAYS);
		if (toolbarNode instanceof ToolBar) {
			ToolBar topToolBar = (ToolBar) toolbarNode;

			// 2. 创建一个自定义按钮
			Button myButton = new Button("编辑/只读");

			// 3. 定义按钮的功能：
			myButton.setOnAction(e -> {
				WebView webView = (WebView) htmlEditor.lookup(".web-view");
				Document document = webView.getEngine().getDocument();
				HTMLElement htmlDocumentElement = (HTMLElement) document.getDocumentElement();
				HTMLElement htmlBodyElement = (HTMLElement) htmlDocumentElement.getElementsByTagName("body").item(0);
				editable = !editable;
				htmlBodyElement.setAttribute("contenteditable", Boolean.toString(editable));
				htmlEditor.lookup(".bottom-toolbar").setVisible(editable);
			});

			// 4. 将按钮添加到工具栏
			topToolBar.getItems().add(myButton);
		}

		Scene scene = new Scene(htmlEditor);
		primaryStage.setScene(scene);
		primaryStage.show();

		Platform.runLater(() -> {

			Set<Node> lookupAll = htmlEditor.lookupAll(".font-menu-button");
			Node fontNode = lookupAll.stream().filter(n -> {
				ComboBox box = (ComboBox) n;
				if (box.getTooltip().getText().startsWith("字体")) {
					return true;
				}
				return false;
			}).findAny().orElse(null);
			ComboBox fontCombo = (ComboBox) fontNode;
			fontCombo.setItems(
					FXCollections.observableArrayList("Arial", "Times New Roman", "Courier New", "华文仿宋", "SimSun"));
			fontCombo.getSelectionModel().select(0);

		});

	}

}
