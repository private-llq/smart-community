package com.jsy.community.util.excel.impl;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.property.ProprietorVO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
public class ProprietorInfoProvider {


    /**
     * 【导出业主信息登记表】生成录入业主信息excel 模板 返回excel数据流
     *
     * @param titleField excel标题字段列
     * @return 返回生成好的excel模板数据流，供控制层直接输出响应excel.xlsx文件
     * @author YuLF
     * @since 2020/11/26 9:50
     */
    public Workbook exportProprietorExcel(String[] titleField) {
        //初始excel 表名称 和 表行数数据
        String sheetName = ProprietorExcelCommander.PROPRIETOR_SHEET_NAME;
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(sheetName);
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, ProprietorExcelCommander.PROPRIETOR_TITLE_NAME, 380, "宋体", 15, titleField.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, titleField);

        return workbook;
    }


    /**
     * [业主信息录入表]对List中的对象字段去重 返回String数组
     *
     * @param communityArchitecture List数据
     * @param colIndex              列类型：表明性别、楼栋、单元、楼层、门牌
     * @return 返回去重好的字段 String数组
     */
    private static String[] getConstraintSet(List<?> communityArchitecture, int colIndex) {
        Set<String> set = new HashSet<>(communityArchitecture.size());
        for (Object object : communityArchitecture) {
            HouseEntity houseEntity = (HouseEntity) object;
            switch (colIndex) {
                //TITLE_FIELD 索引1为性别
                case 1:
                    set.add("男");
                    set.add("女");
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
                    if (StrUtil.isNotEmpty(unit)) {
                        set.add(unit);
                    }
                    break;
                //TITLE_FIELD 索引4为楼层
                case 4:
                    String floor = houseEntity.getFloor();
                    if (StrUtil.isNotEmpty(floor)) {
                        set.add(floor);
                    }
                    break;
                //TITLE_FIELD 索引5为门牌
                case 5:
                    String door = houseEntity.getDoor();
                    if (StrUtil.isNotEmpty(door)) {
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
     * 导入解析、数据验证
     *
     * @param proprietorExcel excel文件
     * @param errorVos        excel导入的相关错误信息集合
     * @return 返回解析好的数据
     */
    public List<ProprietorEntity> importProprietorExcel(MultipartFile proprietorExcel, List<ProprietorVO> errorVos) {
        //最终解析好 正确的 数据集合
        List<ProprietorEntity> userEntityList = new ArrayList<>();
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(proprietorExcel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD, ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //创建一个 Set集合 用于验证房屋编号是否重复输入
            Set<String> houseNumberSet = new HashSet<>(sheetAt.getLastRowNum());
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            ProprietorEntity userEntity;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                userEntity = new ProprietorEntity();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段效验
                    switch (z) {
                        // 1列 验证是否 是一个 正确的中国姓名
                        case 0:
                            if (RegexUtils.isRealName(cellValue)) {
                                userEntity.setRealName(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, " 不是一个正确的中国姓名!");
                                hasError = true;
                            }
                            break;
                        //第2列 验证是否是一个正确的 身份证号
                        case 1:
                            if (RegexUtils.isIdCard(cellValue)) {
                                userEntity.setIdCard(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, " 不是一个正确的身份证号码!");
                                hasError = true;
                            }
                            break;
                        //第3列 电话号码
                        case 2:
                            if (RegexUtils.isMobile(cellValue)) {
                                userEntity.setMobile(cellValue);
                            } else {
                                addResolverError(errorVos, dataRow, " 不是一个正确的电话号码 电信|联通|移动!");
                                hasError = true;
                            }
                            break;
                        //第4列 房屋编号
                        case 3:
                            if (StrUtil.isNotBlank(cellValue) && houseNumberSet.contains(cellValue)) {
                                addResolverError(errorVos, dataRow, " 房屋编号已经被登记存在!");
                                hasError = true;
                            } else {
                                userEntity.setHouseNumber(cellValue);
                                houseNumberSet.add(cellValue);
                            }
                            break;
                        //第5列 微信号码
                        case 4:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isWeChat(cellValue)) {
                                    userEntity.setWechat(cellValue);
                                } else {
                                    addResolverError(errorVos, dataRow, " 微信号是错误的!");
                                    hasError = true;
                                }
                            }
                            break;
                        //第6列 QQ
                        case 5:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isQq(cellValue)) {
                                    userEntity.setQq(cellValue);
                                } else {
                                    addResolverError(errorVos, dataRow, " qq号填写不正确!");
                                    hasError = true;
                                }
                            }
                            break;
                        // 第7列 电子邮箱
                        case 6:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isEmail(cellValue)) {
                                    userEntity.setEmail(cellValue);
                                } else {
                                    addResolverError(errorVos, dataRow, " 邮箱号格式错误!");
                                    hasError = true;
                                }
                            }
                            break;
                        default:
                            break;
                    } //switch-end
                }
                //在这一行数据每一列都效验通过的情况下 才进导入数据库验证集合 的List
                if(!hasError){
                    userEntityList.add(userEntity);
                }
            }
            //如果 错误集合不为空
            return userEntityList;
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }


    /**
     * 为错误对象设置错误msg 便于excel回显
     * 使用realName作为属性字段查找是否有这个对象 如果 有直接返回 没有则创建一个对象返回
     * @param errorList 查找的列表
     * @param realName  真实名称
     * @param errorMsg  错误信息
     * @return 返回列表对象
     */
    public static ProprietorVO setVo(List<ProprietorVO> errorList, String realName, String errorMsg) {
        ProprietorVO resVo = null;
        for (ProprietorVO vo : errorList) {
            if (vo.getRealName().equals(realName)) {
                resVo = vo;
                break;
            }
        }
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        ProprietorVO vo = Optional.ofNullable(resVo).orElseGet(ProprietorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }

    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addResolverError(@NonNull List<ProprietorVO> errorList, @NonNull Row dataRow, String errorMsg) {
        String proprietorRealName = dataRow.getCell(0).getStringCellValue();
        //如果在错误集合里面已经存在这个人的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        ProprietorVO vo = setVo(errorList, proprietorRealName, errorMsg);
        //每一列对象
        Cell cell;
        //每一列对象值
        String stringCellValue;
        //根据当前excel行的业主姓名 验证 在之前的错误集合列表是否 存在该对象 如果存在 则不设置任何属性 只设置备注错误信息
        boolean isExistObj = errorList.stream().anyMatch(vo3 -> proprietorRealName.equals(vo3.getRealName()));
        //存在对象则 设置完Remark 直接return
        if (isExistObj) {
            return;
        }
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ExcelUtil.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    vo.setRealName(stringCellValue);
                    break;
                case 1:
                    vo.setIdCard(stringCellValue);
                    break;
                case 2:
                    vo.setMobile(stringCellValue);
                    break;
                case 3:
                    vo.setHouseNumber(stringCellValue);
                    break;
                case 4:
                    vo.setWechat(stringCellValue);
                    break;
                case 5:
                    vo.setQq(stringCellValue);
                    break;
                case 6:
                    vo.setEmail(stringCellValue);
                    break;
                default:
                    break;
            }
        }
        errorList.add(vo);
    }


}
