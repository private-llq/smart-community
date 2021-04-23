package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;

import java.util.HashSet;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算单Mapper
 * @Date: 2021/4/22 9:02
 * @Version: 1.0
 **/
public interface PropertyFinanceStatementMapper extends BaseMapper<PropertyFinanceStatementEntity> {
    /**
     *@Author: Pipi
     *@Description: 批量更新被驳回的结算单状态
     *@Param: statementNumSet:
     *@Return: java.lang.Integer
     *@Date: 2021/4/22 17:48
     **/
    Integer batchUpdateStatementStatusByStatementNum(HashSet<String> statementNumSet);
}
