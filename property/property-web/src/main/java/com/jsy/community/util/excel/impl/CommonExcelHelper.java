package com.jsy.community.util.excel.impl;

import com.jsy.community.utils.ExcelUtil;
import lombok.Cleanup;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        ExcelUtil.createExcelTitle(workbook, sheet, excelTitle, 530, "宋体", 20, titleField.size());
        //3. 创建 列 字段
        ExcelUtil.createExcelField(workbook, sheet, titleField.toArray(new String[titleField.size()]));
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
        MultiValueMap<String, String> multiValueMap = ExcelUtil.setHeader(excelTitle);
        return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook) , multiValueMap, HttpStatus.OK );
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

    private static Field[] getAllField(Object o){
        return o.getClass().getDeclaredFields();
    }
}
