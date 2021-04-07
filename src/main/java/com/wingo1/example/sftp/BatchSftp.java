package com.wingo1.example.sftp;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class BatchSftp {
	private JFrame frame;
	ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
	private ChannelSftp channelSftp;
	private Session session;
	private List<String> ipList = new ArrayList<>();
	private File localDirectory;
	private String remoteDirectory;
	private JList<String> localJlist;
	private JList<String> remoteJlist;

	private static ExecutorService threadPool = Executors.newSingleThreadExecutor();

	private final static String USER = "root";
	private final static String PWD = "111111";

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					BatchSftp window = new BatchSftp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public BatchSftp() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("批量SFTP工具");
		frame.setBounds(500, 200, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		// IP
		JLabel jLabel = new JLabel("IP段:");
		jLabel.setBounds(5, 5, 40, 20);
		frame.getContentPane().add(jLabel);
		JTextField ipText = new JTextField();
		ipText.setBounds(50, 5, 420, 20);
		ipText.setText("输入IP段，格式：192.168.225.[1-4,6-8]");
		frame.getContentPane().add(ipText);
		// 2个列表
		DefaultListModel<String> localListModel = new DefaultListModel<>();
		localJlist = new JList<String>(localListModel);
		JScrollPane localJScrollPane = new JScrollPane(localJlist);
		localJScrollPane.setBounds(5, 70, 200, 320);
		frame.getContentPane().add(localJScrollPane);
		DefaultListModel<String> remoteListModel = new DefaultListModel<>();
		remoteJlist = new JList<String>(remoteListModel);
		JScrollPane remoteJScrollPane = new JScrollPane(remoteJlist);
		remoteJScrollPane.setBounds(250, 70, 220, 320);
		frame.getContentPane().add(remoteJScrollPane);
		// 按钮些
		JButton openLocalDir = new JButton("选择本地目录");
		openLocalDir.setBounds(5, 30, 120, 30);
		frame.getContentPane().add(openLocalDir);
		openLocalDir.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// 选择文件夹
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					System.out.println("未选择本地目录");
					return;
				}
				localDirectory = chooser.getSelectedFile();
				refreshLocal();
			}
		});

		JTextField remoteDir = new JTextField("输入远程目录，按回车生效");
		remoteDir.setBounds(250, 30, 220, 30);
		remoteDir.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					parseIpText(ipText.getText());
					try {
						remoteDirectory = remoteDir.getText();
						refreshRemote();
					} catch (Exception e1) {
						System.err.println("刷新远程目录出错" + e1);
					}
				}
			}

		});
		frame.getContentPane().add(remoteDir);

		JButton upload = new JButton("批量上传");
		upload.setBounds(5, 400, 100, 30);
		frame.getContentPane().add(upload);
		upload.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				threadPool.execute(() -> {
					parseIpText(ipText.getText());
					int result = JOptionPane.showConfirmDialog(null, "确定上传文件到以下主机" + toMultiline(ipList), "上传确认",
							JOptionPane.YES_NO_OPTION);
					if (result == -1) {// 点了右上角的X
						return;
					}
					if (result == JOptionPane.YES_OPTION) {
						batchUpload();
					}
				});
			}

		});

		JButton download = new JButton("下载");
		download.setBounds(250, 400, 80, 30);
		frame.getContentPane().add(download);
		download.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "待实现...");
			}

		});
		JButton delete = new JButton("批量删除远端文件");
		delete.setBounds(330, 400, 140, 30);
		frame.getContentPane().add(delete);
		delete.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				threadPool.execute(() -> {
					parseIpText(ipText.getText());
					int result = JOptionPane.showConfirmDialog(null, "确定删除以下主机的文件:" + toMultiline(ipList), "删除确认",
							JOptionPane.YES_NO_OPTION);
					if (result == -1) {// 点了右上角的X
						return;
					}
					if (result == JOptionPane.YES_OPTION) {
						batchDelete();
					}
				});
			}

		});

		// 执行情况
		JTextArea jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setWrapStyleWord(true);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBounds(10, 450, 460, 100);
		jScrollPane.setBorder(BorderFactory.createTitledBorder("执行情况")); // 标题边框
		frame.getContentPane().add(jScrollPane);
		// 重定位系统输出
		PrintStream cacheStream = new PrintStream(baoStream);// 临时输出
		System.setOut(cacheStream);
		System.setErr(cacheStream);
		new Thread(() -> {
			while (true) {
				String msg = baoStream.toString();
				baoStream.reset();
				EventQueue.invokeLater(() -> {
					jTextArea.append(msg);
				});
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}).start();// 输出线程

	}

	/**
	 * 批量上传文件到远端
	 */
	private void batchUpload() {
		if (localJlist.getSelectedValuesList().size() == 0) {
			System.out.println("未选择本地文件");
			return;
		}
		// 批量IP
		for (String ip : ipList) {
			try {
				getChannel(ip);
				channelSftp.cd(remoteDirectory);
				// 批量文件
				List<String> selectedValuesList = localJlist.getSelectedValuesList();
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

	/**
	 * 批量删除远端
	 */
	private void batchDelete() {
		// 批量IP
		for (String ip : ipList) {
			try {
				getChannel(ip);
				channelSftp.cd(remoteDirectory);
				List<String> selectedValuesList = remoteJlist.getSelectedValuesList();
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
		DefaultListModel<String> localListModel = (DefaultListModel) localJlist.getModel();
		localListModel.clear();
		File[] listFiles = localDirectory.listFiles();

		for (File file : listFiles) {
			String item = file.getName();
			if (file.isDirectory()) {
				item = item + "/";
			}
			localListModel.addElement(item);
		}
	}

	private void refreshRemote() throws Exception {
		if (ipList.size() == 0) {
			System.err.println("IP为空");
			return;
		}
		DefaultListModel<String> remoteListModel = (DefaultListModel) remoteJlist.getModel();
		remoteListModel.clear();
		String ip = ipList.get(0);
		getChannel(ip);
		channelSftp.cd(remoteDirectory);
		Vector ls = channelSftp.ls(".");
		for (Object object : ls) {
			LsEntry entry = (LsEntry) object;
			if (entry.getAttrs().isDir()) {
				remoteListModel.addElement(entry.getFilename() + "/");
				continue;
			}
			remoteListModel.addElement(entry.getFilename());
		}
		System.out.println("远端目录刷新完成！");
		logout();
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

	private String toMultiline(List<String> ips) {
		StringBuilder sBuilder = new StringBuilder();
		int i = 1;
		for (String string : ips) {
			sBuilder.append(string).append(",");
			if (i % 10 == 0) {
				sBuilder.append("\n");
			}
			i++;
		}
		return sBuilder.toString();
	}
}
