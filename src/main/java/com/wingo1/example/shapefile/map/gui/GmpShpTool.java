package com.wingo1.example.shapefile.map.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.wingo1.example.shapefile.map.FillStandnoToTaxlShp;
import com.wingo1.example.shapefile.map.ShapeFileReaderText;
import com.wingo1.example.shapefile.map.TaxlGmpToShp;
import com.wingo1.example.shapefile.map.TaxlShpToGmp;

public class GmpShpTool {
	private JFrame frame;
	ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GmpShpTool window = new GmpShpTool();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GmpShpTool() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("GMP_SHP_TOOL");
		frame.setBounds(500, 200, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		// 组件
		JPanel convertPanel = createTaxlConvertToolPanel();
		convertPanel.setBounds(10, 10, 320, 100);
		convertPanel.setBorder(BorderFactory.createTitledBorder("滑行道中线工具")); // 标题边框
		frame.getContentPane().add(convertPanel);
		JPanel textPanel = createStandNoTaxlNamePanel();
		textPanel.setBounds(330, 10, 160, 100);
		textPanel.setBorder(BorderFactory.createTitledBorder("停机位滑行道编号工具")); // 标题边框
		frame.getContentPane().add(textPanel);
		// 执行情况
		JTextArea jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setWrapStyleWord(true);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBounds(10, 130, 460, 220);
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

	private JPanel createTaxlConvertToolPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton shp2GmpButton = new JButton("SHP->GMP(不需要CRS)");
		shp2GmpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new TaxlShpToGmp().excute();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JTextField crsText = new JTextField();
		crsText.setText("32648");
		JButton gmp2ShpButton = new JButton("GMP->SHP");
		gmp2ShpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new TaxlGmpToShp().excute(crsText.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton fillStandButton = new JButton("向SHP填入机位");
		fillStandButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new FillStandnoToTaxlShp().excute(crsText.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(new JLabel("CRS code:"));
		panel.add(crsText);
		panel.add(shp2GmpButton);
		panel.add(gmp2ShpButton);
		panel.add(fillStandButton);
		return panel;
	}

	private JPanel createStandNoTaxlNamePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton textButton = new JButton("SHP->GMP");
		textButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new ShapeFileReaderText().excute();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(textButton);
		return panel;
	}


}
