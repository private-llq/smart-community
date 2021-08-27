package com.jsy.community.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @return
 * @Author lihao
 * @Description POI工具类
 * @Date 2021/3/4 15:06
 * @Param
 **/
@Slf4j
public class POIUtils {
	private final static String xls = "xls";
	private final static String xlsx = "xlsx";
	private final static String DATE_FORMAT = "yyyy/MM/dd";
	
	/**
	 * 读入excel文件除了第一行和第二行的所有行，解析后返回
	 *
	 * @param file
	 * @throws IOException
	 */
	public static List<String[]> readExcel(MultipartFile file) throws IOException {
		//检查文件
		checkFile(file);
		
		//获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		
		//创建返回对象，把每行中的所有值作为一个数组，所有行作为一个集合返回
		List<String[]> list = new ArrayList<String[]>();
		
		if (workbook != null) {
			// 遍历每一个sheet         workbook.getNumberOfSheets()：获取工作簿中的电子表格数量
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				log.info("当前是第" + sheetNum+"个工作簿");
				//获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if (sheet == null) {
					continue;
				}
				
				//获得当前sheet的开始行
				int firstRowNum = sheet.getFirstRowNum();
				//获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();
				log.info("当前工作簿共：" + lastRowNum + "行");
				//循环遍历除了前2行的所有行
				int realRow = 0;
				for (int rowNum = firstRowNum + 2; rowNum <= lastRowNum; rowNum++) {
					//获得当前行
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					if (isRowEmpty(row)) {
						continue;
					}
					//获得当前行的开始列
					int firstCellNum = row.getFirstCellNum();
					//获得当前行的列数【该列有值才算有效列】
					int lastCellNum = row.getPhysicalNumberOfCells();
					String[] cells = new String[row.getPhysicalNumberOfCells()];
					//循环当前行 遍历出每一列
					for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
						Cell cell = row.getCell(cellNum);
						if (cell != null && cell.getCellType() != CellType.BLANK) {
							cells[cellNum] = getCellValue(cell);
						}
					}
					realRow += 1;
					list.add(cells);
				}
				log.info("当前工作簿实际：" + realRow + "行");
			}
			workbook.close();
		}
		return list;
	}
	
	//校验文件是否合法
	public static void checkFile(MultipartFile file) throws IOException {
		//判断文件是否存在
		if (null == file) {
			throw new FileNotFoundException("文件不存在！");
		}
		//获得文件名
		String fileName = file.getOriginalFilename();
		//判断文件是否是excel文件
		if (!StringUtils.isEmpty(fileName)) {
			if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {  //endsWith(String suffix) 测试此字符串是否以指定的后缀结束。
				throw new IOException(fileName + "不是excel文件");
			}
		}
	}
	
	public static Workbook getWorkBook(MultipartFile file) {
		//获得文件名
		String fileName = file.getOriginalFilename();
		//创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			//获取excel文件的io流
			InputStream is = file.getInputStream();
			
			if (!StringUtils.isEmpty(fileName)) {
				//根据文件后缀名不同(xls和xlsx)用不同的Workbook实现类对象
				if (fileName.endsWith(xls)) {
					//2003版本
					workbook = new HSSFWorkbook(is);
				} else if (fileName.endsWith(xlsx)) {
					//2007版本
					workbook = new XSSFWorkbook(is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}
	
	public static String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		//如果当前单元格内容为日期类型，需要特殊处理
		String dataFormatString = cell.getCellStyle().getDataFormatString();
		if (dataFormatString.equals("m/d/yy")) {
			cell.getCellType();
			Date date = cell.getDateCellValue();
			if (date != null) {
				cellValue = new SimpleDateFormat(DATE_FORMAT).format(date);
				return cellValue;
			}
		}
		//如果当前单元格内容为日期类型，需要特殊处理
		/*if (cell.getCellStyle().getDataFormat() ==HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd HH:mm:ss")
				||cell.getCellStyle().getDataFormat() ==HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd hh:mm:ss") ){

			Date date = cell.getDateCellValue();
			if (date != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cellValue=format.format(date);
				return cellValue;
			}
		}*/

		/*if (dataFormatString.equals("yyyy-mm-dd hh:mm:ss") *//*|| HSSFDateUtil.isCellDateFormatted(cell)*//*) {
			cell.getCellType();
			Date date = cell.getDateCellValue();
			if (date != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cellValue=format.format(date);
				return cellValue;
			}
		}*/
		//若该单元格是数字，把数字当成String来读，避免出现1读成1.0的情况
		if (cell.getCellType() == CellType.NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)){
				Date date = cell.getDateCellValue();
				if (date != null) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					cellValue=format.format(date);
					return cellValue;
				}
			}
			double numericCellValue = cell.getNumericCellValue();
			cell.setCellType(CellType.STRING);
			cellValue = String.valueOf(cell.getStringCellValue());
		} else {
			cellValue = String.valueOf(cell.getStringCellValue());
		}
		return cellValue;
	}
	
	/**
	 * @return boolean
	 * @Author lihao
	 * @Description 判断是否为空行
	 * @Date 2021/3/7 22:49
	 * @Param [row]
	 **/
	public static boolean isRowEmpty(Row row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != CellType.BLANK)
				return false;
		}
		return true;
	}

}
