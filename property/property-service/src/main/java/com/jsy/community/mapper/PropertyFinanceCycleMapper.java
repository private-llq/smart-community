package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceCycleEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务结算周期表Mapper
 * @Date: 2021/4/22 9:12
 * @Version: 1.0
 **/
public interface PropertyFinanceCycleMapper extends BaseMapper<PropertyFinanceCycleEntity> {

    /**
     *@Author: Pipi
     *@Description: 根据号数获取需要结算的社区ID列表
     *@Param: startDate:
     *@Return: java.util.List<java.lang.Long>
     *@Date: 2021/4/22 10:04
     **/
    List<Long> queryCommunityIdByStartDate(Integer startDate);
}
