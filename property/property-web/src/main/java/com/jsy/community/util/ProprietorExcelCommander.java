package com.jsy.community.util;

import com.jsy.community.constant.ConstError;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.excel.impl.ProprietorInfoProvider;
import com.jsy.community.util.excel.impl.ProprietorMemberProvider;
import com.jsy.community.vo.property.ProprietorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * excel中转站
 * @author YuLF
 * @since 2020-11-26 15:54
 * 业主 excel 下载 导入 指定使用具体某个子类的方法  ，这里是控制器类和excel具体实现类的中枢
 * 业主 excel 指挥者[提供零件]  负责提供 excel 所需要的各个组件方法  和具体组装好的成品对象
 */
@Slf4j
public class ProprietorExcelCommander {

    /**
     * PROPRIETOR_TITLE_FIELD 其他的列宽
     */
    private static final int PROPRIETOR_OTHER_WIDTH = 256 * 19;

    /**
     * PROPRIETOR_TITLE_FIELD 回显 excel 错误信息 备注列 宽度
     */
    private static final int PROPRIETOR_REMARK_WIDTH = 256 * 80;

    /**
     * 错误信息Excel 在 Minio 中存在的bucket名称
     */
    public static final String BUCKET_NAME = "proprietor-excel";

    /**
     * 业主信息录入表.xlsx 字段 如果增加字段  需要改变实现类逻辑
     */
    public static final String[] PROPRIETOR_TITLE_FIELD = {"姓名", "身份证号", "联系电话", "房屋编号", "微信", "QQ", "电子邮箱","备注"};

    /**
     * 业主Sheet名称
     */
    public static final String PROPRIETOR_SHEET_NAME = "通讯录";

    /**
     * 业主excel标题名称
     */
    public static final String PROPRIETOR_TITLE_NAME = "业主信息";

    /**
     * 业主家属信息录入表.xlsx 字段 如果增加字段  需要改变实现类逻辑
     */
    public static final String[] MEMBER_TITLE_FIELD = {"所属业主", "与业主关系", "家属性别", "所属房屋", "家属姓名", "家属身份证号", "家属手机号"};

    /**
     * 用户导入文件时 可用后缀
     */
    public static final String[] SUPPORT_EXCEL_EXTENSION = {"xls", "xlsx"};

    /**
     * 业主家属信息模板下载 需要增加约束的 总行数量
     * 如果为1000 表明业主家属信息excel 最多同时为1000名家属录入信息
     * 当然也可以更多 只是超过1000行的单元格 没有和社区 直接关联的数据 直接选择
     */
    public static final Integer MEMBER_CONSTRAINT_ROW = 1000;


    /**
     * 【业主信息录入excel 模板 下载方法】 控制方法
     * [扩展]避免直接改动excel导出功能实现方法,
     * 这里如果需要改变使用另一种下载excel模板的方式， 新建类实现JSYExcelAbstract 的exportProprietorExcel方法 然后在这里替换new ProprietorExcelProvider()
     * @return 返回生成好的excel工作簿 好让控制器直接转换为数据流响应给客户端 下载
     * @author YuLF
     * @since 2020/11/26 16:00
     */
    public static Workbook exportProprietorInfo() {
        String[] fields = Arrays.copyOf(ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD, ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1);
        return new ProprietorInfoProvider().exportProprietorExcel( fields );
    }


    public static Workbook exportProprietorDefaultInfo(){
        return new ProprietorInfoProvider().exportProprietorExcel( PROPRIETOR_TITLE_FIELD );
    }

    /**
     * 【业主信息导入】解析导入的业主信息 excel文件 返回 信息实体列表 控制方法
     *
     * @return 返回解析好的数据
     * @author YuLF
     * @Param proprietorExcel      excel文件
     * @Param errorVos             解析错误信息集合
     * @since 2020/11/26 16:55
     */
    public static List<ProprietorEntity> importProprietorExcel(MultipartFile proprietorExcel, List<ProprietorVO> errorVos) {
        return new ProprietorInfoProvider().importProprietorExcel(proprietorExcel, errorVos);
    }

    /**
     * 【业主家属信息导入】解析导入的业主信息 excel文件 返回 信息实体列表 控制方法
     *
     * @return 返回解析好的数据
     * @author YuLF
     * @Param proprietorExcel      excel文件
     * @since 2020/11/26 16:55
     */
    public static List<UserEntity> importMemberExcel(MultipartFile proprietorExcel, Map<String, Object> map) {
        return new ProprietorMemberProvider().importProprietorExcel(proprietorExcel, map);
    }

    
    /**
     * 【业主家属成员excel模板导出】下载业主家庭成员Excel控制方法
     * @author YuLF
     * @since  2020/12/7 14:57
     * @Param  userEntityList       社区名称、社区住户名、社区住户uid信息
     * @Param  解析数据需要携带的数据
     * @return 返回创建好的Workbook
     */
    public static Workbook exportProprietorMember(List<UserEntity> userEntityList, Map<String, Object> res) {
        //调用导出excel 创建模板
        return new ProprietorMemberProvider().exportProprietorExcel(userEntityList,res);
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
            sheet.setColumnWidth(i, PROPRIETOR_OTHER_WIDTH );
            //如果匹配到 备注 这个字段列 最后一列 备注字段列 宽度调宽
            if( i == ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1 ){
                sheet.setColumnWidth(i, PROPRIETOR_REMARK_WIDTH );
            }
            XSSFCell cell1 = row2.createCell(i);
            cell1.setCellValue(fieldData[i]);
            cell1.setCellStyle(cellStyle);
        }
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
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "excel文件信息无效!请重新下载模板");
        }
        for (int i = 0; i < field.length; i++) {
            Cell cell = row.getCell(i);
            //如果标题 字段 列为空 或者 标题列字段 和 titleField 里面对应的下标 列内容不匹配 则抛出异常
            if (cell == null || !row.getCell(i).getStringCellValue().equals(field[i])) {
                throw new JSYException(ConstError.NORMAL, "字段匹配错误：预期第" + (i + 1) + "列字段是" + field[i] + "，但发现的是：" + row.getCell(i).getStringCellValue());
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
                        f.setMaximumFractionDigits(0);
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
            log.error("com.jsy.community.util.ProprietorExcelCommander.getAllUidAndNameForList:{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(),"数据类型转换失败!请重试");
        }
        return resMap;
    }

    public static boolean isEmpty(String str){
        return str == null || "".equals(str.trim()) || "null".equals(str) || "undefined".equals(str);
    }

}
