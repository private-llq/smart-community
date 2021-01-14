package com.jsy.community.util;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author YuLF
 * @since 2020-11-26 14:59
 * Excel导入导出
 */
public interface ExcelHandler {

    /**
     * 对业主的Excel导入进行处理
     * @param excel     业主信息表.xlsx
     * @param map       需要携带的传递参数
     * @return          返回解析好的 List<Entity> 数据
     */
    List<?> importProprietorExcel(MultipartFile excel, Map<String, Object> map);

    /**
     * 导出业主Excel模板
     * @param entityList        实体List
     * @param res               存放实现类需要传递的数据
     * @return                  返回生成好的工作簿
     */
     Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> res);

}
