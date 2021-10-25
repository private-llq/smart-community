package com.jsy.community.service.impl;

import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.service.HouseExcelHandler;
import com.jsy.community.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: DKS
 * @Description:
 * @Date: 2021/10/22 10:16
 * @Version: 1.0
 **/
@Service
public class HouseExcelHandlerImpl implements HouseExcelHandler {
    
    // 导出房屋信息表字段 如果增加字段 需要改变实现类逻辑
    private static final String[] House_TITLE_FIELD = {"物业", "小区", "楼宇", "单元", "房号", "建筑面积", "状态", "业主", "住户量"};
    
    // 导出房屋信息表字段 如果增加字段 需要改变实现类逻辑
    private static final String[] HOUSE_MEMBER_TITLE_FIELD = {"姓名", "电话", "房屋信息", "身份", "小区", "物业", "标签"};
    
    /**
     *@Author: DKS
     *@Description: 导出房屋信息表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/10/22 12:56
     **/
    @Override
    public Workbook exportHouse(List<?> entityList) {
        //工作表名称
        String titleName = "房屋信息表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = House_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < House_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                HouseEntity entity = (HouseEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 物业
                        cell.setCellValue(entity.getCompanyName());
                        break;
                    case 1:
                        // 小区
                        cell.setCellValue(entity.getCommunityName());
                        break;
                    case 2:
                        // 楼宇
                        cell.setCellValue(entity.getBuilding());
                        break;
                    case 3:
                        // 单元
                        cell.setCellValue(entity.getUnit());
                        break;
                    case 4:
                        // 房号
                        cell.setCellValue(entity.getDoor());
                        break;
                    case 5:
                        // 建筑面积
                        if (entity.getBuildArea() != null) {
                            cell.setCellValue(entity.getBuildArea());
                        }
                        break;
                    case 6:
                        // 状态
                        cell.setCellValue(entity.getStatus());
                        break;
                    case 7:
                        // 业主
                        cell.setCellValue(entity.getOwner());
                        break;
                    case 8:
                        // 住户量
                        cell.setCellValue(entity.getHouseNumber());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
    
    /**
     *@Author: DKS
     *@Description: 导出住户信息表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/10/22 16:00
     **/
    @Override
    public Workbook exportHouseMember(List<?> entityList) {
        //工作表名称
        String titleName = "住户信息表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = HOUSE_MEMBER_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < HOUSE_MEMBER_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                HouseMemberEntity entity = (HouseMemberEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 姓名
                        cell.setCellValue(entity.getName());
                        break;
                    case 1:
                        // 电话
                        cell.setCellValue(entity.getMobile());
                        break;
                    case 2:
                        // 房屋信息
                        cell.setCellValue(entity.getHouseSite());
                        break;
                    case 3:
                        // 身份
                        cell.setCellValue(entity.getRelationName());
                        break;
                    case 4:
                        // 小区
                        cell.setCellValue(entity.getCommunityName());
                        break;
                    case 5:
                        // 物业
                        cell.setCellValue(entity.getCompanyName());
                        break;
                    case 6:
                        // 标签
                        cell.setCellValue(entity.getTallyName());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
}
