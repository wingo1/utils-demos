package com.wingo1.example.sftp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class BatchSftpController implements Initializable {

	ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
	private ChannelSftp channelSftp;
	private Session session;
	private List<String> ipList = new ArrayList<>();
	private File localDirectory;
	private String remoteDirectory;
	private final static String USER = "root";
	private final static String PWD = "111111";
	private static ExecutorService threadPool = Executors.newSingleThreadExecutor();

	@FXML
	private TextField ipTextField;
	@FXML
	private TextField remoteDir;
	@FXML
	private ListView<String> localList;
	@FXML
	private ListView<String> remoteList;
	@FXML
	private TextArea output;
	private ObservableList<String> localObservableList;
	private ObservableList<String> remoteObservableList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		localObservableList = FXCollections.observableArrayList();
		localList.setItems(localObservableList);
		localList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		remoteObservableList = FXCollections.observableArrayList();
		remoteList.setItems(remoteObservableList);
		remoteList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// 重定位系统输出
		PrintStream cacheStream = new PrintStream(baoStream);// 临时输出
		System.setOut(cacheStream);
		System.setErr(cacheStream);
		Thread thread = new Thread(() -> {
			while (true) {
				String msg = baoStream.toString();
				baoStream.reset();
				Platform.runLater(() -> {
					if (!output.isFocused())
						output.appendText(msg);
				});
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});// 输出线程
		thread.setDaemon(true);
		thread.start();

	}

	@FXML
	private void openLocal() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("选择本地目录");
		if ((localDirectory = directoryChooser.showDialog(null)) == null) {
			System.out.println("未选择本地目录");
			return;
		}
		refreshLocal();
	}

	@FXML
	private void openRemote(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			parseIpText(ipTextField.getText());
			try {
				remoteDirectory = remoteDir.getText();
				refreshRemote();
			} catch (Exception e1) {
				System.err.println("刷新远程目录出错" + e1);
			}
		}

	}

	@FXML
	private void batchUploadClick() {
		parseIpText(ipTextField.getText());
		Alert alert = new Alert(AlertType.CONFIRMATION, "确定上传文件到以下主机:", ButtonType.YES, ButtonType.NO);
		TextArea textArea = new TextArea(ipList.toString());
		textArea.setWrapText(true);
		alert.getDialogPane().setExpandableContent(textArea);
		alert.getDialogPane().setExpanded(true);
		Optional<ButtonType> showAndWait = alert.showAndWait();
		if (showAndWait.get() == ButtonType.YES) {
			if (localList.getSelectionModel().getSelectedItems().size() == 0) {
				alert = new Alert(AlertType.ERROR);
				alert.setContentText("未选择本地文件");
				alert.showAndWait();
				System.out.println("未选择本地文件");
				return;
			}
			threadPool.execute(() -> {
				batchUpload();
			});
		}

	}

	@FXML
	private void download() {
		Alert alert = new Alert(AlertType.INFORMATION, "未实现", ButtonType.CANCEL);
		alert.showAndWait();
	}

	/**
	 * 批量上传文件到远端
	 */
	private void batchUpload() {
		// 批量IP
		for (String ip : ipList) {
			try {
				getChannel(ip);
				channelSftp.cd(remoteDirectory);
				// 批量文件
				List<String> selectedValuesList = localList.getSelectionModel().getSelectedItems();
				for (String select : selectedValuesList) {
					File selectFile = new File(localDirectory.getAbsolutePath() + "/" + select);
					// 如果是目录就只是创建同名目录
					if (selectFile.isDirectory()) {
						channelSftp.mkdir(select);
						System.out.println(ip + "同名目录创建完成！");
						continue;
					}
					try (InputStream inputStream = FileUtils.openInputStream(selectFile);) {
						channelSftp.put(inputStream, select);
						System.out.println(ip + "上传完成！");
					}
					// 修改权限
					channelSftp.chmod(Integer.parseInt("777", 8), select);
				}
			} catch (Exception e) {
				System.err.println(ip + "批量上传出错:" + e);
			} finally {
				logout();
			}
		}
		try {
			refreshRemote();
		} catch (Exception e) {
			System.out.println("batchUpload refreshRemote出错" + e);
		}
	}

	@FXML
	private void batchDeleteClick() {
		parseIpText(ipTextField.getText());
		Alert alert = new Alert(AlertType.CONFIRMATION, "确定删除以下主机的文件:", ButtonType.YES, ButtonType.NO);
		TextArea textArea = new TextArea(ipList.toString());
		textArea.setWrapText(true);
		alert.getDialogPane().setExpandableContent(textArea);
		alert.getDialogPane().setExpanded(true);
		if (alert.showAndWait().get() == ButtonType.YES) {
			threadPool.execute(() -> {
				batchDelete();
			});
		}
	}

	/**
	 * 批量删除远端
	 */
	private void batchDelete() {
		// 批量IP
		for (String ip : ipList) {
			try {
				getChannel(ip);
				channelSftp.cd(remoteDirectory);
				List<String> selectedValuesList = remoteList.getSelectionModel().getSelectedItems();
				if (selectedValuesList.size() == 0) {
					System.out.println("未选择远端待删除的文件");
					return;
				}
				for (String select : selectedValuesList) {
					// 目录
					if (select.endsWith("/")) {
						channelSftp.rmdir(select);
						continue;
					}
					channelSftp.rm(select);
				}
				System.out.println(ip + "删除完成！");
			} catch (Exception e) {
				System.err.println(ip + "批量删除出错:" + e);
			} finally {
				logout();
			}
		}
		try {
			refreshRemote();
		} catch (Exception e) {
			System.out.println("batchDelete refreshRemote出错" + e);
		}
	}

	private void refreshLocal() {
		localObservableList.clear();
		File[] listFiles = localDirectory.listFiles();

		for (File file : listFiles) {
			String item = file.getName();
			if (file.isDirectory()) {
				item = item + "/";
			}
			localObservableList.add(item);
		}
	}

	private void refreshRemote() throws Exception {
		if (ipList.size() == 0) {
			System.err.println("IP为空");
			return;
		}
		String ip = ipList.get(0);
		getChannel(ip);
		channelSftp.cd(remoteDirectory);
		Vector ls = channelSftp.ls(".");
		Platform.runLater(() -> {
			remoteObservableList.clear();
			for (Object object : ls) {
				LsEntry entry = (LsEntry) object;
				if (entry.getAttrs().isDir()) {
					remoteObservableList.add(entry.getFilename() + "/");
					continue;
				}
				remoteObservableList.add(entry.getFilename());
			}
			System.out.println("远端目录刷新完成！");
			logout();
		});
	}

	// 192.168.225.[1-4,6-8]
	private void parseIpText(String text) {
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.)\\[(.+)\\]");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			ipList.clear();
			String prefix = matcher.group(1);
			String suffix = matcher.group(2);
			String[] split = suffix.split(",");
			try {
				for (String segment : split) {
					String[] split2 = segment.split("-");
					if (split2.length == 1) {
						ipList.add(prefix + Integer.valueOf(split2[0]));
						continue;
					}
					for (int i = Integer.valueOf(split2[0]); i <= Integer.valueOf(split2[1]); i++) {
						ipList.add(prefix + i);
					}
				}
			} catch (Exception e) {
				System.out.println("IP段格式错误，eg:192.168.225.[1-4,6-8]");
			}
		} else {
			System.out.println("IP格式错误");
		}

	}

	private void getChannel(String ip) throws JSchException {
		JSch jsch = new JSch();
		session = jsch.getSession(USER, ip);
		session.setPassword(PWD);
		Properties configTemp = new Properties();
		configTemp.put("StrictHostKeyChecking", "no");
		// 为Session对象设置properties
		session.setConfig(configTemp);
		session.setTimeout(2000);
		session.connect();
		// 通过Session建立链接
		// 打开SFTP通道
		Channel channel = session.openChannel("sftp");
		// 建立SFTP通道的连接
		channel.connect();
		System.out.println(ip + "connect successfully!");
		channelSftp = (ChannelSftp) channel;
	}

	private void logout() {
		if (channelSftp != null) {
			channelSftp.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

}
