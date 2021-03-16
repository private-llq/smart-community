package com.jsy.community.util.excel.impl;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.ProprietorVO;
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
     * @return 返回生成好的excel模板数据流，供控制层直接输出响应excel.xlsx文件
     * @author YuLF
     * @Param map                      key  name = 社区名   key communityUserNum = 社区房间数量
     * @Param entityList               excel列约束  数据集合 楼栋、单元、楼层、门牌
     * @since 2020/11/26 9:50
     */
    public Workbook exportProprietorExcel() {
        //初始excel 表名称 和 表行数数据
        String sheetName = ProprietorExcelCommander.PROPRIETOR_SHEET_NAME;
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(sheetName);
        //获得表头字段列
        String[] titleField = Arrays.copyOf(ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD, ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1) ;
        //创建excel标题行头
        ProprietorExcelCommander.createExcelTitle(workbook, sheet, ProprietorExcelCommander.PROPRIETOR_TITLE_NAME, 380, "宋体", 15, titleField.length);
        //创建excel列字段
        ProprietorExcelCommander.createExcelField(workbook, sheet, titleField);

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
     * @param vos             社区房屋编号 + house_id的编号
     * @return 返回解析好的数据
     */
    public List<UserEntity> importProprietorExcel(MultipartFile proprietorExcel, List<ProprietorVO> vos) {
        //最终解析好 正确的 数据集合
        List<UserEntity> userEntityList = new ArrayList<>();
        //解析错误 | 数据效验不通过 的数据集合  初始化错误集32
        List<ProprietorVO> errorVos = new ArrayList<>(32);
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(proprietorExcel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD, ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1);
            //效验excel标题行
            ProprietorExcelCommander.validExcelField(sheetAt, titleField);
            //创建一个 Set集合 用于验证房屋编号是否重复输入
            Set<String> houseNumberSet = new HashSet<>(sheetAt.getLastRowNum());
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                Row dataRow = sheetAt.getRow(j);
                //如果这行数据不为空 创建一个 实体接收 信息
                UserEntity userEntity = new UserEntity();
                userEntity.setHouseEntity(new HouseEntity());
                for (int z = 0; z < titleField.length; z++) {
                    Cell cell = dataRow.getCell(z);
                    String cellValue = ProprietorExcelCommander.getCellValForType(cell).toString();
                    //列字段效验
                    switch (z) {
                        // 1列 验证是否 是一个 正确的中国姓名
                        case 0:
                            if (RegexUtils.isRealName(cellValue)) {
                                userEntity.setRealName(cellValue);
                            } else {
                                addResolverError(errorVos,dataRow, " 不是一个正确的中国姓名!" );
                            }
                            break;
                        //第2列 验证是否是一个正确的 身份证号
                        case 1:
                            if (RegexUtils.isIdCard(cellValue)) {
                                userEntity.setIdCard(cellValue);
                            } else {
                                addResolverError(errorVos,dataRow, " 不是一个正确的身份证号码!" );
                            }
                            break;
                        //第3列 电话号码
                        case 2:
                            if (RegexUtils.isMobile(cellValue)) {
                                userEntity.setMobile(cellValue);
                            } else {
                                addResolverError(errorVos,dataRow,  " 不是一个正确的电话号码 电信|联通|移动!" );
                            }
                            break;
                        //第4列 房屋编号
                        case 3:
                            if (houseNumberSet.contains(cellValue)) {
                                addResolverError(errorVos,dataRow,  " 房屋编号已经被登记存在!" );
                            } else {
                                userEntity.setNumber(cellValue);
                                houseNumberSet.add(cellValue);
                            }
                            break;
                        //第5列 微信号码
                        case 4:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isWeChat(cellValue)) {
                                    userEntity.setWechat(cellValue);
                                } else {
                                    addResolverError(errorVos,dataRow, " 微信号是错误的!" );
                                }
                            }
                            break;
                        //第6列 QQ
                        case 5:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isQQ(cellValue)) {
                                    userEntity.setQq(cellValue);
                                } else {
                                    addResolverError(errorVos,dataRow, " qq号填写不正确!" );
                                }
                            }
                            break;
                        // 第7列 电子邮箱
                        case 6:
                            if (StrUtil.isNotBlank(cellValue)) {
                                if (RegexUtils.isEmail(cellValue)) {
                                    userEntity.setEmail(cellValue);
                                } else {
                                    addResolverError(errorVos,dataRow, " 邮箱号格式错误!" );
                                }
                            }
                            break;
                        default:
                            break;
                    } //switch-end
                }
                userEntityList.add(userEntity);
            }
            //TODO 验证房屋编号
            //查出该社区所有未登记的房屋编号 + house_id
            return userEntityList;
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }

    /**
     * 把解析验证异常的数据添加至 错误集合
     * @param errorList     错误集合
     * @param dataRow       数据行
     * @param errorMsg      错误备注消息
     */
    private static void addResolverError(@NonNull List<ProprietorVO> errorList,@NonNull Row dataRow, String errorMsg){
        ProprietorVO vo = new ProprietorVO();
        for( int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++ ){
            Cell cell = dataRow.getCell(cellIndex);
            String stringCellValue = cell.getStringCellValue();
            switch (cellIndex){
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
        vo.setRemark(errorMsg);
        errorList.add(vo);
    }


}
