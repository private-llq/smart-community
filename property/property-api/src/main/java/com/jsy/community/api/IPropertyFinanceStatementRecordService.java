package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import com.jsy.community.entity.property.PropertyFinanceStatementRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算操作记录服务
 * @Date: 2021/4/24 10:38
 * @Version: 1.0
 **/
public interface IPropertyFinanceStatementRecordService extends IService<PropertyFinanceStatementRecordEntity> {
    /**
     *@Author: Pipi
     *@Description: 根据结算单号查询操作记录列表
     *@Param: statementNum:
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceStatementRecordEntity>
     *@Date: 2021/4/24 10:50
     **/
    PropertyFinanceStatementEntity statementRecordList(String statementNum);
}
