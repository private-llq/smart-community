package com.jsy.community.util;

import com.jsy.community.entity.UserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
     * @param res               其中的key name = 社区名称  communityUserNum = 社区房间数量   这两个值为了在excel中设置 工作表名称 和约束行数 = communityUserNum
     * @return                  返回生成好的工作簿
     */
     Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> res);

}
