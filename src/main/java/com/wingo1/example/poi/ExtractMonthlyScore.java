package com.wingo1.example.poi;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 提取一年每人的分数
 */
public class ExtractMonthlyScore {
	private static final String dir = "D:\\Documents\\工作\\空管科技\\软件研发部绩效考核\\2023新绩效考核";

	private static final String FILENAME_PATTERN = "\\d+(\\d{2})-(.+)\\.xlsx";
	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("00.00");
	private static Workbook finalWorkbook;

	public static void main(String[] args) throws Exception {

		File dirFile = new File(dir);
		// 打开汇总表
		finalWorkbook = WorkbookFactory.create(new File(dir + File.separator + "管制产品部月度考核记录汇总表.xlsx"));// 根目录下

		for (File file : dirFile.listFiles()) {
			if (file.isDirectory()) {// 进入到每月
				processMonth(file);
			} else {
				continue;
			}

		}
		finalWorkbook.write(new FileOutputStream(dir + File.separator + "new_管制产品部月度考核记录汇总表.xlsx"));
		// finalWorkbook.close();
		System.out.println("ALL DONE");
	}

	/**
	 * 每月文件夹
	 * 
	 * @param monthDir
	 */
	private static void processMonth(File monthDir) {
		System.out.println("startProcess " + monthDir.getName());
		File[] listFiles = monthDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(FILENAME_PATTERN);
			}
		});
		for (File file : listFiles) {
			try {
				processSingleOne(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("finish Process " + monthDir.getName());
	}

	private static void processSingleOne(File xlsxFile) throws Exception {
		String month = null;
		String name = null;
		Pattern compile = Pattern.compile(FILENAME_PATTERN);
		Matcher matcher = compile.matcher(xlsxFile.getName());
		if (matcher.find()) {
			month = matcher.group(1);
			name = matcher.group(2);
		}
		Workbook wb = WorkbookFactory.create(xlsxFile);
		Sheet sheetAt = wb.getSheetAt(0);// 第一个就是汇总工作表
		for (Row row : sheetAt) {
			if (row.getCell(1) == null) {
				continue;
			}
			if (row.getCell(1).getStringCellValue().startsWith("最终得分")) {
				// 就是这行了！
				Cell cell = row.getCell(12);
				double numericCellValue = cell.getNumericCellValue();
				String score = SCORE_FORMAT.format(numericCellValue);
				fillSummary(Integer.valueOf(month), name.trim(), numericCellValue);
			} else {
				continue;
			}

		}
		wb.close();
	}

	/**
	 * 填总表
	 * 
	 * @param month
	 * @param name
	 * @param score
	 */
	private static void fillSummary(int month, String name, double score) {
		int startRow = 2;
		Sheet sheetAt = finalWorkbook.getSheetAt(0);
		boolean found = false;
		for (Row row : sheetAt) {
			if (row.getCell(1) == null) {
				continue;
			}
			if (row.getCell(1).getStringCellValue().equals(name)) {// 这行了
				found = true;
				Cell cell = row.getCell(startRow + month);
				if (cell == null) {
					cell = row.createCell(startRow + month);
				}
				cell.setCellValue(score);
			}
		}
		if (!found) {// 新增记录
			sheetAt.shiftRows(1, sheetAt.getLastRowNum(), 1);
			Row createRow = sheetAt.createRow(1);
			Cell createCell = createRow.createCell(startRow + month);
			createCell.setCellValue(score);
			createRow.createCell(1).setCellValue(name);
		}
	}

}
