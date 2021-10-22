package com.jsy.community.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Author: DKS
 * @Description: 房屋Excel助手
 * @Date: 2021/10/22 10:16
 * @Version: 1.0
 **/
public interface HouseExcelHandler {
    /**
     *@Author: DKS
     *@Description: 导出房屋信息表
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/10/22 12:56
     **/
    Workbook exportHouse(List<?> entityList);
    
    /**
     *@Author: DKS
     *@Description: 导出住户信息表
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/10/22 15:59
     **/
    Workbook exportHouseMember(List<?> entityList);
}
