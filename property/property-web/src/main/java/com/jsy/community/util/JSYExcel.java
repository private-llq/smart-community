package com.jsy.community.util;

import com.jsy.community.entity.UserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-11-26 14:59
 * Excel功能指定
 */
public interface JSYExcel {

    /**
     * 对业主的Excel导入进行处理
     * @param excel     业主信息表.xlsx
     * @return          返回解析好的 List<Entity> 数据
     */
    <T> List<UserEntity> importProprietorExcel(MultipartFile excel);

    /**
     * 导出业主Excel模板
     * @param entityList        实体List
     * @param workSheetName     工作表名称
     * @return                  返回生成好的工作簿
     */
     Workbook exportProprietorExcel(List<?> entityList, String workSheetName);

}
