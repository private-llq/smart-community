package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.*;

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
    Integer batchUpdateStatementStatusByStatementNum(Set<String> statementNumSet);
    
    /**
    * @Description: 结算单号批量查 单号-结算单数据 映射
     * @Param: [nums]
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    @MapKey("statementNum")
    Map<String,PropertyFinanceStatementEntity> queryByStatementNumBatch(Collection<String> nums);

    /**
     * @Description: 条件查询批量结算单号
     * @Param: [query]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
     **/
    List<String> queryStatementNumsByCondition(@Param("query")PropertyFinanceStatementEntity query);
}
