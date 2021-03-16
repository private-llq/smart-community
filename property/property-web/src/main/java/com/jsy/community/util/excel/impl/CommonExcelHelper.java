package com.jsy.community.util.excel.impl;

import lombok.Cleanup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * maven
 * 		<!-- poi excel 2007 -->
 * 		<dependency>
 * 			<groupId>org.apache.poi</groupId>
 * 			<artifactId>poi-ooxml</artifactId>
 * 			<version>4.1.2</version>
 * 		</dependency>
 * 公共excel导出
 * @author YuLF
 * @since 2021-02-26 10:52
 */
public class CommonExcelHelper {

    /**
     * 导出业务数据集合为excel文档
     * @Param   objects         业务数据集合
     * @Param   fieldHeader     业务数据对象 字段 对应的 中文名称，excel将用这些中文名称作为字段列
     * @Param   excelTitle      excel标题栏文字
     * @return  返回响应实体，Controller直接返回该类型，为响应下载类型
     */
    public static ResponseEntity<byte[]> export(@NonNull List<?> objects, @NonNull Map<String, Object> fieldHeader, @NonNull String excelTitle) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        //1.创建 sheet
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(excelTitle);

        // 设置第5列的列宽为20个字符宽度
        //sheet.setColumnWidth(4, 20*256);

        //取得Map列中队列的所有字段中文值 作为 excel 中文字段
        List<Object> titleField= new ArrayList<>(fieldHeader.values());
        //2. 创建 标题 头
        createExcelTitle(workbook, sheet, excelTitle, titleField.size());
        //3. 创建 列 字段
        createExcelField(workbook, sheet, titleField);
        int dataRow = 2;
        //4. 创建 数据 列
        for (Object o : objects) {
            XSSFRow row = sheet.createRow(dataRow);
            Field[] allField = getAllField(o);
            for( int cell = 0; cell < titleField.size(); cell++ ){
                XSSFCell fieldCell = row.createCell(cell);
                String fieldKey = getKeyForVal(allField, titleField.get(cell), fieldHeader);
                if( Objects.isNull(fieldKey) ){
                    throw new IllegalArgumentException("fieldHeader 存在对象不存在的字段!");
                }
                fieldCell.setCellValue(getObjectField(fieldKey, o));
            }
            dataRow++;
        }
        MultiValueMap<String, String> multiValueMap = setHeader(excelTitle);
        return new ResponseEntity<>(readWorkbook(workbook) , multiValueMap, HttpStatus.OK );
    }

    /**
     * 调用对象的 Get 方法 取出值
     * @Param field     字段名称
     * @Param o         具体的对象
     * @author YuLF
     * @since  2021/2/26 11:41
     */
    private static String getObjectField(String field, Object o)  {
        Method getFieldMethod;
        Object invoke = null;
        try {
            getFieldMethod = o.getClass().getDeclaredMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
            invoke = getFieldMethod.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoke == null ? "" : String.valueOf(invoke);
    }

    /**
     * Map 通过 值找到Key 前置条件 excel 标题 值不唯一
     * @author YuLF
     * @since  2021/2/26 11:31
     * @Param  objField  对象的所有列字段
     * @Param  fieldVal  某个列字段的值
     */
    private static String getKeyForVal(Field[] objField, Object fieldVal, Map<String, Object> header){
        for (Field field : objField) {
            Object o = header.get(field.getName());
            if( Objects.isNull(o) ){
                continue;
            }
            if( o.equals(fieldVal) ){
                return field.getName();
            }
        }
        return null;
    }

    /**
     * 设置响应头信息
     * @param fileFullName  附件下载文件全名称
     * @return              返回响应头Map
     */
    private static MultiValueMap<String, String> setHeader(String fileFullName){
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        fileFullName = new String((fileFullName + ".xlsx").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        multiValueMap.set("Content-Disposition", "attachment;filename=" + fileFullName);
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        return multiValueMap;
    }

    /**
     * 读取工作簿 返回字节数组
     * @param workbook      excel工作簿
     * @return              返回读取完成的字节数组
     */
    private static byte[] readWorkbook(Workbook workbook) throws IOException {
        @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        @Cleanup InputStream is = new ByteArrayInputStream(bos.toByteArray());
        byte[] byt = new byte[is.available()];
        int read = is.read(byt);
        return byt;
    }

    private static Field[] getAllField(Object o){
        return o.getClass().getDeclaredFields();
    }


    /**
     * 【创建excel列字段头】
     * @param workbook      工作簿
     * @param sheet         工作表
     * @param fieldData     列字段数据
     */
    private static void createExcelField(Workbook workbook, XSSFSheet sheet, List<Object> fieldData){
        XSSFRow row2 = sheet.createRow(1);
        CellStyle cellStyle = provideBold(workbook);
        AtomicInteger i = new AtomicInteger();
        fieldData.forEach( field -> {
            XSSFCell cell1 = row2.createCell(i.get());
            cell1.setCellValue(fieldData.get(i.get()).toString());
            cell1.setCellStyle(cellStyle);
            i.getAndIncrement();
        });
    }

    /**
     * 【提供粗体字体样式】
     * @param workbook      工作簿
     * @return              返回设置好的样式
     */
    private static CellStyle provideBold(Workbook workbook){
        CellStyle fieldCellStyle = workbook.createCellStyle();
        Font fieldFont = workbook.createFont();
        fieldFont.setBold(true);
        fieldCellStyle.setFont(fieldFont);
        return fieldCellStyle;
    }

    /**
     * 【创建excel标题头-第一行】
     * @param workbook          工作簿
     * @param sheet             工作表
     * @param excelTitle        excel标题名称
     * @param mergeCellLength   excel合并单元格数量
     */
    private static void createExcelTitle(Workbook workbook, XSSFSheet sheet, String excelTitle, int mergeCellLength){
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) (530));
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(excelTitle);
        XSSFCellStyle workBookCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        Font workBookFont = workbook.createFont();
        workBookFont.setFontHeightInPoints((short) 20);
        workBookFont.setFontName("宋体");
        workBookCellStyle.setFont(workBookFont);
        workBookCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(workBookCellStyle);
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, mergeCellLength - 1);
        sheet.addMergedRegion(region);
    }


}
