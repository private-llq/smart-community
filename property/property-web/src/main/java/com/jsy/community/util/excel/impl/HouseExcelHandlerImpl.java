package com.jsy.community.util.excel.impl;

import com.jsy.community.constant.ConstError;
import com.jsy.community.constant.PropertyEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.HouseExcelHandler;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.vo.property.HouseImportErrorVO;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
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

    public static final String[] EXPORT_HOUSE_TEMPLATE = {"编号", "楼层", "楼栋编号", "楼栋名称", "单元编号", "单元名称", "建筑面积㎡", "房屋类型", "房产类型", "装修情况", "备注"};
    public static final String[] EXPORT_ERROR_INFO = {"编号", "楼层", "楼栋编号", "楼栋名称", "单元编号", "单元名称", "建筑面积㎡", "房屋类型", "房产类型", "装修情况", "备注", "错误提示"};
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
        ProprietorExcelCommander.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_HOUSE_TEMPLATE.length);
        // 创建Excel字段列
        createExcelField(workbook, sheet, EXPORT_HOUSE_TEMPLATE);
        //添加需要约束数据的列下标   "房屋类型", "房产类型", "装修情况"
        int[] arrIndex = new int[]{7, 8, 9};
        // 创建约束数据隐藏表 避免数据过大下拉框不显示问题
        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
        HashMap<Integer, String> constraintMap = new HashMap<>();
        constraintMap.put(7, "商铺,住宅");
        constraintMap.put(8, "商品房,房改房,集资房,经适房,廉租房,公租房,安置房,小产权房");
        constraintMap.put(9, "样板间,毛坯,简装,精装");
        //表明验证约束 结束行
        int endRow = 1000;
        // 添加约束
        for (int index : arrIndex) {
            String[] constraintData = constraintMap.get(index).split(",");
            //创建业主信息登记表与隐藏表的约束字段
            ProprietorExcelCommander.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, endRow, index);
            //绑定验证
            sheet.addValidationData(setBox(sheet, endRow, index));
        }
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1, true);
        return workbook;
    }

    /**
     * @Author: Pipi
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.ProprietorEntity>
     * @Date: 2021/5/19 11:47
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
            validExcelField(sheetAt, titleField);
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
                    cellValue = ProprietorExcelCommander.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 编号
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setNumber(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的编号!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 楼层
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setFloor(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的楼层!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 楼栋编号
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuildingNumber(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的楼栋编号!");
                                hasError = true;
                            }
                            break;
                        case 3:
                            // 楼栋名称
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuilding(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的楼栋名称!");
                                hasError = true;
                            }
                            break;
                        case 4:
                            // 单元编号
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setUnitNumber(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的单元编号!");
                                hasError = true;
                            }
                            break;
                        case 5:
                            // 单元名称
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setUnit(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的单元名称!");
                                hasError = true;
                            }
                            break;
                        case 6:
                            // 建筑面积㎡
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setBuildArea(Double.valueOf(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请填写正确的建筑面积!");
                                hasError = true;
                            }
                            break;
                        case 7:
                            // 房屋类型
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setHouseType(PropertyEnum.HouseTypeEnum.getCode(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请选择正确的房屋类型!");
                                hasError = true;
                            }
                            break;
                        case 8:
                            // 房产类型
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setPropertyType(PropertyEnum.PropertyTypeEnum.getCode(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请选择正确的房产类型!");
                                hasError = true;
                            }
                            break;
                        case 9:
                            // 装修情况
                            if (StringUtils.isNotBlank(cellValue)) {
                                houseEntity.setDecoration(PropertyEnum.DecorationEnum.getCode(cellValue));
                            } else {
                                addResolverError(errorVos, dataRow, "请选择正确的装修情况!");
                                hasError = true;
                            }
                            break;
                        case 10:
                            // 备注
                            houseEntity.setComment(cellValue);
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
        ProprietorExcelCommander.createExcelTitle(workbook, sheet, "房屋信息", 380, "宋体", 15, EXPORT_ERROR_INFO.length);
        //创建excel列字段
        ProprietorExcelCommander.createExcelField(workbook, sheet, EXPORT_ERROR_INFO);
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
        sheet.setColumnWidth(11, 15000);
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
                        // 编号
                        cell.setCellValue(vo.getNumber());
                        break;
                    case 1:
                        // 楼层
                        cell.setCellValue(vo.getFloor());
                        break;
                    case 2:
                        // 楼栋编号
                        cell.setCellValue(vo.getBuildingNumber());
                        break;
                    case 3:
                        // 楼栋名称
                        cell.setCellValue(vo.getBuilding());
                        break;
                    case 4:
                        // 单元编号
                        cell.setCellValue(vo.getUnitNumber());
                        break;
                    case 5:
                        // 单元名称
                        cell.setCellValue(vo.getUnit());
                        break;
                    case 6:
                        // 建筑面积㎡
                        cell.setCellValue(String.valueOf(vo.getBuildArea()));
                        break;
                    case 7:
                        // 房屋类型
                        cell.setCellValue(vo.getHouseType());
                        break;
                    case 8:
                        // 房产类型
                        cell.setCellValue(vo.getPropertyType());
                        break;
                    case 9:
                        // 装修情况
                        cell.setCellValue(vo.getDecoration());
                        break;
                    case 10:
                        // 备注
                        cell.setCellValue(vo.getComment());
                        break;
                    case 11:
                        // 其他错误信息
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
        boolean isExistObj = errorList.stream().anyMatch(vo3 -> number.equals(vo3.getNumber()));
        //存在对象则 设置完Remark 直接return
        if (isExistObj) {
            return;
        }
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ProprietorExcelCommander.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    vo.setNumber(stringCellValue);
                    break;
                case 1:
                    vo.setFloor(stringCellValue);
                    break;
                case 2:
                    vo.setBuildingNumber(stringCellValue);
                    break;
                case 3:
                    vo.setBuilding(stringCellValue);
                    break;
                case 4:
                    vo.setUnitNumber(stringCellValue);
                    break;
                case 5:
                    vo.setUnit(stringCellValue);
                    break;
                case 6:
                    vo.setBuildArea(Double.valueOf(stringCellValue));
                    break;
                case 7:
                    vo.setHouseType(stringCellValue);
                    break;
                case 8:
                    vo.setPropertyType(stringCellValue);
                    break;
                case 9:
                    vo.setDecoration(stringCellValue);
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
     * 为错误对象设置错误msg 便于excel回显
     * 使用realName作为属性字段查找是否有这个对象 如果 有直接返回 没有则创建一个对象返回
     * @param errorList 查找的列表
     * @param number  真实名称
     * @param errorMsg  错误信息
     * @return 返回列表对象
     */
    public static HouseImportErrorVO setVo(List<HouseImportErrorVO> errorList, String number, String errorMsg) {
        HouseImportErrorVO resVo = null;
        for (HouseImportErrorVO vo : errorList) {
            if (vo.getNumber().equals(number)) {
                resVo = vo;
                break;
            }
        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        HouseImportErrorVO vo = Optional.ofNullable(resVo).orElseGet(HouseImportErrorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }
}
