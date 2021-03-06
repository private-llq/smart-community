package com.jsy.community.util.excel.impl;

import com.jsy.community.entity.HouseBuildingTypeEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.HouseExcelHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.property.BuildingImportErrorVO;
import com.jsy.community.vo.property.HouseImportErrorVO;
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
import java.util.*;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/5/18 16:19
 * @Version: 1.0
 **/
@Service
public class HouseExcelHandlerImpl implements HouseExcelHandler {

//    public static final String[] EXPORT_HOUSE_TEMPLATE = {"编号", "楼层", "楼栋编号", "楼栋名称", "单元编号", "单元名称", "建筑面积㎡", "房屋类型", "房产类型", "装修情况", "备注"};
//    public static final String[] EXPORT_ERROR_INFO = {"编号", "楼层", "楼栋编号", "楼栋名称", "单元编号", "单元名称", "建筑面积㎡", "房屋类型", "房产类型", "装修情况", "备注", "错误提示"};
    public static final String[] EXPORT_HOUSE_TEMPLATE = {"房屋号码(必填)", "所属楼宇(必填)", "楼宇总层数(必填)", "所属单元(必填)", "所属楼层(必填)", "建筑面积(必填)", "实用面积(选填)", "房屋状态(选填)", "备注(选填)", "房屋地址(选填)"};
    public static final String[] EXPORT_ERROR_INFO = {"房屋号码(必填)", "所属楼宇(必填)", "楼宇总层数(必填)", "所属单元(必填)", "所属楼层(必填)", "建筑面积(必填)", "实用面积(选填)", "房屋状态(选填)", "备注(选填)", "房屋地址(选填)", "错误提示"};
    // 导出房屋信息表字段 如果增加字段 需要改变实现类逻辑
    protected static final String[] House_TITLE_FIELD = {"ID", "房屋号码", "所属楼宇", "所属单元", "所在楼层", "房屋地址", "建筑面积", "实用面积", "状态", "住户数量", "备注"};
    // 楼宇导入模板字段 如果增加字段 需要改变实现类逻辑
    public static final String[] EXPORT_BUILDING_TEMPLATE = {"楼宇名称(必填)", "楼宇层数(必填)", "楼宇分类(必填)"};
    public static final String[] EXPORT_BUILDING_ERROR_INFO = {"楼宇名称(必填)", "楼宇层数(必填)", "楼宇分类(必填)", "错误提示"};
    
    /**
     * @Author: Pipi
     * @Description: 获取房屋模板
     * @Param: :
     * @Return: org.apache.poi.ss.usermodel.Workbook
     * @Date: 2021/5/18 16:19
     **/
    @Override
    public Workbook exportHouseTemplate() {
        // 表名称
        String titleName = "房屋信息";
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_HOUSE_TEMPLATE.length);
        // 创建Excel字段列
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_HOUSE_TEMPLATE);
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

//    /**
//     * @Author: Pipi
//     * @Description: 解析、常规格式效验Excel数据
//     * @Param: excel:
//     * @Param: errorVos:
//     * @Return: java.util.List<com.jsy.community.entity.ProprietorEntity>
//     * @Date: 2021/5/19 11:47
//     */
//    @Override
//    public List<HouseEntity> importHouseExcel(MultipartFile excel, List<HouseImportErrorVO> errorVos) {
//        List<HouseEntity> houseEntities = new ArrayList<>();
//        //把文件流转换为工作簿
//        try {
//            //把文件流转换为工作簿
//            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
//            //从工作簿中读取工作表
//            Sheet sheetAt = workbook.getSheetAt(0);
//            //excel 字段列
//            String[] titleField = Arrays.copyOf(EXPORT_HOUSE_TEMPLATE, EXPORT_HOUSE_TEMPLATE.length - 1);
//            //效验excel标题行
//            ExcelUtil.validExcelField(sheetAt, titleField);
//            //每一列对象 值
//            String cellValue;
//            //列对象
//            Cell cell;
//            //行对象
//            Row dataRow;
//            //每一行的数据对象
//            HouseEntity houseEntity;
//            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
//            boolean hasError;
//            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
//            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
//                dataRow = sheetAt.getRow(j);
//                hasError = false;
//                //如果这行数据不为空 创建一个 实体接收 信息
//                houseEntity = new HouseEntity();
//                for (int z = 0; z < titleField.length; z++) {
//                    cell = dataRow.getCell(z);
//                    cellValue = ExcelUtil.getCellValForType(cell).toString();
//                    //列字段校验
//                    switch (z) {
//                        case 0:
//                            // 编号
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setNumber(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的编号!");
//                                hasError = true;
//                            }
//                            break;
//                        case 1:
//                            // 楼层
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setFloor(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的楼层!");
//                                hasError = true;
//                            }
//                            break;
//                        case 2:
//                            // 楼栋编号
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setBuildingNumber(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的楼栋编号!");
//                                hasError = true;
//                            }
//                            break;
//                        case 3:
//                            // 楼栋名称
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setBuilding(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的楼栋名称!");
//                                hasError = true;
//                            }
//                            break;
//                        case 4:
//                            // 单元编号
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setUnitNumber(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的单元编号!");
//                                hasError = true;
//                            }
//                            break;
//                        case 5:
//                            // 单元名称
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setUnit(cellValue);
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的单元名称!");
//                                hasError = true;
//                            }
//                            break;
//                        case 6:
//                            // 建筑面积㎡
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setBuildArea(Double.valueOf(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请填写正确的建筑面积!");
//                                hasError = true;
//                            }
//                            break;
//                        case 7:
//                            // 房屋类型
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setHouseType(PropertyEnum.HouseTypeEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的房屋类型!");
//                                hasError = true;
//                            }
//                            break;
//                        case 8:
//                            // 房产类型
//                            /*if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setPropertyType(PropertyEnum.PropertyTypeEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的房产类型!");
//                                hasError = true;
//                            }*/
//                            break;
//                        case 9:
//                            // 装修情况
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setDecoration(PropertyEnum.DecorationEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的装修情况!");
//                                hasError = true;
//                            }
//                            break;
//                        case 10:
//                            // 备注
//                            houseEntity.setComment(cellValue);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                if(!hasError){
//                    houseEntities.add(houseEntity);
//                }
//            }
//            return houseEntities;
//        } catch (IOException e) {
//            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
//        }
//    }
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/8/7 13:42
     */
    @Override
    public List<HouseEntity> importHouseExcel(MultipartFile excel, List<HouseImportErrorVO> errorVos) {
        List<HouseEntity> houseEntities = new ArrayList<>();
        //把文件流转换为工作簿
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(EXPORT_HOUSE_TEMPLATE, EXPORT_HOUSE_TEMPLATE.length - 1);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            HouseEntity houseEntity;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                houseEntity = new HouseEntity();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 房屋号码
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setDoor(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的房屋号码!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 楼栋名称
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuilding(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的楼栋名称!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 总楼层数
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setTotalFloor(Integer.valueOf(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的总楼层数!");
                                hasError = true;
                            }
                            break;
                        case 3:
                            // 单元名称
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setUnit(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的单元名称!");
                                hasError = true;
                            }
                            break;
                        case 4:
                            // 楼层
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setFloor(Integer.valueOf(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的楼层!");
                                hasError = true;
                            }
                            break;
                        case 5:
                            // 建筑面积㎡
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuildArea(Double.valueOf(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的建筑面积!");
                                hasError = true;
                            }
                            break;
                        case 6:
                            // 实用面积㎡
                            houseEntity.setPracticalArea(Double.valueOf(cellValue));
                            break;
                        case 7:
                            // 房屋状态
                            houseEntity.setStatus(cellValue);
                            break;
//                        case 8:
//                            // 房屋类型
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setHouseType(PropertyEnum.HouseTypeEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的房屋类型!");
//                                hasError = true;
//                            }
//                            break;
//                        case 8:
//                            // 房产类型
//                            /*if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setPropertyType(PropertyEnum.PropertyTypeEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的房产类型!");
//                                hasError = true;
//                            }*/
//                            break;
//                        case 9:
//                            // 装修情况
//                            if (StringUtils.isNotBlank(cellValue)) {
//                                houseEntity.setDecoration(PropertyEnum.DecorationEnum.getCode(cellValue));
//                            } else {
//                                addResolverError(errorVos, dataRow, "请选择正确的装修情况!");
//                                hasError = true;
//                            }
//                            break;
                        case 8:
                            // 备注
                            houseEntity.setComment(cellValue);
                            break;
                        case 9:
                            // 房屋地址
                            houseEntity.setAddress(cellValue);
                            break;
                        default:
                            break;
                    }
                }
                if(!hasError){
                    houseEntities.add(houseEntity);
                }
            }
            return houseEntities;
        } catch (IOException e) {
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/8/7 13:42
     */
    @Override
    public List<HouseEntity> importBuildingExcel(MultipartFile excel, List<BuildingImportErrorVO> errorVos) {
        List<HouseEntity> houseEntities = new ArrayList<>();
        //把文件流转换为工作簿
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(EXPORT_BUILDING_TEMPLATE, EXPORT_BUILDING_TEMPLATE.length);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            HouseEntity houseEntity;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                houseEntity = new HouseEntity();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 楼栋名称
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuilding(cellValue);
                            } else {
                                addBuildingResolverError(errorVos, dataRow, "请填写正确的楼宇名称!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 总楼层数
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setTotalFloor(Integer.valueOf(cellValue));
                            } else {
                                addBuildingResolverError(errorVos, dataRow, "请填写正确的楼宇层数!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 楼宇分类
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuildingTypeName(cellValue);
                            } else {
                                addBuildingResolverError(errorVos, dataRow, "请填写正确的楼宇分类!");
                                hasError = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if(!hasError){
                    houseEntities.add(houseEntity);
                }
            }
            return houseEntities;
        } catch (IOException e) {
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }

    /**
     *@Author: Pipi
     *@Description: 导出上传时的错误信息
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/5/21 17:51
     **/
    @Override
    public Workbook exportErrorExcel(List<HouseImportErrorVO> errorVos) {
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("房屋信息错误收集");
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, "房屋信息", 380, "宋体", 15, EXPORT_ERROR_INFO.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_ERROR_INFO);
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
        sheet.setColumnWidth(10, 15000);
//        sheet.setColumnWidth(10, 2000);
//        sheet.setColumnWidth(11, 15000);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_ERROR_INFO.length; j++) {
                cell = row.createCell(j);
                HouseImportErrorVO vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        // 房屋号码
                        if (StringUtils.isNotBlank(vo.getDoor())) {
                            cell.setCellValue(vo.getDoor());
                        }
                        break;
                    case 1:
                        // 所属楼宇
                        if (StringUtils.isNotBlank(vo.getBuilding())) {
                            cell.setCellValue(vo.getBuilding());
                        }
                        break;
                    case 2:
                        // 楼宇总层数
                        if (vo.getTotalFloor() != null) {
                            cell.setCellValue(vo.getTotalFloor());
                        }
                        break;
                    case 3:
                        // 所属单元
                        if (StringUtils.isNotBlank(vo.getUnit())) {
                            cell.setCellValue(vo.getUnit());
                        }
                        break;
                    case 4:
                        // 所属楼层
                        if (vo.getFloor() != null) {
                            cell.setCellValue(vo.getFloor());
                        }
                        break;
                    case 5:
                        // 建筑面积㎡
                        if (vo.getBuildArea() != null) {
                            cell.setCellValue(String.valueOf(vo.getBuildArea()));
                        }
                        break;
                    case 6:
                        // 实用面积㎡
                        if (vo.getPracticalArea() != null) {
                            cell.setCellValue(String.valueOf(vo.getPracticalArea()));
                        }
                        break;
                    case 7:
                        // 房屋状态
                        if (StringUtils.isNotBlank(vo.getStatus())) {
                            cell.setCellValue(vo.getStatus());
                        }
                        break;
                    case 8:
                        // 备注
                        if (StringUtils.isNotBlank(vo.getComment())) {
                            cell.setCellValue(vo.getComment());
                        }
                        break;
                    case 9:
                        // 房屋地址
                        if (StringUtils.isNotBlank(vo.getAddress())) {
                            cell.setCellValue(vo.getAddress());
                        }
                        break;
                    case 10:
                        // 错误提示
                        if (StringUtils.isNotBlank(vo.getRemark())) {
                            cell.setCellValue(vo.getRemark());
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
     *@Author: DKS
     *@Description: 导出楼栋上传时的错误信息
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/10 10:51
     **/
    @Override
    public Workbook exportBuildingErrorExcel(List<BuildingImportErrorVO> errorVos) {
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("楼栋信息错误收集");
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, "楼栋信息", 380, "宋体", 15, EXPORT_BUILDING_ERROR_INFO.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_BUILDING_ERROR_INFO);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_BUILDING_ERROR_INFO.length; j++) {
                cell = row.createCell(j);
                BuildingImportErrorVO vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        // 所属楼宇
                        if (StringUtils.isNotBlank(vo.getBuilding())) {
                            cell.setCellValue(vo.getBuilding());
                        }
                        break;
                    case 1:
                        // 楼宇总层数
                        if (vo.getTotalFloor() != null) {
                            cell.setCellValue(vo.getTotalFloor());
                        }
                        break;
                    case 2:
                        // 楼宇名称
                        if (StringUtils.isNotBlank(vo.getBuildingTypeName())) {
                            cell.setCellValue(vo.getBuildingTypeName());
                        }
                        
                        break;
                    case 3:
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

    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addResolverError(@NonNull List<HouseImportErrorVO> errorList, @NonNull Row dataRow, String errorMsg) {
        //获取单元格
        XSSFCell valueCell = (XSSFCell) dataRow.getCell(0);
        //设置单元格类型
        valueCell.setCellType(CellType.STRING);
        String number = valueCell.getStringCellValue();
        //如果在错误集合里面已经存在这个编号的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        HouseImportErrorVO vo = setVo(errorList, number, errorMsg);
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
                    vo.setDoor(stringCellValue);
                    break;
                case 1:
                    vo.setBuilding(stringCellValue);
                    break;
                case 2:
                    vo.setTotalFloor(Integer.valueOf(stringCellValue));
                    break;
                case 3:
                    vo.setUnit(stringCellValue);
                    break;
                case 4:
                    vo.setFloor(Integer.valueOf(stringCellValue));
                    break;
                case 5:
                    vo.setBuildArea(Double.valueOf(stringCellValue));
                    break;
                case 6:
                    vo.setPracticalArea(Double.valueOf(stringCellValue));
                    break;
                case 7:
                    vo.setStatus(stringCellValue);
                    break;
                case 8:
                    vo.setComment(stringCellValue);
                    break;
                case 9:
                    vo.setAddress(stringCellValue);
                    break;
                case 10:
                    vo.setRemark(stringCellValue);
                    break;
                default:
                    break;
            }
        }
        errorList.add(vo);
    }
    
    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addBuildingResolverError(@NonNull List<BuildingImportErrorVO> errorList, @NonNull Row dataRow, String errorMsg) {
        //获取单元格
        XSSFCell valueCell = (XSSFCell) dataRow.getCell(0);
        //设置单元格类型
        valueCell.setCellType(CellType.STRING);
        String number = valueCell.getStringCellValue();
        //如果在错误集合里面已经存在这个编号的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        BuildingImportErrorVO vo = setBuildingVo(errorList, number, errorMsg);
        //每一列对象
        Cell cell;
        //每一列对象值
        String stringCellValue;
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ExcelUtil.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    vo.setBuilding(stringCellValue);
                    break;
                case 1:
                    if (StringUtils.isNotBlank(stringCellValue)) {
                        vo.setTotalFloor(Integer.valueOf(stringCellValue));
                    }
                    break;
                case 2:
                    vo.setBuildingTypeName(stringCellValue);
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
    public static HouseImportErrorVO setVo(List<HouseImportErrorVO> errorList, String number, String errorMsg) {
        HouseImportErrorVO resVo = null;
//        for (HouseImportErrorVO vo : errorList) {
//            if (vo.getNumber().equals(number)) {
//                resVo = vo;
//                break;
//            }
//        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        HouseImportErrorVO vo = Optional.ofNullable(resVo).orElseGet(HouseImportErrorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }
    
    /**
     * 为错误对象设置错误msg 便于excel回显
     * 使用realName作为属性字段查找是否有这个对象 如果 有直接返回 没有则创建一个对象返回
     * @param errorList 查找的列表
     * @param number  真实名称
     * @param errorMsg  错误信息
     * @return 返回列表对象
     */
    public static BuildingImportErrorVO setBuildingVo(List<BuildingImportErrorVO> errorList, String number, String errorMsg) {
        BuildingImportErrorVO resVo = null;
//        for (HouseImportErrorVO vo : errorList) {
//            if (vo.getNumber().equals(number)) {
//                resVo = vo;
//                break;
//            }
//        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        BuildingImportErrorVO vo = Optional.ofNullable(resVo).orElseGet(BuildingImportErrorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }
    
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
    
    /**
     * @Author: DKS
     * @Description: 下载楼栋信息导入模板
     * @Param: :
     * @Return: org.apache.poi.ss.usermodel.Workbook
     * @Date: 2021/8/10 9:19
     **/
    @Override
    public Workbook exportBuildingTemplate(List<HouseBuildingTypeEntity> houseBuildingTypeEntities) {
        String[] arr = new String[houseBuildingTypeEntities.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = houseBuildingTypeEntities.get(i).getPropertyTypeName();
        }
        String str = StringUtils.join(arr,",");
        // 表名称
        String titleName = "楼宇信息";
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_BUILDING_TEMPLATE.length);
        // 创建Excel字段列
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_BUILDING_TEMPLATE);
        //添加需要约束数据的列下标
        int[] arrIndex = new int[]{2};
        // 创建约束数据隐藏表 避免数据过大下拉框不显示问题
        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
        HashMap<Integer, String> constraintMap = new HashMap<>();
        constraintMap.put(2, str);
        //表明验证约束 结束行
        int endRow = 1000;
        // 添加约束
        for (int index : arrIndex) {
            String[] constraintData = constraintMap.get(index).split(",");
            //创建楼宇导入表与隐藏表的约束字段
            ExcelUtil.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, endRow, index);
            //绑定验证
            sheet.addValidationData(ExcelUtil.setBox(sheet, endRow, index));
        }
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1, true);
        return workbook;
    }
}
