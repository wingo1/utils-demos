package com.wingo1.example.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * excel里有所有写入的dataType，需要从另一data_type.h文件中获取其中文意思
 * 
 * @author cdatc-wingo1
 *
 */
public class DataTypeMeaning extends Application {

	@Override
	public void start(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("过期配置excel");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File excel = fileChooser.showOpenDialog(null);
		fileChooser.setInitialDirectory(excel.getParentFile());
		fileChooser.setTitle("data_type.h");
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".h", "*.h"));
		File dataTypeH = fileChooser.showOpenDialog(null);
		try {
			process(excel, dataTypeH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);

	}

	public static void main(String[] args) {
		launch(args);
	}

	private void process(File excel, File dataTypeH) throws Exception {
		Map<String, String> MeaningMap = processHFile(dataTypeH);
		Workbook wb = WorkbookFactory.create(excel);
		for (Sheet sheet : wb) {
			sheet.shiftColumns(1, sheet.getRow(0).getLastCellNum(), 2);
			for (Row row : sheet) {
				if (row == sheet.getRow(0)) {// 第一行
					row.createCell(1).setCellValue("code");
					row.createCell(2).setCellValue("meaning");
					continue;
				}
				Cell cell = row.getCell(0);
				String stringCellValue = String.valueOf((int) cell.getNumericCellValue()); // 应该是data_type数值
				String meaningStr = MeaningMap.get(stringCellValue.trim());
				if (meaningStr == null) {
					continue;
				}
				row.createCell(1).setCellValue(meaningStr.split("@")[0]);
				row.createCell(2).setCellValue(meaningStr.split("@")[1]);

			}
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("保存excel");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File saveFile = fileChooser.showSaveDialog(null);
		wb.write(new FileOutputStream(saveFile));
		Alert alert = new Alert(AlertType.INFORMATION, "保存完毕");
		alert.showAndWait();
	}

	/**
	 * 
	 * @param dataTypeH
	 * @return 类型和它含义
	 * @throws FileNotFoundException
	 */
	private Map<String, String> processHFile(File dataTypeH) throws Exception {
		Map<String, String> map = new HashMap<>();
		try (Scanner scanner = new Scanner(dataTypeH);) {
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				// const int MID_LOG_LOGREC =6; //ATC各功能模块发到LOGREC服务器的日志
				Pattern compile = Pattern.compile("const int\\s+(\\w+)\\s*=\\s*(\\d+);\\s*//(.+)");
				Matcher matcher = compile.matcher(nextLine);
				if (matcher.find()) {
					// code@meaning
					map.put(matcher.group(2), matcher.group(1) + "@" + matcher.group(3));
				}
			}
		}
		return map;
	}

}
