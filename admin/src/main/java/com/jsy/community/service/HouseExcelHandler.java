package com.jsy.community.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 房屋Excel助手
 * @Date: 2021/5/18 16:16
 * @Version: 1.0
 **/
public interface HouseExcelHandler {
    /**
     *@Author: DKS
     *@Description: 导出房屋信息表
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/9 14:46
     **/
    Workbook exportHouse(List<?> entityList);
}
