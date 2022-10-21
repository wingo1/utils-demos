package com.wingo1.example.poi;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Excel第3列部分单元格加前缀“第二卷”
 * 
 * @author cdatc-wingo1
 *
 */
public class ExcelAddprefix extends Application {
	private static Logger logger = LoggerFactory.getLogger(ExcelAddprefix.class);

	public static void main(String[] args) {
		Application.launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Excel File");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File excel = fileChooser.showOpenDialog(null);
		readExcel(excel);
		System.exit(0);

	}

	private void readExcel(File file) throws Exception {
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheetAt(0);
		for (Row row : sheet) {
			Cell cell = row.getCell(2);
			logger.info("########当前row：{}", row.getRowNum());
			if (cell == null || cell.getCellType() != CellType.STRING) {
				logger.info("----无需前缀,非string");
				continue;
			}
			String stringCellValue = cell.getStringCellValue();
			// 需要加
			if (StringUtils.isNotEmpty(stringCellValue) && Character.isDigit(stringCellValue.trim().charAt(0))) {
				logger.info("++++需要添加前缀‘第二卷 ’{}", stringCellValue);
				String newStr = "第二卷" + stringCellValue;
				cell.setCellValue(newStr);
			} else {
				logger.info("----无需前缀{}", stringCellValue);
			}
		}
		Alert alert = new Alert(AlertType.INFORMATION, "处理完毕");
		alert.showAndWait();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Excel File");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File saveFile = fileChooser.showSaveDialog(null);
		wb.write(new FileOutputStream(saveFile));
		alert = new Alert(AlertType.INFORMATION, "保存完毕");
		alert.showAndWait();
	}

}
