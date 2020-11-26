package com.jsy.community.util;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.entity.HouseEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.util.*;

/**
 * @author YuLF
 * @since 2020-11-26 09:48
 */
public class ProprietorExcelFactory {

    /**
     * 业主信息录入表.xlsx 字段
     */
    private static final String[] TITLE_FIELD = {"姓名", "性别", "楼栋", "单元", "楼层", "门牌", "身份证", "联系方式", "详细地址"};


    /**
     * 行下拉框约束限制，最多到5000行
     */
    private static final int VALID_CONSTRAINT_ROW = 5000;

    /**
     * 生成录入业主信息excel 模板 返回excel数据流
     *
     * @return 返回生成好的excel模板数据流，供控制层直接输出响应excel.xlsx文件
     * @author YuLF
     * @Param workbook                 excel工作簿对象
     * @Param communityArchitecture    excel列约束  数据集合 楼栋、单元、楼层、门牌
     * @Param workSheetName            打开excel 的Sheet 工作表名称
     * @since 2020/11/26 9:50
     */
    public static Workbook generateExcel(List<HouseEntity> communityArchitecture, String workSheetName) {
        //2.2 创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(workSheetName);
        //1.创建表头行
        XSSFRow row = sheet.createRow(0);
        row.setHeight((short) (530));
        //创建表头列
        XSSFCell cell = row.createCell(0);
        //设置表头名称
        cell.setCellValue(workSheetName);
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
        //合并单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, TITLE_FIELD.length);
        sheet.addMergedRegion(region);
        //2.创建 工作表 字段标题 第二行
        XSSFRow row2 = sheet.createRow(1);
        CellStyle fieldCellStyle = workbook.createCellStyle();
        //设置粗体
        Font fieldFont = workbook.createFont();
        fieldFont.setBold(true);
        fieldCellStyle.setFont(fieldFont);
        //创建字段标题头
        for (int i = 0; i < TITLE_FIELD.length; i++) {
            //只要TITLE_FIELD 索引为 1、2、3、4、5都需要添加选择填写约束
            //性别、楼栋、单元、楼层、门牌单元格 列 选择约束添加限制
            if (i > 0 && i <= 5) {
                //绑定约束验证器
                sheet.addValidationData(setBox(sheet, communityArchitecture, i));
            }
            XSSFCell cell1 = row2.createCell(i);
            cell1.setCellValue(TITLE_FIELD[i]);
            cell1.setCellStyle(fieldCellStyle);
        }
        return workbook;
    }

    /**
     * 设置 业主信息登记.xlsx 单元格的输入限制，只能从特定的数据选择
     * @author YuLF
     * @since  2020/11/26 11:40
     * @Param  sheet                    工作表
     * @Param  communityArchitecture    数据库的楼栋、单元、楼层、门牌号List数据
     * @return 返回绑定好的数据验证器  用于单元格绑定这个验证器
     */
    private static DataValidation setBox(XSSFSheet sheet, List<HouseEntity> communityArchitecture, int constraintColIndex) {
        CellRangeAddressList addressList = new CellRangeAddressList(2, VALID_CONSTRAINT_ROW, constraintColIndex, constraintColIndex);
        //通过传过来的 约束列constraintColIndex 获得List中的约束数据
        String[] constraintData = getConstraintSet(communityArchitecture, constraintColIndex);
        //创建一个 XSSFSheet约束对象
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint dvConstraint = dvHelper
                .createExplicitListConstraint(constraintData);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
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
     * 对List中的对象字段去重 返回String数组
     * @param communityArchitecture     List数据
     * @param colIndex                  列类型：表明性别、楼栋、单元、楼层、门牌
     * @return                          返回去重好的字段 String数组
     */
    private static String[] getConstraintSet(List<HouseEntity> communityArchitecture, int colIndex) {
        Set<String> set = new HashSet<>();
        for (HouseEntity next : communityArchitecture) {
            switch (colIndex) {
                //TITLE_FIELD 索引1为性别
                case 1:
                    set.add("男");set.add("女");
                    break;
                //TITLE_FIELD 索引2为楼栋
                case 2:
                    String building = next.getBuilding();
                    if (StrUtil.isNotEmpty(building)) {
                        set.add(building);
                    }
                    break;
                //TITLE_FIELD 索引3为单元
                case 3:
                    String unit = next.getUnit();
                    if(StrUtil.isNotEmpty(unit)){
                        set.add(unit);
                    }
                    break;
                //TITLE_FIELD 索引4为楼层
                case 4:
                    String floor = next.getFloor();
                    if(StrUtil.isNotEmpty(floor)){
                        set.add(floor);
                    }
                    break;
                //TITLE_FIELD 索引5为门牌
                case 5:
                    String door = next.getDoor();
                    if(StrUtil.isNotEmpty(door)){
                        //加 [] 避免excel自动转换为日期格式 如序列值为1-2 被转换为1月2日
                        set.add("["+door+"]");
                    }
                    break;
                default:
                    break;
            }
        }
        return set.toArray(new String[0]);
    }

}
