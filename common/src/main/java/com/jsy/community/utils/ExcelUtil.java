package com.jsy.community.utils;

import com.jsy.community.exception.JSYException;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @Author: Pipi
 * maven
 *  * 		<!-- poi excel 2007 -->
 *  * 		<dependency>
 *  * 			<groupId>org.apache.poi</groupId>
 *  * 			<artifactId>poi-ooxml</artifactId>
 *  * 			<version>4.1.2</version>
 *  * 		</dependency>
 * @Description: Excel表格工具类
 * @Date: 2021/5/24 11:48
 * @Version: 1.0
 **/
public class ExcelUtil {

    /**
     * TITLE_FIELD 其他的列宽
     */
    private static final int OTHER_WIDTH = 256 * 19;

    /**
     * PROPRIETOR_TITLE_FIELD 回显 excel 错误信息 备注列 宽度
     */
    private static final int REMARK_WIDTH = 256 * 80;

    /**
     * 用户导入文件时 可用后缀
     */
    public static final String[] SUPPORT_EXCEL_EXTENSION = {"xls", "xlsx"};

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
        createExcelTitle(workbook, sheet, excelTitle, 530, "宋体", 20, titleField.size());
        //3. 创建 列 字段
        createExcelField(workbook, sheet, titleField.toArray(new String[titleField.size()]));
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

    private static Field[] getAllField(Object o){
        return o.getClass().getDeclaredFields();
    }

    /**
     * 【创建excel标题头-第一行】
     * @param workbook          工作簿
     * @param sheet             工作表
     * @param excelTitle        excel标题名称
     * @param titleHeight       excel标题高度
     * @param font              excel标题字体
     * @param fontSize          excel标题字体大小
     * @param mergeCellLength   excel合并单元格数量
     */
    public static void createExcelTitle(Workbook workbook, XSSFSheet sheet, String excelTitle, int titleHeight, String font, int fontSize, int mergeCellLength){
        //创建表头行
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) (titleHeight));
        //创建表头列
        XSSFCell cell = row.createCell(0);
        //设置表头名称
        cell.setCellValue(excelTitle);
        //创建一个单元格样式
        XSSFCellStyle workBookCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        //创建单元格字体
        Font workBookFont = workbook.createFont();
        //设置粗体
        workBookFont.setBold(true);
        //设置表头字体样式
        workBookFont.setFontHeightInPoints((short) fontSize);
        workBookFont.setFontName(font);
        workBookCellStyle.setFont(workBookFont);
        //设置居中显示
        workBookCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(workBookCellStyle);
        //合并单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, mergeCellLength - 1);
        sheet.addMergedRegion(region);
    }

    /**
     * 【创建excel标题头-第一行】
     * @Description: 附加给表头增加超链接
     * @param workbook          工作簿
     * @param sheet             工作表
     * @param excelTitle        excel标题名称
     * @param titleHeight       excel标题高度
     * @param font              excel标题字体
     * @param fontSize          excel标题字体大小
     * @param mergeCellLength   excel合并单元格数量
     */
    public static void createLinkExcelTitle(Workbook workbook, XSSFSheet sheet, String excelTitle, int titleHeight, String font, int fontSize, int mergeCellLength, String relationTitleName){
        //创建表头行
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) (titleHeight));
        //创建表头列
        XSSFCell cell = row.createCell(0);
        //设置表头名称
        cell.setCellValue(excelTitle);
        cell.setCellFormula("HYPERLINK(\"#" + relationTitleName + "!A1\",\"" + relationTitleName + "\")");
        //创建一个单元格样式
        XSSFCellStyle workBookCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        //创建单元格字体
        Font workBookFont = workbook.createFont();
        //设置粗体
        workBookFont.setBold(true);
        //设置表头字体样式
        workBookFont.setFontHeightInPoints((short) fontSize);
        workBookFont.setFontName(font);
        workBookCellStyle.setFont(workBookFont);
        // 创建颜色对象,设置颜色
        byte[] rgb = new byte[]{(byte) 0, (byte) 0, (byte) 255};
        DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();
        XSSFColor xssfColor = new XSSFColor(rgb, new DefaultIndexedColorMap());
        // 设置颜色序号
        xssfColor.setIndexed(4);
        workBookFont.setColor(xssfColor.getIndexed());
        //设置居中显示
        workBookCellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(workBookCellStyle);
        //合并单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, mergeCellLength - 1);
        sheet.addMergedRegion(region);
    }

    /**
     * 【创建excel列字段头】
     * @param workbook      工作簿
     * @param sheet         工作表
     * @param fieldData     列字段数据
     */
    public static void createExcelField(Workbook workbook, XSSFSheet sheet, String[] fieldData){
        //创建 工作表 字段标题 第二行
        XSSFRow row2 = sheet.createRow(1);
        //获取字体样式
        XSSFCellStyle cellStyle = provideBold(workbook);
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        row2.setHeight((short)380);
        //创建字段标题头
        for (int i = 0; i < fieldData.length; i++) {
            //设置列宽 普通列
            sheet.setColumnWidth(i, OTHER_WIDTH);
            //如果匹配到 备注 这个字段列 最后一列 备注字段列 宽度调宽
            if( i == fieldData.length - 1 ){
                sheet.setColumnWidth(i, REMARK_WIDTH);
            }
            XSSFCell cell1 = row2.createCell(i);
            cell1.setCellValue(fieldData[i]);
            cell1.setCellStyle(cellStyle);
        }
    }

    /**
     * 【提供粗体字体样式】
     * @param workbook      工作簿
     * @return              返回设置好的样式
     */
    public static XSSFCellStyle provideBold(Workbook workbook){
        XSSFCellStyle fieldCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        //设置粗体
        Font fieldFont = workbook.createFont();
        fieldFont.setBold(true);
        //设置字体大小
        fieldFont.setFontHeightInPoints((short)14);
        //设置字体样式
        fieldFont.setFontName("宋体");
        //设置字体高度
        fieldFont.setFontHeight((short)200);
        fieldCellStyle.setFont(fieldFont);
        return fieldCellStyle;
    }

    /**
     * 【设置单元格为文本格式】将指定行数 指定的列下标字段 单元格格式全部设置为 文本格式 避免一些excel的自动转换
     * @param workbook          工作簿
     * @param setCellField      需要设置的列下标数组
     */
    public static void setCellFormatToString(Workbook workbook, String[] setCellField, String sheetName, int endRow){
        DataFormat format = workbook.createDataFormat();
        CellStyle fieldCellStyle = workbook.createCellStyle();
        fieldCellStyle.setDataFormat(format.getFormat("@"));
        Sheet sheet = workbook.getSheet(sheetName);
        int row;
        int cellStart = 2;
        //设置单元格为文本格式 避免某些转换 前两行为 标题行 和列字段行 所以从2开始
        for(row = cellStart; row <= endRow; row++){
            for(String cellIndex : setCellField){
                sheet.createRow(row).createCell(Integer.parseInt(cellIndex)).setCellStyle(fieldCellStyle);
            }
        }
    }

    /**
     * 【提供红色背景样式】
     * @param workbook  工作薄
     * @return          返回样式对象
     */
    public static XSSFCellStyle provideBackground(Workbook workbook){
        XSSFCellStyle fieldCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        fieldCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        fieldCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return fieldCellStyle;
    }

    /**
     * 【设置获取列单元格验证提示框】设置 单元格的输入限制，只能从特定的数据选择
     * @author YuLF
     * @since  2020/11/26 11:40
     * @Param  workbook                 工作薄
     * @Param  endRow                   验证结束行
     * @Param  sheet                    工作表
     * @return 返回绑定好的数据验证器  用于单元格绑定这个验证器
     */
    public static DataValidation setBox(XSSFSheet sheet, int endRow, int constraintColIndex) {
        //下标0的行和下标1的行 为 标题和字段 所以从2开始
        CellRangeAddressList addressList = new CellRangeAddressList(2, endRow, constraintColIndex, constraintColIndex);
        //创建一个 XSSFSheet约束对象
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        //绑定约束  加载叫做“名称管理器关联的数据”这个sheet的数据    与隐藏表和真正显示的表关联
        DataValidationConstraint hiddenSheetField = dvHelper.createFormulaListConstraint("thisHiddenName" + constraintColIndex);
        DataValidation validation = dvHelper.createValidation(hiddenSheetField, addressList);

        //显示下拉框约束数据选择框
        validation.setSuppressDropDownArrow(true);
        //设置错误信息框显示
        validation.setShowErrorBox(true);
        //约束列不允许空值
        validation.setEmptyCellAllowed(false);
        //错误样式为警告
        validation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
        validation.createPromptBox("提示", "请从下拉列表中选择数据");
        validation.setShowPromptBox(true);
        return validation;
    }

    /**
     * 【创建隐藏表的下拉框约束数据单元格】
     *  创建名称管理器、录入表模板 与 隐藏表之间的关联关系 ，避免 下拉框数据一次性过多导致工作表下拉框不显示问题
     * @param workbook              当前工作薄
     * @param sheet                 隐藏的工作表
     * @param constraintData        约束的数据
     * @param createCellStartRow    创建列的开始行，为真正数据的结束行开始 避免和真正的工作表之间数据重复
     * @param constraintColIndex    约束字段列下标
     */
    public static void createProprietorConstraintRef(Workbook workbook, XSSFSheet sheet, String[] constraintData, int createCellStartRow, int constraintColIndex){
        //2.循环给隐藏的域列赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后）
        for(int i = 0; i < constraintData.length; i++){
            XSSFRow row = sheet.getRow(createCellStartRow + i );
            if(row != null){
                row.createCell(constraintColIndex).setCellValue(constraintData[i]);
            } else {
                sheet.createRow(createCellStartRow + i ).createCell(constraintColIndex).setCellValue(constraintData[i]);
            }
        }
        //创建名称管理器关联这些数据
        createNameManagerRef(workbook, constraintColIndex, createCellStartRow, constraintData.length);
    }

    /**
     * 【创建名称管理器】 正常Sheet和隐藏Sheet之间的关联器
     * @param workbook              工作簿
     * @param constraintColIndex    约束列索引
     * @param createCellStartRow    设置列值开始行
     * @param createCellEndRow      设置列值结束行
     */
    public static void createNameManagerRef(Workbook workbook,  int constraintColIndex, int createCellStartRow, int createCellEndRow){
        //把所有数据添加到隐藏的Sheet 列中 然后以引用列的方式显示下拉框数组 避免直接绑定数据验证 数据量过多不显示
        //创建一个名称管理器 方便真正录入业主信息的表引用
        Name categoryName = workbook.createName();
        //坑1：名称管理器的名称不能设置为中文 否则引用不到
        categoryName.setNameName("thisHiddenName" + constraintColIndex);
        //使用当前字段头下标 获取excel头部的英语字符 用来组成以下的引用隐藏表的公式   ColumnEnglishChar 最后的值为 26个英语字母的其中一个
        char columnEnglishChar = (char) ((int) 'A' + constraintColIndex);
        //设置引用公式
        int constraintBeginRow = createCellStartRow + 1;
        //经过变量替代后  例子: hiddenSheet!$A$12:$A$54    表示引用约束  hiddenSheet表的 A12行 到 A54行   数据长度-1  数组是从0开始
        String constraintFormula = "hiddenSheet!$" + columnEnglishChar +"$"+ constraintBeginRow + ":$" + columnEnglishChar +"$"+ (constraintBeginRow + createCellEndRow - 1);
        categoryName.setRefersToFormula(constraintFormula);
    }

    /**
     * 【验证excel字段列和知道的 field 是否一致】验证 excel 第一行 字段列是否有误
     * @param sheet         工作表
     * @Param field         字段列数组
     */
    public static void validExcelField(Sheet sheet, String[] field){
        //效验Sheet
        //效验工作表头字段
        Row row = sheet.getRow(1);
        if (row == null) {
            throw new JSYException(400, "excel文件信息无效!请重新下载模板");
        }
        for (int i = 0; i < field.length; i++) {
            Cell cell = row.getCell(i);
            //如果标题 字段 列为空 或者 标题列字段 和 titleField 里面对应的下标 列内容不匹配 则抛出异常
            if (cell == null || !row.getCell(i).getStringCellValue().equals(field[i])) {
                throw new JSYException(1, "字段匹配错误：预期第" + (i + 1) + "列字段是" + field[i] + "，但发现的是：" + row.getCell(i).getStringCellValue());
            }
        }
    }

    /**
     * 【验证数据行第一列不为空】主要是用来判断用户 第一个单元格是否有值  如果有值 则读取这一行，没有值就不读取这行了，
     * @param dataRow       当前行
     * @return              返回当前行第一列的值是否为空
     */
    public static boolean cellOneIsNotNull(Row dataRow) {
        if( dataRow == null ){
            return false;
        }
        Cell cell = dataRow.getCell(0);
        if(cell == null){
            return false;
        }
        return !"".equals(cell.getStringCellValue());
    }

    /**
     *  根据 excel单元格的类型获取值
     * @author YuLF
     * @since  2020/11/27 9:31
     * @Param       cell   每一列单元格
     * @return      返回单元格的值
     */
    public static Object getCellValForType(Cell cell)
    {
        Object cellValue = StringUtils.EMPTY;
        if(cell != null){
            CellType cellType = cell.getCellType();
            switch (cellType){
                //数字
                case NUMERIC:
                    //如果是日期类型
                    if (DateUtil.isCellDateFormatted(cell)){
                        cellValue =   cell.getDateCellValue();
                    }else{
                        //避免poi读入手机号 自动变为 科学计数
                        NumberFormat f=new DecimalFormat("############");
                        f.setMaximumFractionDigits(999);
                        cellValue= f.format(cell.getNumericCellValue());
                    }
                    break;
                //字符串
                case STRING:
                    cellValue =   cell.getStringCellValue();
                    break;
                //Boolean
                case BOOLEAN:
                    cellValue =  cell.getBooleanCellValue();
                    break;
                //故障
                case ERROR:
                    break;
                default:
                    break;
            }
        }
        return cellValue;
    }

    /**
     * 传入List 返回 Map  key = fieldKey ， 值 = fieldVal
     * @param list     数据实体集合
     * @param fieldKey Map Key List对应的某个对象的属性字段
     * @param fieldVal Map Val List对应的某个对象的属性字段
     * @author YuLF
     * @since 2020-12-07 11:00
     */
    public static <T> Map<String, Object> getAllUidAndNameForList(Collection<T> list, String fieldKey, String fieldVal) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Map<String, Object> resMap = new HashMap<>(list.size());
        try {
            String keyFieldMethod = "get" + fieldKey.substring(0, 1).toUpperCase() + fieldKey.substring(1);
            String valFieldMethod = "get" + fieldVal.substring(0, 1).toUpperCase() + fieldVal.substring(1);
            for (T t : list) {
                Class<?> aClass = t.getClass();
                resMap.put(String.valueOf(aClass.getDeclaredMethod(keyFieldMethod).invoke(t)), String.valueOf(aClass.getDeclaredMethod(valFieldMethod).invoke(t)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JSYException(501, "数据类型转换失败!请重试");
        }
        return resMap;
    }

    /**
     * 读取工作簿 返回字节数组
     *
     * @param workbook excel工作簿
     * @return 返回读取完成的字节数组
     */
    public static byte[] readWorkbook(Workbook workbook) throws IOException {
        //2.3 把workbook转换为字节输入流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        //@Cleanup注解 会在作用域的末尾将调用is.close()方法，并使用了try/finally代码块 执行。
        @Cleanup InputStream is = new ByteArrayInputStream(bos.toByteArray());
        byte[] byt = new byte[is.available()];
        //2.4 读取字节流 响应实体返回
        int read = is.read(byt);
        return byt;
    }

    /**
     * 设置响应头信息
     * @param fileFullName  附件下载文件全名称
     * @return              返回响应头Map
     */
    public static MultiValueMap<String, String> setHeader(String fileFullName){
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        fileFullName = new String((fileFullName + ".xlsx").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        multiValueMap.set("Content-Disposition", "attachment;filename=" + fileFullName);
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        return multiValueMap;
    }

    /**
     *@Author: Pipi
     *@Description: 超链接样式
     *@Param: workbook:
     *@Return: org.apache.poi.xssf.usermodel.XSSFCellStyle
     *@Date: 2021/4/25 14:31
     **/
    public static XSSFCellStyle hyperlinkStyle(Workbook workbook) {
        XSSFCellStyle fieldCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        Font font = null;
        //创建一个字体对象
        font = workbook.createFont();
        // 创建颜色对象,设置颜色
        byte[] rgb = new byte[]{(byte) 0, (byte) 0, (byte) 255};
        DefaultIndexedColorMap colorMap = new DefaultIndexedColorMap();
        XSSFColor xssfColor = new XSSFColor(rgb, new DefaultIndexedColorMap());
        // 设置颜色序号
        xssfColor.setIndexed(4);
        // 给字体设置颜色
        font.setColor(xssfColor.getIndexed());
        // 设置下划线
        font.setUnderline(Font.U_SINGLE);
        fieldCellStyle.setFont(font);
        return fieldCellStyle;
    }

    /**
     * 设置有效性
     * @param offset 主影响单元格所在列，即此单元格由哪个单元格影响联动
     * @param sheet
     * @param rowNum 行数
     * @param colNum 列数
     */
    public static void setDataValidation(String offset,XSSFSheet sheet, int rowNum,int colNum) {
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        DataValidation data_validation_list;
        data_validation_list = getDataValidationByFormula(
                "INDIRECT($" + offset + (rowNum) + ")", rowNum, colNum,dvHelper);
        sheet.addValidationData(data_validation_list);
    }

    /**
     * 加载下拉列表内容
     * @param formulaString
     * @param naturalRowIndex
     * @param naturalColumnIndex
     * @param dvHelper
     * @return
     */
    private static  DataValidation getDataValidationByFormula(
            String formulaString, int naturalRowIndex, int naturalColumnIndex,XSSFDataValidationHelper dvHelper) {
        // 加载下拉列表内容
        // 举例：若formulaString = "INDIRECT($A$2)" 表示规则数据会从名称管理器中获取key与单元格 A2 值相同的数据，
        //如果A2是江苏省，那么此处就是江苏省下的市信息。
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        int firstRow = naturalRowIndex -1;
        int lastRow = naturalRowIndex - 1;
        int firstCol = naturalColumnIndex - 1;
        int lastCol = naturalColumnIndex - 1;
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                lastRow, firstCol, lastCol);
        // 数据有效性对象
        // 绑定
        XSSFDataValidation data_validation_list = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
        data_validation_list.setEmptyCellAllowed(false);
        if (data_validation_list instanceof XSSFDataValidation) {
            data_validation_list.setSuppressDropDownArrow(true);
            data_validation_list.setShowErrorBox(true);
        } else {
            data_validation_list.setSuppressDropDownArrow(false);
        }
        // 设置输入信息提示信息
        data_validation_list.createPromptBox("下拉选择提示", "请使用下拉方式选择合适的值！");
        return data_validation_list;
    }

    /**
     *  计算formula
     * @param offset 偏移量，如果给0，表示从A列开始，1，就是从B列
     * @param rowId 第几行
     * @param colCount 一共多少列
     * @return 如果给入参 1,1,10. 表示从B1-K1。最终返回 $B$1:$K$1
     *
     */
    public static String getRange(int offset, int rowId, int colCount) {
        char start = (char)('A' + offset);
        if (colCount <= 25) {
            char end = (char)(start + colCount - 1);
            return "$" + start + "$" + rowId + ":$" + end + "$" + rowId;
        } else {
            char endPrefix = 'A';
            char endSuffix = 'A';
            if ((colCount - 25) / 26 == 0 || colCount == 51) {// 26-51之间，包括边界（仅两次字母表计算）
                if ((colCount - 25) % 26 == 0) {// 边界值
                    endSuffix = (char)('A' + 25);
                } else {
                    endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
                }
            } else {// 51以上
                if ((colCount - 25) % 26 == 0) {
                    endSuffix = (char)('A' + 25);
                    endPrefix = (char)(endPrefix + (colCount - 25) / 26 - 1);
                } else {
                    endSuffix = (char)('A' + (colCount - 25) % 26 - 1);
                    endPrefix = (char)(endPrefix + (colCount - 25) / 26);
                }
            }
            return "$" + start + "$" + rowId + ":$" + endPrefix + endSuffix + "$" + rowId;
        }
    }

    /**
     * 把指定的Obj对象转换为对应的List<String>对象
     * @param obj           List<String>的Object对象
     *
     */
    public static   List<String> castStrList(Object obj)
    {
        List<String> result = new ArrayList<>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {

                result.add((String) o);
            }
            return result;
        }
        return null;
    }
}
