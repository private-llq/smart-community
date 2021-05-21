package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceStatementRecordEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务结算记录表Mapper
 * @Date: 2021/4/22 9:08
 * @Version: 1.0
 **/
public interface PropertyFinanceStatementRecordMapper extends BaseMapper<PropertyFinanceStatementRecordEntity> {

    /**
     *@Author: Pipi
     *@Description: 批量新增操作记录
     *@Param: recordEntities:
     *@Return: java.lang.Integer
     *@Date: 2021/4/24 9:40
     **/
    Integer batchInsert(List<PropertyFinanceStatementRecordEntity> recordEntities);
}
