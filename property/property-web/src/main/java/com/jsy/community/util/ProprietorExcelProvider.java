package com.jsy.community.util;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.entity.HouseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import java.util.*;

/**
 * @author YuLF
 * @since 2020-11-26 09:48
 * 业主信息录入.xlsx 下载 模板提供类
 */
@Slf4j
public class ProprietorExcelProvider extends JSYExcelAbstract {



    /**
     * 行下拉框约束限制，最多到(当前社区住户房间数量.size)行
     */
    private int VALID_CONSTRAINT_ROW = 1;

    /**
     * Sheet表名称
     */
    private String SHEET_NAME = StringUtils.EMPTY;

    /**
     * 隐藏Sheet表
     */
    private XSSFSheet HIDDEN_SHEET;


    /**
     * 生成录入业主信息excel 模板 返回excel数据流
     *
     * @return 返回生成好的excel模板数据流，供控制层直接输出响应excel.xlsx文件
     * @author YuLF
     * @Param map                      key  name = 社区名   key communityUserNum = 社区房间数量
     * @Param entityList               excel列约束  数据集合 楼栋、单元、楼层、门牌
     * @since 2020/11/26 9:50
     */
    @Override
    public  Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> map) {
        //初始excel 表名称 和 表行数数据
        this.SHEET_NAME = map.get("name") + "业主信息登记表";
        //拿到 社区总房间的条数   因为第一行和第二行是excel字段和标题 所以+2
        this.VALID_CONSTRAINT_ROW = Integer.parseInt(map.get("communityUserNum").toString()) + 2;

        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(this.SHEET_NAME);
        //创建一个隐藏的工作表  来存放约束下拉框的数组数据  避免直接绑定所有数据 数据量过多 导致下拉框不显示
        this.HIDDEN_SHEET = (XSSFSheet) workbook.createSheet("hiddenSheet");
        //1.创建表头行
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) (530));
        //创建表头列
        XSSFCell cell = row.createCell(0);
        //设置表头名称
        cell.setCellValue(this.SHEET_NAME);
        //创建一个单元格样式
        XSSFCellStyle workBook_cellStyle = (XSSFCellStyle) workbook.createCellStyle();
        //创建单元格字体
        Font workBookFont = workbook.createFont();
        //设置表头字体样式
        workBookFont.setFontHeightInPoints((short) 20);
        workBookFont.setFontName("宋体");
        workBook_cellStyle.setFont(workBookFont);
        //设置居中显示
        workBook_cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(workBook_cellStyle);
        String[] titleField = ProprietorExcelCommander.TITLE_FIELD;
        //合并单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, titleField.length);
        sheet.addMergedRegion(region);
        //2.创建 工作表 字段标题 第二行
        XSSFRow row2 = sheet.createRow(1);
        CellStyle fieldCellStyle = workbook.createCellStyle();
        //设置粗体
        Font fieldFont = workbook.createFont();
        fieldFont.setBold(true);
        fieldCellStyle.setFont(fieldFont);

        //创建字段标题头
        for (int i = 0; i < titleField.length; i++) {
            XSSFCell cell1 = row2.createCell(i);
            cell1.setCellValue(titleField[i]);
            cell1.setCellStyle(fieldCellStyle);
            if (i > 0 && i <= 5) {
                //只要TITLE_FIELD 索引为 1、2、3、4、5都需要添加选择填写约束
                //性别、楼栋、单元、楼层、门牌单元格 列 选择约束添加限制
                //绑定约束验证器
                sheet.addValidationData(setBox(workbook, sheet, entityList, i));
            }

        }

        //设置某列单元格格式 统一为 文本格式 防止 把第五列的门牌号 选择后自动转换为日期格式
        setCellFormatToString(workbook, new String[]{"5"});
        return workbook;
    }

    /**
     * 将指定行数 指定的列下标字段 单元格格式全部设置为 文本格式 避免一些excel的自动转换
     * @param workbook          工作簿
     * @param setCellField      需要设置的列下标数组
     */
    private void setCellFormatToString(Workbook workbook, String[] setCellField){
        DataFormat format = workbook.createDataFormat();
        CellStyle fieldCellStyle = workbook.createCellStyle();
        fieldCellStyle.setDataFormat(format.getFormat("@"));
        Sheet sheet = workbook.getSheet(this.SHEET_NAME);
        int row;
        //设置单元格为文本格式 避免某些转换
        for(row = 2; row <= this.VALID_CONSTRAINT_ROW; row++){
            for(String cellIndex : setCellField){
                sheet.createRow(row).createCell(Integer.parseInt(cellIndex)).setCellStyle(fieldCellStyle);
            }
        }
    }

    /**
     * 设置 业主信息登记.xlsx 单元格的输入限制，只能从特定的数据选择
     * @author YuLF
     * @since  2020/11/26 11:40
     * @Param  workbook                 工作薄
     * @Param  sheet                    工作表
     * @Param  communityArchitecture    数据库的楼栋、单元、楼层、门牌号List数据
     * @return 返回绑定好的数据验证器  用于单元格绑定这个验证器
     */
    private DataValidation setBox(Workbook workbook,XSSFSheet sheet, List<?> communityArchitecture, int constraintColIndex) {
        //下标0的行和下标1的行 为 标题和字段 所以从2开始
        CellRangeAddressList addressList = new CellRangeAddressList(2, this.VALID_CONSTRAINT_ROW, constraintColIndex, constraintColIndex);
        //通过传过来的 约束列constraintColIndex 获得List中的约束数据
        String[] constraintData = getConstraintSet(communityArchitecture, constraintColIndex);

        //创建业主信息登记表与隐藏表的约束字段
        createNameManagerRef(workbook, constraintData, constraintColIndex);

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
     * 创建隐藏表与业主登记表之间数据的约束
     * @param workbook              当前工作薄
     * @param constraintData        约束数据
     * @param constraintColIndex    约束字段列下标
     */
    public void createNameManagerRef(Workbook workbook, String[] constraintData, int constraintColIndex){
        //2.循环给隐藏的域列赋值（为了防止下拉框的行数与隐藏域的行数相对应，将隐藏域加到结束行之后）
        for(int i = 0; i < constraintData.length; i++){
            XSSFRow row = HIDDEN_SHEET.getRow(this.VALID_CONSTRAINT_ROW + i );
            if(row != null){
                row.createCell(constraintColIndex).setCellValue(constraintData[i]);
            } else {
                HIDDEN_SHEET.createRow(this.VALID_CONSTRAINT_ROW + i ).createCell(constraintColIndex).setCellValue(constraintData[i]);
            }
        }
        //把所有数据添加到隐藏的Sheet 列中 然后以引用列的方式显示下拉框数组 避免直接绑定数据验证 数据量过多不显示
        //创建一个名称管理器 方便真正录入业主信息的表引用
        Name categoryName = workbook.createName();
        //坑1：名称管理器的名称不能设置为中文 否则引用不到
        categoryName.setNameName("thisHiddenName" + constraintColIndex);
        //使用当前字段头下标 获取excel头部的英语字符 用来组成以下的引用隐藏表的公式   ColumnEnglishChar 最后的值为 26个英语字母的其中一个
        char ColumnEnglishChar = (char) ((int) 'A' + constraintColIndex);
        //设置引用公式
        int constraintBeginRow = this.VALID_CONSTRAINT_ROW + 1;
        //经过变量替代后  例子: hiddenSheet!$A$12:$A$54    表示引用约束  hiddenSheet表的 A12行 到 A54行   数据长度-1  数组是从0开始
        String constraintFormula = "hiddenSheet!$" + ColumnEnglishChar +"$"+ constraintBeginRow + ":$" + ColumnEnglishChar +"$"+ (constraintBeginRow + constraintData.length - 1);
        categoryName.setRefersToFormula(constraintFormula);
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1,true);
    }

    /**
     * 对List中的对象字段去重 返回String数组
     * @param communityArchitecture     List数据
     * @param colIndex                  列类型：表明性别、楼栋、单元、楼层、门牌
     * @return                          返回去重好的字段 String数组
     */
    private static String[] getConstraintSet(List<?> communityArchitecture, int colIndex) {
        Set<String> set = new HashSet<>();
        for (Object object : communityArchitecture) {
            HouseEntity houseEntity = (HouseEntity)object;
            switch (colIndex) {
                //TITLE_FIELD 索引1为性别
                case 1:
                    set.add("男");set.add("女");
                    break;
                //TITLE_FIELD 索引2为楼栋
                case 2:
                    String building = houseEntity.getBuilding();
                    if (StrUtil.isNotEmpty(building)) {
                        set.add(building);
                    }
                    break;
                //TITLE_FIELD 索引3为单元
                case 3:
                    String unit = houseEntity.getUnit();
                    if(StrUtil.isNotEmpty(unit)){
                        set.add(unit);
                    }
                    break;
                //TITLE_FIELD 索引4为楼层
                case 4:
                    String floor = houseEntity.getFloor();
                    if(StrUtil.isNotEmpty(floor)){
                        set.add(floor);
                    }
                    break;
                //TITLE_FIELD 索引5为门牌
                case 5:
                    String door = houseEntity.getDoor();
                    if(StrUtil.isNotEmpty(door)){
                        set.add(door);
                    }
                    break;
                default:
                    break;
            }
        }
        return set.toArray(new String[0]);
    }


}
