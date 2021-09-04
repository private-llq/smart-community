package com.jsy.community.util.excel.impl;

import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.MembersHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.property.HouseMemberVO;
import com.jsy.community.vo.property.RelationImportErrVO;
import com.jsy.community.vo.property.RelationImportQO;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: com.jsy.community
 * @description: 房间成员导入导出
 * @author: Hu
 * @create: 2021-08-31 15:45
 **/
@Service
public class MembersHandlerImpl implements MembersHandler {

    public static final String[] EXPORT_MEMBERS_TITLE = {"业主姓名", "APP用户名", "联系电话","房屋地址","身份","有效期"};
    public static final String[] EXPORT_MEMBERS_TEMPLATE = {"住户姓名(必填)", "住户身份(必填)", "手机号码(必填)", "所属楼宇(必填)", "所属单元(必填)", "房屋号码(必填)", "出生日期", "入住日期", "入住原因", "性别","业主卡号","单位"};
    public static final String[] EXPORT_MEMBERS_ERROR = {"住户姓名(必填)", "住户身份(必填)", "手机号码(必填)", "所属楼宇(必填)", "所属单元(必填)", "房屋号码(必填)", "出生日期", "入住日期", "入住原因", "性别","业主卡号","单位","错误提示"};



    /**
     * @Description: 成员表导入
     * @author: Hu
     * @since: 2021/9/3 14:06
     * @Param: [file]
     * @return: java.util.List<com.jsy.community.entity.HouseMemberEntity>
     */
    @Override
    public List<RelationImportQO> importRelation(MultipartFile file,List<RelationImportErrVO> errorVos) {
        List<RelationImportQO> relationImportQOS = new ArrayList<>();
        //把文件流转换为工作簿
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(EXPORT_MEMBERS_TEMPLATE, EXPORT_MEMBERS_TEMPLATE.length - 1);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            RelationImportQO relationImportQO;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                relationImportQO = new RelationImportQO();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 住户姓名(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setName(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "住户姓名不能为空!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 住户身份(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                Integer code = BusinessEnum.RelationshipEnum.getNameCode(cellValue);
                                relationImportQO.setRelation(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "住户身份不能为空!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 手机号码(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                if (cellValue.matches(RegexUtils.REGEX_MOBILE)){
                                    relationImportQO.setMobile(cellValue);
                                }else {
                                    addResolverError(errorVos, dataRow, "不支持的手机号!");
                                    hasError = true;
                                }
                            } else {
                                addResolverError(errorVos, dataRow, "手机号不能为空!");
                                hasError = true;
                            }
                            break;
                        case 3:
                            // 所属楼宇(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setBuilding(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "所属楼宇不能为空!");
                                hasError = true;
                            }
                            break;
                        case 4:
                            // 所属单元(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setUnit(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "所属单元不能为空!");
                                hasError = true;
                            }
                            break;
                        case 5:
                            // 房屋号码(必填)
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setDoor(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "房间号码不能为空!");
                                hasError = true;
                            }
                            break;
                        case 6:
                            // 出生日期
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setBirthday(LocalDate.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            }
                            break;
                        case 7:
                            // 入住日期
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setEnterTime(LocalDate.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            }
                            break;
                        case 8:
                            // 入住原因
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setEnterReason(cellValue);
                            }
                            break;
                        case 9:
                            // 性别
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setSex(Integer.parseInt(cellValue));
                            }
                            break;
                        case 10:
                            // 业主卡号
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setCreditCard(cellValue);
                            }
                            break;
                        case 11:
                            // 单位
                            if (StringUtils.isNotBlank(cellValue)) {
                                relationImportQO.setRelationUnit(cellValue);
                            }
                            break;
                        default:
                            break;
                    }
                }
                if(!hasError){
                    relationImportQOS.add(relationImportQO);
                }
            }
            return relationImportQOS;
        } catch (IOException e) {
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }

    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addResolverError(@NonNull List<RelationImportErrVO> errorList, @NonNull Row dataRow, String errorMsg) {
        //获取单元格
        XSSFCell valueCell = (XSSFCell) dataRow.getCell(0);
        //设置单元格类型
        valueCell.setCellType(CellType.STRING);
        String number = valueCell.getStringCellValue();
        //如果在错误集合里面已经存在这个编号的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        RelationImportErrVO vo = setVo(errorList, number, errorMsg);
        //每一列对象
        Cell cell;
        //每一列对象值
        String stringCellValue;
        //根据当前excel行的业主姓名 验证 在之前的错误集合列表是否 存在该对象 如果存在 则不设置任何属性 只设置备注错误信息
        // boolean isExistObj = errorList.stream().anyMatch(vo3 -> number.equals(vo3.getNumber()));
        //存在对象则 设置完Remark 直接return
//        if (isExistObj) {
//            return;
//        }
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ExcelUtil.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setName(stringCellValue);
                    }
                    break;
                case 1:
                    // 住户身份(必填)
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setRelation(stringCellValue);
                    }
                    break;
                case 2:
                    // 手机号码(必填)
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setMobile(stringCellValue);
                    }
                    break;
                case 3:
                    // 所属楼宇(必填)
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setBuilding(stringCellValue);
                    }
                    break;
                case 4:
                    // 所属单元(必填)
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setUnit(stringCellValue);
                    }
                    break;
                case 5:
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setDoor(stringCellValue);
                    }
                    break;
                case 6:
                    // 出生日期
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setBirthday(LocalDate.parse(stringCellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }
                    break;
                case 7:
                    // 入住日期
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setEnterTime(LocalDate.parse(stringCellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }
                    break;
                case 8:
                    // 入住原因
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setEnterReason(stringCellValue);
                    }
                    break;
                case 9:
                    // 性别
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setSex(Integer.parseInt(stringCellValue));
                    }
                    break;
                case 10:
                    // 业主卡号
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setCreditCard(stringCellValue);
                    }
                    break;
                case 11:
                    // 单位
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setRelationUnit(stringCellValue);
                    }
                    break;
                default:
                    break;
            }
        }
        errorList.add(vo);
    }

    /**
     * 为错误对象设置错误msg 便于excel回显
     * 使用realName作为属性字段查找是否有这个对象 如果 有直接返回 没有则创建一个对象返回
     * @param errorList 查找的列表
     * @param number  真实名称
     * @param errorMsg  错误信息
     * @return 返回列表对象
     */
    public static RelationImportErrVO setVo(List<RelationImportErrVO> errorList, String number, String errorMsg) {
        RelationImportErrVO resVo = null;
//        for (HouseImportErrorVO vo : errorList) {
//            if (vo.getNumber().equals(number)) {
//                resVo = vo;
//                break;
//            }
//        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        RelationImportErrVO vo = Optional.ofNullable(resVo).orElseGet(RelationImportErrVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setError(vo.getError() == null ? errorMsg :  vo.getError() + "，" + errorMsg );
        return vo;
    }

    @Override
    public Workbook exportRelationTemplate() {
        // 表名称
        String titleName = "房屋成员信息";
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_MEMBERS_TEMPLATE.length);
        // 创建Excel字段列
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_MEMBERS_TEMPLATE);
        return workbook;
    }

    @Override
    public Workbook exportErrorExcel(List<RelationImportErrVO> errorVos) {
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("成员信息错误收集");
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, "成员信息", 380, "宋体", 15, EXPORT_MEMBERS_ERROR.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_MEMBERS_ERROR);
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 2000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 2000);
        sheet.setColumnWidth(4, 2000);
        sheet.setColumnWidth(5, 2000);
        sheet.setColumnWidth(6, 2000);
        sheet.setColumnWidth(7, 2000);
        sheet.setColumnWidth(8, 2000);
        sheet.setColumnWidth(9, 2000);
        sheet.setColumnWidth(10, 2000);
        sheet.setColumnWidth(11, 2000);
        sheet.setColumnWidth(12, 2000);
        sheet.setColumnWidth(13, 2000);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_MEMBERS_ERROR.length; j++) {
                cell = row.createCell(j);
                RelationImportErrVO vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        if (StringUtils.isNotBlank(vo.getName())) {
                            cell.setCellValue(vo.getName());
                        }
                        break;
                    case 1:
                        // 住户身份(必填)
                        if (StringUtils.isNotBlank(vo.getRelation())) {
                            cell.setCellValue(vo.getRelation());
                        }
                        break;
                    case 2:
                        // 手机号码(必填)
                        if (StringUtils.isNotBlank(vo.getMobile())) {
                            cell.setCellValue(vo.getMobile());
                        }
                        break;
                    case 3:
                        // 所属楼宇(必填)
                        if (StringUtils.isNotBlank(vo.getBuilding())) {
                            cell.setCellValue(vo.getBuilding());
                        }
                        break;
                    case 4:
                        // 所属单元(必填)
                        if (StringUtils.isNotBlank(vo.getUnit())) {
                            cell.setCellValue(vo.getUnit());
                        }
                        break;
                    case 5:
                        if (StringUtils.isNotBlank(vo.getDoor())) {
                            cell.setCellValue(vo.getDoor());
                        }
                        break;
                    case 6:
                        // 出生日期
                        if (vo.getBirthday()!=null) {
                            cell.setCellValue(vo.getBirthday().toString());
                        }
                        break;
                    case 7:
                        // 入住日期
                        if (vo.getEnterTime()!=null) {
                            cell.setCellValue(vo.getEnterTime().toString());
                        }
                        break;
                    case 8:
                        // 入住原因
                        if (StringUtils.isNotBlank(vo.getEnterReason())) {
                            cell.setCellValue(vo.getEnterReason());
                        }
                        break;
                    case 9:
                        // 性别
                        if (vo.getSex()!=null) {
                            cell.setCellValue(vo.getSex());
                        }
                        break;
                    case 10:
                        // 业主卡号
                        if (StringUtils.isNotBlank(vo.getCreditCard())) {
                            cell.setCellValue(vo.getCreditCard());
                        }
                        break;
                    case 11:
                        // 单位
                        if (StringUtils.isNotBlank(vo.getRelationUnit())) {
                            cell.setCellValue(vo.getRelationUnit());
                        }
                        break;
                    case 12:
                        // 单位
                        if (StringUtils.isNotBlank(vo.getError())) {
                            cell.setCellValue(vo.getError());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

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
