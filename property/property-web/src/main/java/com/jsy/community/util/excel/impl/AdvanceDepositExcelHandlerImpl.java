package com.jsy.community.util.excel.impl;

import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.AdvanceDepositExcelHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.property.AdvanceDepositImportErrorVO;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/5/18 16:19
 * @Version: 1.0
 **/
@Service
public class AdvanceDepositExcelHandlerImpl implements AdvanceDepositExcelHandler {
    
    // 充值余额导入模板字段 如果增加字段 需要改变实现类逻辑
    public static final String[] EXPORT_ADVANCE_DEPOSIT_TEMPLATE = {"姓名", "手机", "房屋地址(楼宇单元房屋号码)", "房屋号码", "付款金额/(元)", "到账金额/(元)"};
    public static final String[] EXPORT_ADVANCE_DEPOSIT_ERROR_INFO = {"姓名", "手机", "房屋地址(楼宇单元房屋号码)", "房屋号码", "付款金额/(元)", "到账金额/(元)", "错误提示"};
    
    /**
     * @Author: DKS
     * @Description: 获取充值余额导入模板
     * @Return: org.apache.poi.ss.usermodel.Workbook
     * @Date: 2021/8/13 14:19
     **/
    @Override
    public Workbook exportAdvanceDepositTemplate() {
        // 表名称
        String titleName = "充值余额导入";
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_ADVANCE_DEPOSIT_TEMPLATE.length);
        // 创建Excel字段列
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_ADVANCE_DEPOSIT_TEMPLATE);
        //添加需要约束数据的列下标   "房屋类型", "房产类型", "装修情况"
//        int[] arrIndex = new int[]{7, 8, 9};
        // 创建约束数据隐藏表 避免数据过大下拉框不显示问题
//        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
//        HashMap<Integer, String> constraintMap = new HashMap<>();
//        constraintMap.put(7, "商铺,住宅");
//        constraintMap.put(8, "商品房,房改房,集资房,经适房,廉租房,公租房,安置房,小产权房");
//        constraintMap.put(9, "样板间,毛坯,简装,精装");
        //表明验证约束 结束行
//        int endRow = 1000;
        // 添加约束
//        for (int index : arrIndex) {
//            String[] constraintData = constraintMap.get(index).split(",");
//            //创建业主信息登记表与隐藏表的约束字段
//            ExcelUtil.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, endRow, index);
//            //绑定验证
//            sheet.addValidationData(ExcelUtil.setBox(sheet, endRow, index));
//        }
        //隐藏 隐藏表  下标1 就是隐藏表
//        workbook.setSheetHidden(1, true);
        return workbook;
    }
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/8/13 17:01
     */
    @Override
    public List<PropertyAdvanceDepositEntity> importAdvanceDepositExcel(MultipartFile excel, List<AdvanceDepositImportErrorVO> errorVos) {
        List<PropertyAdvanceDepositEntity> propertyAdvanceDepositEntity = new ArrayList<>();
        //把文件流转换为工作簿
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(EXPORT_ADVANCE_DEPOSIT_TEMPLATE, EXPORT_ADVANCE_DEPOSIT_TEMPLATE.length);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            PropertyAdvanceDepositEntity entity;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                entity = new PropertyAdvanceDepositEntity();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 姓名
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setRealName(cellValue);
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的姓名!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 手机号
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setMobile(cellValue);
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的手机号!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 房屋地址
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setAddress(cellValue);
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的房屋地址!");
                                hasError = true;
                            }
                            break;
                        case 3:
                            // 房屋号码
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setDoor(cellValue);
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的房屋号码!");
                                hasError = true;
                            }
                            break;
                        case 4:
                            // 付款金额
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setPayAmount(new BigDecimal(cellValue));
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的付款金额!");
                                hasError = true;
                            }
                            break;
                        case 5:
                            // 到账金额
                            if (StringUtils.isNotBlank(cellValue)) {
                                entity.setReceivedAmount(new BigDecimal(cellValue));
                            } else {
                                addAdvanceDepositResolverError(errorVos, dataRow, "请填写正确的到账金额!");
                                hasError = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if(!hasError){
                    propertyAdvanceDepositEntity.add(entity);
                }
            }
            return propertyAdvanceDepositEntity;
        } catch (IOException e) {
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }
    
//    /**
//     *@Author: DKS
//     *@Description: 导出楼栋上传时的错误信息
//     *@Param: :
//     *@Return: org.apache.poi.ss.usermodel.Workbook
//     *@Date: 2021/8/10 10:51
//     **/
//    @Override
//    public Workbook exportBuildingErrorExcel(List<BuildingImportErrorVO> errorVos) {
//        //创建excel 工作簿对象
//        Workbook workbook = new XSSFWorkbook();
//        //创建 一个工作表
//        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("楼栋信息错误收集");
//        //创建excel标题行头
//        ExcelUtil.createExcelTitle(workbook, sheet, "楼栋信息", 380, "宋体", 15, EXPORT_BUILDING_ERROR_INFO.length);
//        //创建excel列字段
//        ExcelUtil.createExcelField(workbook, sheet, EXPORT_BUILDING_ERROR_INFO);
//        sheet.setColumnWidth(0, 4000);
//        sheet.setColumnWidth(1, 4000);
//        sheet.setColumnWidth(2, 4000);
//        sheet.setColumnWidth(3, 4000);
//        //每行excel数据
//        XSSFRow row;
//        //每列数据
//        XSSFCell cell;
//        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
//        for (int index = 0; index < errorVos.size(); index++) {
//            row = sheet.createRow(index + 2);
//            //创建列
//            for (int j = 0; j < EXPORT_ERROR_INFO.length; j++) {
//                cell = row.createCell(j);
//                BuildingImportErrorVO vo = errorVos.get(index);
//                switch (j) {
//                    case 0:
//                        // 所属楼宇
//                        cell.setCellValue(vo.getBuilding());
//                        break;
//                    case 1:
//                        // 楼宇总层数
//                        cell.setCellValue(vo.getTotalFloor());
//                        break;
//                    case 2:
//                        // 楼宇名称
//                        cell.setCellValue(vo.getBuildingTypeName());
//                        break;
//                    case 3:
//                        // 错误提示
//                        cell.setCellValue(vo.getRemark());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//        return workbook;
//    }
    
    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addAdvanceDepositResolverError(@NonNull List<AdvanceDepositImportErrorVO> errorList, @NonNull Row dataRow, String errorMsg) {
        //获取单元格
        XSSFCell valueCell = (XSSFCell) dataRow.getCell(0);
        //设置单元格类型
        valueCell.setCellType(CellType.STRING);
        String number = valueCell.getStringCellValue();
        //如果在错误集合里面已经存在这个编号的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        AdvanceDepositImportErrorVO vo = setAdvanceDepositVo(errorList, number, errorMsg);
        //每一列对象
        Cell cell;
        //每一列对象值
        String stringCellValue;
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ExcelUtil.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    vo.setName(stringCellValue);
                    break;
                case 1:
                    vo.setMobile(stringCellValue);
                    break;
                case 2:
                    vo.setHouseAddress(stringCellValue);
                    break;
                case 3:
                    vo.setDoor(stringCellValue);
                    break;
                case 4:
                    vo.setPayAmount(new BigDecimal(stringCellValue));
                    break;
                case 5:
                    vo.setReceivedAmount(new BigDecimal(stringCellValue));
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
    public static AdvanceDepositImportErrorVO setAdvanceDepositVo(List<AdvanceDepositImportErrorVO> errorList, String number, String errorMsg) {
        AdvanceDepositImportErrorVO resVo = null;
//        for (HouseImportErrorVO vo : errorList) {
//            if (vo.getNumber().equals(number)) {
//                resVo = vo;
//                break;
//            }
//        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        AdvanceDepositImportErrorVO vo = Optional.ofNullable(resVo).orElseGet(AdvanceDepositImportErrorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }
    
    /**
     *@Author: DKS
     *@Description: 写入充值余额导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/8/16 13:50
     **/
    public Workbook exportAdvanceDepositErrorExcel(List<AdvanceDepositImportErrorVO> errorVos) {
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("充值余额导入错误收集");
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, "充值余额", 380, "宋体", 15, EXPORT_ADVANCE_DEPOSIT_ERROR_INFO.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_ADVANCE_DEPOSIT_ERROR_INFO);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 4000);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_ADVANCE_DEPOSIT_ERROR_INFO.length; j++) {
                cell = row.createCell(j);
                AdvanceDepositImportErrorVO vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        // 姓名
                        cell.setCellValue(vo.getName());
                        break;
                    case 1:
                        // 手机
                        cell.setCellValue(vo.getMobile());
                        break;
                    case 2:
                        // 房屋地址(楼宇单元房屋号码)
                        cell.setCellValue(vo.getHouseAddress());
                        break;
                    case 3:
                        // 房屋号码
                        cell.setCellValue(vo.getDoor());
                        break;
                    case 4:
                        // 付款金额/(元)
                        cell.setCellValue(String.valueOf(vo.getPayAmount()));
                        break;
                    case 5:
                        // 到账金额/(元)
                        cell.setCellValue(String.valueOf(vo.getReceivedAmount()));
                        break;
                    case 6:
                        // 错误提示
                        cell.setCellValue(vo.getRemark());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
}
