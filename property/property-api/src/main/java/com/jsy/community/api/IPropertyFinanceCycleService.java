package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceCycleEntity;

import java.util.List;

/**
 * @Author: Pipi
 * @Description: 物业财务结算周期表服务
 * @Date: 2021/4/22 9:28
 * @Version: 1.0
 **/
public interface IPropertyFinanceCycleService extends IService<PropertyFinanceCycleEntity> {

    /**
     *@Author: Pipi
     *@Description: 根据号数获取需要结算的社区ID列表
     *@Param: date: 号数
     *@Return: java.util.List<java.lang.Long>
     *@Date: 2021/4/22 10:01
     **/
    List<PropertyFinanceCycleEntity> needStatementCommunityId(Integer date);
}
