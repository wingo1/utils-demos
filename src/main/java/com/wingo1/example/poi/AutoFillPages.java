package com.wingo1.example.poi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 根据偏离表里的章节去WORD转的PDF里找页码并填回偏离表页码列
 * 
 * @author cdatc-wingo1
 *
 */
public class AutoFillPages extends Application {
	private static Logger logger = LoggerFactory.getLogger(ExcelAddprefix.class);
	int handleCount = 0;
	int notHandleCount = 0;

	public static void main(String[] args) {
		Application.launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("打开偏离表");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File excel = fileChooser.showOpenDialog(null);
		fileChooser.setInitialDirectory(excel.getParentFile());
		fileChooser.setTitle("打开第二卷");
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("pdf", "*.pdf"));
		File secondFile = fileChooser.showOpenDialog(null);

		fileChooser.setTitle("打开第三卷");
		File thirdFile = fileChooser.showOpenDialog(null);
		process(excel, secondFile, thirdFile);
		System.exit(0);
	}

	private void process(File excel, File secondPdf, File thirdPdf) throws Exception {
		Map<Integer, String> preparePdf2 = preparePdf(secondPdf);
		Map<Integer, String> preparePdf3 = preparePdf(thirdPdf);
		Workbook wb = WorkbookFactory.create(excel);
		Sheet sheet = wb.getSheetAt(0);
		for (Row row : sheet) {
			Cell cell = row.getCell(2);
			logger.debug("########当前row：{}", row.getRowNum());
			if (isMerge(sheet, cell.getRowIndex(), 2)) {
				logger.info("----合并单元格");
				continue;
			}
			if (cell == null || cell.getCellType() != CellType.STRING) {
				logger.debug("----无需前缀,非string");
				notFound(wb, row);
				continue;
			}
			String stringCellValue = cell.getStringCellValue();
			int page = 0;
			String prefix = "";
			if (stringCellValue.trim().startsWith("第二卷")) {
				page = findInPDF(preparePdf2, stringCellValue);
				prefix = "第二卷：";
			} else if (stringCellValue.trim().startsWith("第三卷")) {
				page = findInPDF(preparePdf3, stringCellValue);
				prefix = "第三卷：";
			} else {
				if (stringCellValue.trim().startsWith("这是") || stringCellValue.trim().startsWith("建议系统")) {

				} else {
					notFound(wb, row);
				}

				continue;
			}
			if (page > 0) {
				Cell cell5 = row.getCell(5);
				cell5.setCellValue(prefix + page + "页");
				handleCount++;
			} else {
				notFound(wb, row);
			}
		}
		Alert alert = new Alert(AlertType.INFORMATION, "处理完毕,已处理：" + handleCount + ",未处理:" + notHandleCount);
		alert.showAndWait();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("保存excel");
		fileChooser.setInitialDirectory(new File("."));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("excel2007", "*.xlsx"));
		File saveFile = fileChooser.showSaveDialog(null);
		wb.write(new FileOutputStream(saveFile));
		alert = new Alert(AlertType.INFORMATION, "保存完毕");
		alert.showAndWait();

	}

	private void notFound(Workbook wb, Row row) {
//		Cell cell = row.getCell(5);
//		if (cell.getCellType() == CellType.STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
//			return;
//		}
		notHandleCount++;
		Cell cell5 = row.getCell(5);
		CellStyle createCellStyle = wb.createCellStyle();
		createCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		createCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell5.setCellValue("NOTFOUND");
		cell5.setCellStyle(createCellStyle);
	}

	/**
	 * 是否合并单元格
	 * 
	 * @param sheet
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean isMerge(Sheet sheet, int row, int col) {
		for (CellRangeAddress cellRangeAddress : sheet.getMergedRegions()) {
			int firstColumn = cellRangeAddress.getFirstColumn();
			int lastColumn = cellRangeAddress.getLastColumn();
			int firstRow = cellRangeAddress.getFirstRow();
			int lastRow = cellRangeAddress.getLastRow();
			// 偏离表里没有横向合并的
			if (row > firstRow && row <= lastRow) {// 第一个不考虑
				return true;
			}
		}
		return false;
	}

	/**
	 * 准备PDF
	 * 
	 * @param pdfFile
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, String> preparePdf(File pdfFile) throws Exception {
		PDDocument doc = PDDocument.load(pdfFile);
		Map<Integer, String> map = new HashMap<>();
		int pages = doc.getNumberOfPages();
		for (int i = 1; i < pages + 1; i++) {
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			pdfTextStripper.setStartPage(i);
			pdfTextStripper.setEndPage(i);
			String text = pdfTextStripper.getText(doc);
			try (BufferedReader read = new BufferedReader(new StringReader(text))) {
				for (int j = 0; j < 5; j++) {// 顶多5行出页码
					String readLine = read.readLine();
					if (readLine == null) {
						break;
					}
					if (readLine.trim().matches("\\d+")) {
						map.put(Integer.parseInt(readLine.trim()), text);
						break;
					}
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * @param preparedPDF
	 * @param stringCellValue
	 * @return 页码 ，错误-1 /-2
	 */
	private Integer findInPDF(Map<Integer, String> preparedPDF, String stringCellValue) throws Exception {
		String line = stringCellValue.split("\n")[0];
		Pattern pattern = Pattern.compile("\\d(\\.\\d+)*");
		Matcher matcher = pattern.matcher(line);
		int page = -1;
		if (matcher.find()) {
			String titleNum = matcher.group();
			for (Entry<Integer, String> entry : preparedPDF.entrySet()) {
				try (BufferedReader read = new BufferedReader(new StringReader(entry.getValue()))) {
					String tmpStr = "";
					while ((tmpStr = read.readLine()) != null) {
						if (tmpStr.startsWith(titleNum)) {
							page = entry.getKey();
							break;
						}
					}
					if (page > 0) {
						break;
					}
				}

				/*
				 * if (entry.getValue().contains(titleNum)) {// 不担心1.2 匹配1.2.1，因为从上到下的顺序 page =
				 * entry.getKey(); break; }
				 */
			}
			// 再用正文精确点
			if (stringCellValue.split("\n").length > 1) {
				String line2 = stringCellValue.split("\n")[1];
				int start = line2.length() < 6 ? 1 : 6;
				int end = line2.length() > 30 ? 30 : line2.length();
				String line2sub = stringCellValue.split("\n")[1].substring(start, end);
				if (StringUtils.isNotEmpty(line2sub) && (end - start > 20)) {
					for (Entry<Integer, String> entry : preparedPDF.entrySet()) {
						if (entry.getValue().contains(line2sub)) {
							logger.info("正文匹配:" + line2sub);
							page = entry.getKey();
							break;
						}
					}
				}
			}
			return page;
		}
		return -2;
	}

}
