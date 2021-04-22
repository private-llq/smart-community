package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceOrderEntity;

import java.util.List;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    
    /**
    * @Description: 根据收款单号批量查询列表
     * @Param: [receiptNums,query]
     * @Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceOrderEntity>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<PropertyFinanceOrderEntity> queryByReceiptNums(Collection<String> receiptNums,PropertyFinanceOrderEntity query);
    
    /**
    * @Description: 账单号模糊查询收款单号列表
     * @Param: [orderNum]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/22
    **/
    List<String> queryReceiptNumsListByOrderNumLike(String orderNum);
}
