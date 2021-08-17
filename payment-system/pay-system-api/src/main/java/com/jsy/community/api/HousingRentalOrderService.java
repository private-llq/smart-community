package com.jsy.community.api;

import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 房屋租赁订单服务
 * @Date: 2021/8/16 16:59
 * @Version: 1.0
 **/
public interface HousingRentalOrderService {

    /**
     * @author: Pipi
     * @description: 支付完成之后修改租赁端订单支付状态
     * @param orderNo: 支付系统订单编号
     * @param housingContractOderNo: 租赁系统合同编号
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @date: 2021/8/16 17:05
     **/
    Map<String, Object> completeLeasingOrder(String orderNo, String housingContractOderNo);
}
