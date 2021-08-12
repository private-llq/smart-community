package com.jsy.community.utils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class POIUtil {

    /*读取文件*/

    public static List<String[]> ReadMessage(MultipartFile file) throws IOException {
        List<String[]> list = new ArrayList<>();
        String[] strings = null;
        //判断文件是否存在，是否是excel文件
        POIUtil.checkFile(file);
        //根据文件后缀名不同(xls和xlsx)用不同的Workbook实现类对象
        Workbook workbook = POIUtil.getWorkBook(file);
        InputStream inputStream = file.getInputStream();//获取文件流
        if (workbook != null) {//work对象不为空

            /*// 遍历每一个sheet         workbook.getNumberOfSheets()：获取工作簿中的电子表格数量
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }*/

            //2、得到表
            Sheet sheet = workbook.getSheetAt(0);//得到第一个工作表
            //int rowCount = sheet.getPhysicalNumberOfRows();//获取行数
            //获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            for (int i =firstRowNum+1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    int CellNum = row.getPhysicalNumberOfCells();//一行中的列数
                    strings = new String[CellNum];
                    for (int j = 0; j < CellNum; j++) {//循环
                        Cell cell = row.getCell(j);//取出单元格
                        String cellValue = null;
                        if (cell != null) {
                            CellType cellType = cell.getCellType();//获取类型
                            switch (cellType) {
                                case STRING:
                                    System.out.print("【STRING】");
                                    cellValue = cell.getStringCellValue();
                                    break;
                                case BOOLEAN:
                                    System.out.print("【BOOLEAN】");
                                    cellValue = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case BLANK://空
                                    System.out.print("【空】");
                                    break;
                                case NUMERIC://数字(日期、普通数字)
                                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 日期
                                        System.out.print("【日期】");
                                        cellValue = DateToStr(cell.getDateCellValue());
                                    } else {
                                        //不是日期格式，防止数字过长！
                                        System.out.print("【数字】");

                                        cell.setCellType(CellType.STRING);

                                        cellValue = cell.getStringCellValue();
                                    }
                                    break;
                                case ERROR:
                                    System.out.print("【输入错误】");
                                    break;
                                default:
                                    System.out.print("【default】");
                                    cellValue = cell.toString();
                                    break;
                            }
                        }
                        strings[j] = cellValue;
                        System.out.println(cellValue);

                    }
                }
                list.add(strings);
            }
            inputStream.close();
        }
        return list;

    }

    // 日期转换成字符串
    public static String DateToStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }


    /*判断文件是否存在，是否是excel文件*/
    public static void checkFile(MultipartFile file) throws IOException {
        //判断文件是否存在
        if(ObjectUtils.isEmpty(file)){
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if (!StringUtils.isEmpty(fileName)) {
            if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {  //endsWith(String suffix) 测试此字符串是否以指定的后缀结束。
                throw new IOException(fileName + "不是excel文件");
            }
        }
    }

    /*根据文件后缀名不同(xls和xlsx)用不同的Workbook实现类对象*/
    public static Workbook getWorkBook(MultipartFile file) throws IOException {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        //获取excel文件的io流
        InputStream is = file.getInputStream();
        if (!StringUtils.isEmpty(fileName)) {
            //根据文件后缀名不同(xls和xlsx)用不同的Workbook实现类对象
            if (fileName.endsWith("xls")) {
                //2003版本
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                //2007版本
                workbook = new XSSFWorkbook(is);
            }
        }
        return workbook;
    }


    /*excel写出*/
    public static void writePoi( List<String[]> list, String filename, Integer type, HttpServletResponse response) throws IOException {
        Workbook workbook = null;
        String formFileName = filename;
        String types = ".xls";
        switch (type) {
            case 1:
                types = ".xls";
                workbook = new HSSFWorkbook();
                break;
            case 2:
                types = ".xlsx";
                workbook = new XSSFWorkbook();
                break;
            default:
                workbook = new HSSFWorkbook(); //创建一个薄
                break;
        }

        Sheet sheet = workbook.createSheet(filename);   //创建表

        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < list.get(i).length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(list.get(i)[j]);
            }
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(formFileName + types, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


}
