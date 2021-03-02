package com.jsy.community.mapper;

import com.jsy.community.qo.livingpayment.RemarkQO;

/**
 * @program: com.jsy.community
 * @description: 生活缴费mapper层
 * @author: Hu
 * @create: 2020-12-11 09:31
 **/
public interface LivingPaymentOperationMapper {
    /**
     * @Description: 添加订单备注
     * @author: Hu
     * @since: 2021/2/20 14:21
     * @Param:
     * @return:
     */
    void addRemark(RemarkQO remarkQO);

}
