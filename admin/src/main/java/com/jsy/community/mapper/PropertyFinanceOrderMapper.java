package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description:  物业缴费账单
 * @author: DKS
 * @create: 2021-11-09 12:00
 **/
@Mapper
public interface PropertyFinanceOrderMapper extends BaseMapper<PropertyFinanceOrderEntity> {
    
    /**
     *@Author: DKS
     *@Description: 根据时间段查询物业费用总计
     *@Param: startTime,endTime:
     *@Date: 2021/11/09 13:47
     **/
    BigDecimal financeTurnover(LocalDate startTime, LocalDate endTime);
}
