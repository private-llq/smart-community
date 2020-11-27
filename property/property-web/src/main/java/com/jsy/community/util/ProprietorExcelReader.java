package com.jsy.community.util;

import com.jsy.community.constant.ConstError;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-11-26 14:53
 * 业主信息录入表.xlsx 导入解析 实现类
 */
@Slf4j
public class ProprietorExcelReader extends JSYExcelAbstract {

    @Override
    public List<UserEntity> importProprietorExcel(MultipartFile proprietorExcel) {
        List<UserEntity> userEntityList = new ArrayList<>();
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(proprietorExcel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //效验Sheet
            //效验工作表头字段
            Row row = sheetAt.getRow(1);
            if (row == null) {
                throw new JSYException(JSYError.BAD_REQUEST.getCode(), "excel文件信息无效!请重新下载模板");
            }
            String[] titleField = ProprietorExcelCommander.TITLE_FIELD;
            for (int i = 0; i < titleField.length; i++) {
                Cell cell = row.getCell(i);
                //如果标题 字段 列为空 或者 标题列字段 和 titleField 里面对应的下标 列内容不匹配 则抛出异常
                if (cell == null || !row.getCell(i).getStringCellValue().equals(titleField[i])) {
                    throw new JSYException(ConstError.NORMAL, "字段匹配错误：预期第" + i + "列字段是" + titleField[i] + "，但发现的是：" + row.getCell(i).getStringCellValue());
                }
            }
            int lastRowNum = sheetAt.getLastRowNum();
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                Row dataRow = sheetAt.getRow(j);
                if (dataRow != null) {
                    //如果这行数据不为空 创建一个 实体接收 信息
                    UserEntity userEntity = new UserEntity();
                    userEntity.setHouseEntity(new HouseEntity());
                    //遍历列
                    //dataRow.getLastCellNum()避免有的列为空，所以 需要检查 9个列的字段 titleField.length
                    for (int z = 0; z < titleField.length; z++) {
                        Cell cell = dataRow.getCell(z);
                        String CellValue = getCellValForType(cell).toString();
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
                                if (StringUtils.isNoneBlank(CellValue)) {
                                    userEntity.setDetailAddress(CellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列未填写您的详细地址!");
                                }
                                break;
                            default:
                                break;
                        } //switch-end
                    }
                    userEntityList.add(userEntity);
                }
            }
            return userEntityList;
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }
    /**
     *  根据 excel单元格的类型获取值
     * @author YuLF
     * @since  2020/11/27 9:31
     * @Param       cell   每一列单元格
     * @return      返回单元格的值
     */
    public static Object getCellValForType(Cell cell)
    {
        Object CellValue = StringUtils.EMPTY;
        if(cell != null){
            CellType cellType = cell.getCellType();
            switch (cellType){
                case NUMERIC: //数字
                    //如果是日期类型
                    if (DateUtil.isCellDateFormatted(cell)){
                        CellValue =   cell.getDateCellValue();
                    }else{
                        //避免poi读入手机号 自动变为 科学计数
                        NumberFormat f=new DecimalFormat("############");
                        f.setMaximumFractionDigits(0);
                        CellValue= f.format(cell.getNumericCellValue());
                    }
                    break;
                case STRING: //字符串
                    CellValue =   cell.getStringCellValue();
                    break;
                case BOOLEAN: //Boolean
                    CellValue =  cell.getBooleanCellValue();
                    break;
                case ERROR: //故障
                    break;
                default:
                    break;
            }
        }
        return CellValue;
    }
}
