package com.jsy.community.service;

import com.jsy.community.entity.HouseBuildingTypeEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.vo.property.BuildingImportErrorVO;
import com.jsy.community.vo.property.HouseImportErrorVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 房屋Excel助手
 * @Date: 2021/5/18 16:16
 * @Version: 1.0
 **/
public interface HouseExcelHandler {
    
    /**
     *@Author: Pipi
     *@Description: 获取房屋模板
     *@Param: : 
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/5/18 16:19
     **/
    Workbook exportHouseTemplate();

    /**
     * @Author: Pipi
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/5/19 11:47
     **/
    List<HouseEntity> importHouseExcel(MultipartFile excel, List<HouseImportErrorVO> errorVos);
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/8/10 10:47
     **/
    List<HouseEntity> importBuildingExcel(MultipartFile excel, List<BuildingImportErrorVO> errorVos);

    /**
     *@Author: Pipi
     *@Description: 导出上传时的错误信息
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/5/21 17:51
     **/
    Workbook exportErrorExcel(List<HouseImportErrorVO> errorVos);
    
    /**
     *@Author: DKS
     *@Description: 导出楼栋上传时的错误信息
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/10 10:51
     **/
    Workbook exportBuildingErrorExcel(List<BuildingImportErrorVO> errorVos);
    
    /**
     *@Author: DKS
     *@Description: 导出房屋信息表
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/9 14:46
     **/
    Workbook exportHouse(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 下载楼栋信息导入模板
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/10 8:19
     **/
    Workbook exportBuildingTemplate(List<HouseBuildingTypeEntity> houseBuildingTypeEntities);
}
