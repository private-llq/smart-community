package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CarOrderEntity;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarOrderQO;

import java.util.Map;

public interface ICarTemporaryOrderService extends IService<CarOrderEntity> {
    /**
     * @Description: 查询临时订单
     * @Param: [baseQO]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CarOrderEntity>
     * @Author: Tian
     * @Date: 2021/9/7-9:22
     **/
    Page<CarOrderEntity> selectCarOrder(BaseQO<CarOrderQO> baseQO);
    /**
     * @Description: 今日订单数和收入金额
     * @Param: []
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Author: Tian
     * @Date: 2021/9/7-11:02
     **/
    Map<String, Object> selectMoney();
}
