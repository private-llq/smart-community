package com.jsy.community.util;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 财务Excel助手
 * @Date: 2021/4/24 15:04
 * @Version: 1.0
 **/
public interface FinanceExcelHandler {

    /**
     *@Author: Pipi
     *@Description: 导出结算单主表
     *@Param: entityList: 实体List
     *@Param: res: 存放实现类需要传递的数据
     *@Return: org.apache.poi.ss.usermodel.Workbook 返回生成好的工作簿
     *@Date: 2021/4/24 15:07
     **/
    Workbook exportMaterStatement(List<?> entityList);

    /**
     *@Author: Pipi
     *@Description: 导出结算单主表和从表
     *@Param: entityList: 实体List
     *@Param: res: 存放实现类需要传递的数据
     *@Return: org.apache.poi.ss.usermodel.Workbook 返回生成好的工作簿
     *@Date: 2021/4/24 15:09
     **/
    Workbook exportMasterSlaveStatement(List<?> entityList);

    /**
     *@Author: Pipi
     *@Description: 导出账单表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/25 15:46
     **/
    Workbook exportMaterOrder(List<?> entityList);
}
