package com.jsy.community.util.excel.impl;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.JSYExcel;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author YuLF
 * @since 2020-11-26 09:48
 * 业主信息录入.xlsx 下载模板、excel解析提供类
 */
@Slf4j
public class ProprietorInfoProvider implements JSYExcel {


    /**
     * 【导出业主信息登记表】生成录入业主信息excel 模板 返回excel数据流
     * @return 返回生成好的excel模板数据流，供控制层直接输出响应excel.xlsx文件
     * @author YuLF
     * @Param map                      key  name = 社区名   key communityUserNum = 社区房间数量
     * @Param entityList               excel列约束  数据集合 楼栋、单元、楼层、门牌
     * @since 2020/11/26 9:50
     */
    @Override
    public  Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> map) {
        //初始excel 表名称 和 表行数数据
        String sheetName = map.get("name") + "业主信息登记表";
        //拿到 社区总房间的条数   因为第一行和第二行是excel字段和标题 所以+2
        int validRow = Integer.parseInt(map.get("communityUserNum").toString()) + 2;
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(sheetName);
        //创建一个隐藏的工作表  来存放约束下拉框的数组数据  避免直接绑定所有数据 数据量过多 导致下拉框不显示
        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
        //获得表头字段列
        String[] titleField = ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD;
        //创建excel标题行头
        ProprietorExcelCommander.createExcelTitle(workbook, sheet, sheetName, 530, "宋体", 20, titleField.length);
        //创建excel列字段
        ProprietorExcelCommander.createExcelField(workbook, sheet, titleField);
        //添加需要约束数据的列下标   "性别", "楼栋", "单元", "楼层", "门牌"
        int[] arrIndex = new int[]{1,2,3,4,5};
        for (int index : arrIndex) {
            //通过传过来的 约束列constraintColIndex 获得List中的约束数据
            String[] constraintData = getConstraintSet(entityList, index);
            //创建业主信息登记表与隐藏表的约束字段
            ProprietorExcelCommander.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, validRow, index);
            //绑定验证
            sheet.addValidationData(ProprietorExcelCommander.setBox(sheet, validRow, index));
        }
        //设置第5列单元格格式 统一为 文本格式 防止 把第五列的门牌号 选择后自动转换为日期格式
        ProprietorExcelCommander.setCellFormatToString(workbook, new String[]{"5"}, sheetName, validRow);
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1,true);
        return workbook;
    }





    /**
     * [业主信息录入表]对List中的对象字段去重 返回String数组
     * @param communityArchitecture     List数据
     * @param colIndex                  列类型：表明性别、楼栋、单元、楼层、门牌
     * @return                          返回去重好的字段 String数组
     */
    private static String[] getConstraintSet(List<?> communityArchitecture, int colIndex) {
        Set<String> set = new HashSet<>(communityArchitecture.size());
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

    /**
     * 导入解析
     * @param proprietorExcel   excel文件
     * @return                  返回解析好的数据
     */
    @Override
    public List<UserEntity> importProprietorExcel(MultipartFile proprietorExcel, Map<String,Object> map) {
        List<UserEntity> userEntityList = new ArrayList<>();
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(proprietorExcel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD;
            //效验excel标题行
            ProprietorExcelCommander.validExcelField(sheetAt, titleField);

            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                Row dataRow = sheetAt.getRow(j);
                if (ProprietorExcelCommander.cellOneIsNotNull(dataRow)) {
                    //如果这行数据不为空 创建一个 实体接收 信息
                    UserEntity userEntity = new UserEntity();
                    userEntity.setHouseEntity(new HouseEntity());
                    //遍历列
                    //dataRow.getLastCellNum()避免有的列为空，所以 需要检查 9个列的字段 titleField.length
                    for (int z = 0; z < titleField.length; z++) {
                        Cell cell = dataRow.getCell(z);
                        String CellValue = ProprietorExcelCommander.getCellValForType(cell).toString();
                        //列字段效验
                        switch (z) {
                            // 1列 验证是否 是一个 正确的中国姓名
                            case 0:
                                if (RegexUtils.isRealName(CellValue)) {
                                    userEntity.setRealName(CellValue);
                                } else {
                                    //因为 第一行 和第二行 是标题 和字段 所以需要+1        列下标是按0开始的 需要+1
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + CellValue + "' 不是一个正确的中国姓名!");
                                }
                                break;
                            //第2列 验证是否是一个正确的 男 女
                            case 1:
                                if (CellValue.equals("男")) {
                                    userEntity.setSex(1);
                                } else if (CellValue.equals("女")) {
                                    userEntity.setSex(2);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + CellValue + "' 不是一个正确的性别!请选择正确的性别");
                                }
                                break;
                            //第3列 楼栋
                            case 2:
                                if (StringUtils.isNoneBlank(CellValue)) {
                                    userEntity.getHouseEntity().setBuilding(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列未选择楼栋!");
                                }
                                break;
                            //第4列 单元
                            case 3:
                                if (StringUtils.isNoneBlank(CellValue)) {
                                    userEntity.getHouseEntity().setUnit(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列未选择单元!");
                                }
                                break;
                            //第5列 楼层
                            case 4:
                                if (StringUtils.isNoneBlank(CellValue)) {
                                    userEntity.getHouseEntity().setFloor(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列未选择楼层!");
                                }
                                break;
                            //第6列 门牌
                            case 5:
                                if (StringUtils.isNoneBlank(CellValue)) {
                                    userEntity.getHouseEntity().setDoor(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列未选择门房号!");
                                }
                                break;
                            // 第7列 身份证 验证
                            case 6:
                                if (RegexUtils.isIDCard(CellValue)) {
                                    userEntity.setIdCard(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + CellValue + "' 不是一个正确的身份证号码!");
                                }
                                break;
                            // 第8列 手机号码 验证
                            case 7:
                                if (RegexUtils.isMobile(CellValue)) {
                                    userEntity.setMobile(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + CellValue + "' 不是一个正确的电话号码 电信|联通|移动!");
                                }
                                break;
                            //第9列 详细地址
                            case 8:
                                if (StringUtils.isNoneBlank(CellValue) && CellValue.length() < 128) {
                                    userEntity.setDetailAddress(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列  '" + CellValue + "' 详细地址不能为空，且字符不能大于127!");
                                }
                                break;
                            default:
                                break;
                        } //switch-end
                    }
                    //保证传进来的泛型是相同的
                    userEntityList.add(userEntity);
                }
            }
            return userEntityList;
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }




}
