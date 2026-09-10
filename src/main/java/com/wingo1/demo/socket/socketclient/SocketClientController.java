package com.wingo1.demo.socket.socketclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class SocketClientController implements Initializable {
	@FXML
	private ComboBox<String> workMode;
	@FXML
	private TextField ip;
	@FXML
	private TextField port;
	@FXML
	private TextArea log;
	@FXML
	private CheckBox stopRecv;

	@FXML
	private ToggleButton connectButton;

	@FXML
	private TextArea recvContent;
	@FXML
	private TextArea sendContent;

	private Socket socket;// TCP
	private BufferedReader socketReader;

	private ExecutorService recvExcutor = Executors.newSingleThreadExecutor();
	private Future<?> recvfuture;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		workMode.getItems().addAll("TCP", "UDP");
		workMode.getSelectionModel().select(0);
		connectButton.selectedProperty().addListener((o, old, ne) -> {
			if (ne == true) {
				connectButton.setText("已连接");
			} else {
				connectButton.setText("连接");
			}
		});

	}

	@FXML
	private void connect(Event event) {
		try {
			boolean isToConnect = connectButton.isSelected();
			if (workMode.getSelectionModel().getSelectedItem().equals("TCP")) {
				if (!isToConnect) {// 断开连接
					if (socket != null) {
						if (recvfuture != null) {
							recvfuture.cancel(true);
						}
						socket.close();
						log("TCP连接断开");
					}
					return;
				}
				log("TCP连接中...");
				socket = new Socket(ip.getText(), Integer.parseInt(port.getText()));
				log("TCP连接成功");
				socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				recvfuture = recvExcutor.submit(() -> {
					while (!Thread.interrupted()) {
						try {
							char[] b = new char[1024];
							Integer len = socketReader.read(b);
							String str = new String(b, 0, len);
							Platform.runLater(() -> {
								if (!stopRecv.isSelected()) {
									recvContent.appendText("\n" + str);
								}
							});
						} catch (IOException e) {
							log(e.toString());
						}
					}
				});
			} else {
				new Alert(AlertType.ERROR, "UDP暂未实现").showAndWait();
				log("UDP暂未实现");
				connectButton.setSelected(false);
			}
		} catch (Exception e) {
			log(e.getMessage());
		}
	}

	@FXML
	private void sendMsg() {
		try {
			if (workMode.getSelectionModel().getSelectedItem().equals("TCP")) {
				if (socket == null || !socket.isConnected()) {
					new Alert(AlertType.ERROR, "TCP未连接！").showAndWait();
					return;
				}
				OutputStream outputStream = socket.getOutputStream();
				String text = sendContent.getText();
				// 处理0x02形式的字符
				Pattern pattern = Pattern.compile("0x\\w\\w");
				Matcher matcher = pattern.matcher(text);
				int index = 0;
				byte[] dest = null;
				while (matcher.find()) {
					byte[] bytes = text.substring(index, matcher.start()).getBytes();
					Byte valueOf = Byte.valueOf(text.substring(matcher.start() + 2, matcher.end()), 16);
					byte[] result = new byte[bytes.length + 1];
					System.arraycopy(bytes, 0, result, 0, bytes.length);
					result[result.length - 1] = valueOf;
					if (dest == null) {
						dest = result;
					} else {
						dest = Arrays.copyOf(dest, dest.length + result.length);
						System.arraycopy(result, 0, dest, dest.length - result.length, result.length);
					}
					index = matcher.end();
				}
				byte[] last = text.substring(index).getBytes();
				if (dest == null) {
					dest = last;
				} else {
					dest = Arrays.copyOf(dest, dest.length + last.length);
					System.arraycopy(last, 0, dest, dest.length - last.length, last.length);
				}
				outputStream.write(dest);
			} else {
				new Alert(AlertType.ERROR, "UDP暂未实现").showAndWait();
				log("UDP暂未实现");
			}
		} catch (Exception e) {
			log(e.getMessage());
		}
	}

	@FXML
	private void clearRecv() {
		recvContent.clear();
	}

	@FXML
	private void clearLog() {
		log.clear();
	}

	@FXML
	private void clearSend() {
		sendContent.clear();
	}

	private void log(String content) {
		Platform.runLater(() -> {
			log.appendText("\n" + content);
		});
	}
}
