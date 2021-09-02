package com.jsy.community.util.excel.impl;

import com.jsy.community.util.MembersHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.property.HouseMemberVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 房间成员导入导出
 * @author: Hu
 * @create: 2021-08-31 15:45
 **/
@Service
public class MembersHandlerImpl implements MembersHandler {

    public static final String[] EXPORT_MEMBERS_TITLE = {"业主姓名", "APP用户名", "联系电话","房屋地址","身份","有效期"};



    /**
     * @Description: 成员表导出
     * @author: Hu
     * @since: 2021/8/31 15:47
     * @Param: [houseMemberVOS]
     * @return: org.apache.poi.ss.usermodel.Workbook
     */
    @Override
    public Workbook exportRelation(List<HouseMemberVO> houseMemberVOS) {
        //工作表名称
        String titleName = "房屋信息表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = EXPORT_MEMBERS_TITLE;
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
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);
        for (int index = 0; index < houseMemberVOS.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_MEMBERS_TITLE.length; j++) {
                cell = row.createCell(j);
                HouseMemberVO entity = (HouseMemberVO) houseMemberVOS.get(index);
                switch (j) {
                    case 0:
                        // 业主姓名
                        cell.setCellValue(entity.getName());
                        break;
                    case 1:
                        // App用户名
                        cell.setCellValue(entity.getAppName());
                        break;
                    case 2:
                        // 联系电话
                        cell.setCellValue(entity.getMobile());
                        break;
                    case 3:
                        // 房屋地址
                        cell.setCellValue(entity.getHouseSite());
                        break;
                    case 4:
                        // 身份
                        if (entity.getRelation()==1){
                            cell.setCellValue("业主");
                            break;
                        }else {
                            if (entity.getRelation()==6){
                                cell.setCellValue("家属");
                                break;
                            }else {
                                cell.setCellValue("租客");
                                break;
                            }
                        }
                    case 5:
                        // 有效期
                        if (entity.getValidTime()!=null){
                            cell.setCellValue(entity.getValidTime().toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
}
