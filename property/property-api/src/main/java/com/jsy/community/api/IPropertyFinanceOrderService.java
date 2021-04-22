package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description:  物业房间账单
 * @author: Hu
 * @create: 2021-04-20 16:28
 **/
public interface IPropertyFinanceOrderService extends IService<PropertyFinanceOrderEntity> {
    void updateDays();

    /**
     *@Author: Pipi
     *@Description: 获取上一个月的需要结算和被驳回的账单
     *@Param: communityIdS: 社区ID列表
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     *@Date: 2021/4/22 10:24
     **/
    List<PropertyFinanceOrderEntity> getNeedStatementOrderList(List<Long> communityIdS);
}
