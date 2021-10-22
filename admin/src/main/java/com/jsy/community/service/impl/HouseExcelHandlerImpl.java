package com.jsy.community.service.impl;

import com.jsy.community.entity.HouseEntity;
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
 * @Author: Pipi
 * @Description:
 * @Date: 2021/5/18 16:19
 * @Version: 1.0
 **/
@Service
public class HouseExcelHandlerImpl implements HouseExcelHandler {
    
    // 导出房屋信息表字段 如果增加字段 需要改变实现类逻辑
    protected static final String[] House_TITLE_FIELD = {"ID", "房屋号码", "所属楼宇", "所属单元", "所在楼层", "房屋地址", "建筑面积", "实用面积", "状态", "住户数量", "备注"};
    
    /**
     *@Author: DKS
     *@Description: 导出房屋信息表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/9 14:46
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
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 2000);
        sheet.setColumnWidth(10, 6000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < House_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                HouseEntity entity = (HouseEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // ID
                        cell.setCellValue(entity.getId());
                        break;
                    case 1:
                        // 房屋号码
                        cell.setCellValue(entity.getDoor());
                        break;
                    case 2:
                        // 所属楼宇
                        cell.setCellValue(entity.getBuilding());
                        break;
                    case 3:
                        // 所属单元
                        cell.setCellValue(entity.getUnit());
                        break;
                    case 4:
                        // 所在楼层
                        cell.setCellValue(entity.getFloor());
                        break;
                    case 5:
                        // 房屋地址
                        cell.setCellValue(entity.getBuilding() + entity.getUnit() + entity.getDoor());
                        break;
                    case 6:
                        // 建筑面积
                        cell.setCellValue(entity.getBuildArea());
                        break;
                    case 7:
                        // 实用面积
                        cell.setCellValue(entity.getPracticalArea());
                        break;
                    case 8:
                        // 状态
                        cell.setCellValue(entity.getStatus());
                        break;
                    case 9:
                        // 住户数量
                        cell.setCellValue(entity.getHouseNumber());
                        break;
                    case 10:
                        // 备注
                        cell.setCellValue(entity.getComment());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
}
