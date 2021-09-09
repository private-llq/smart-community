package com.jsy.community.util;

import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.vo.property.FinanceImportErrorVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     *@Author: Pipi
     *@Description: 导出收款单主表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/26 10:16
     **/
    Workbook exportMasterReceipt(List<?> entityList);

    /**
     *@Author: Pipi
     *@Description: 导出收款单主从表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/26 11:26
     **/
    Workbook exportMasterSlaveReceipt(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收入
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/19 15:46
     **/
    Workbook exportFinanceForm(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收费报表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 10:31
     **/
    Workbook exportCharge(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 导出收款报表-收款报表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 10:59
     **/
    Workbook exportCollectionForm(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 导出收款报表-账单统计
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 11:12
     **/
    Workbook exportCollectionFormOrder(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 获取历史账单模板
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/9/7 9:32
     **/
    Workbook exportFinanceTemplate();
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/9/7 10:00
     */
   List<PropertyFinanceOrderEntity> importFinanceExcel(MultipartFile excel, List<FinanceImportErrorVO> errorVos);
    
    /**
     *@Author: DKS
     *@Description: 写入充值余额导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/9/7 16:13
     **/
    Workbook exportFinanceOrderErrorExcel(List<FinanceImportErrorVO> errorVos);
    
    /**
     *@Author: DKS
     *@Description: 导出历史账单表
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/9/8 13:41
     **/
    Workbook exportFinance(List<?> entityList);
}
