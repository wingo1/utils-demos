package com.wingo1.demo.xml.dom4j.replace;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileSystemView;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BatchXMLReplaceController implements Initializable {
	@FXML
	private TextField filePath;

	private File xmlFile;

	@FXML
	private TextField xpathTextField;

	@FXML
	private TextField originTextField;

	@FXML
	private TextField newTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// TODO Auto-generated method stub

	}

	@FXML
	private void openXML() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择XML文件");
		fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		File showOpenDialog = fileChooser.showOpenDialog(null);
		if (showOpenDialog == null) {
			new Alert(AlertType.WARNING, "未选择文件", ButtonType.CLOSE).showAndWait();
			return;
		}
		xmlFile = showOpenDialog;
		filePath.setText(xmlFile.getAbsolutePath());
	}

	@FXML
	private void TestXpath() throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(xmlFile);
		List<Node> selectNodes = doc.selectNodes(xpathTextField.getText());
		int count = 0;
		StringBuilder sbBuilder = new StringBuilder();
		for (Node node : selectNodes) {
			String stringValue = node.getStringValue();
			// 正则
			String replaceAll = stringValue.replaceAll(originTextField.getText(), newTextField.getText());
			String oldXml = node.asXML();
			node.setText(replaceAll);
			count++;
			sbBuilder.append("\n第" + count + "个," + "原节点：" + oldXml + ",替换后节点：" + node.asXML());
		}
		Alert alert = new Alert(AlertType.INFORMATION, "总共找到" + selectNodes.size() + "个节点,下面列出替换测试结果:");
		TextArea textArea = new TextArea(sbBuilder.toString());
		textArea.setWrapText(true);
		alert.getDialogPane().setExpandableContent(textArea);
		alert.getDialogPane().setExpanded(true);
		alert.showAndWait();

	}

	@FXML
	private void replace() throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(xmlFile);
		List<Node> selectNodes = doc.selectNodes(xpathTextField.getText());
		for (Node node : selectNodes) {
			String stringValue = node.getStringValue();
			// 正则
			String replaceAll = stringValue.replaceAll(originTextField.getText(), newTextField.getText());
			node.setText(replaceAll);
		}
		// write
		FileChooser fileChooser1 = new FileChooser();
		fileChooser1.setTitle("Save");
		fileChooser1.setInitialDirectory(xmlFile.getParentFile());
		fileChooser1.setInitialFileName(xmlFile.getName());
		File file = fileChooser1.showSaveDialog(null);
		if (file != null) {
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
			writer.write(doc);
		}
	}
}
