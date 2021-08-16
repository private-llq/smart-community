package com.jsy.community.util;

import com.jsy.community.entity.property.PropertyAdvanceDepositEntity;
import com.jsy.community.vo.property.AdvanceDepositImportErrorVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *@Author: DKS
 *@Description: 预存款excel助手
 *@Date: 2021/8/10 9:10
 **/
public interface AdvanceDepositExcelHandler {
    
    /**
     *@Author: Pipi
     *@Description: 获取充值余额导入模板
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/13 14:19
     **/
    Workbook exportAdvanceDepositTemplate();

    /**
     * @Author: Pipi
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/5/19 11:47
     **/
    List<PropertyAdvanceDepositEntity> importAdvanceDepositExcel(MultipartFile excel, List<AdvanceDepositImportErrorVO> errorVos);
    
    /**
     *@Author: DKS
     *@Description: 写入充值余额导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/8/16 13:50
     **/
    Workbook exportAdvanceDepositErrorExcel(List<AdvanceDepositImportErrorVO> errorVos);
}
