package com.jsy.community.api;

import com.jsy.community.qo.livingpayment.LivingPaymentQO;
import com.jsy.community.qo.livingpayment.RemarkQO;

/**
 * @program: com.jsy.community
 * @description: 生活缴费service
 * @author: Hu
 * @create: 2020-12-11 09:30
 **/
public interface ILivingPaymentOperationService {

    /**
     * 生活缴费生成订单保存数据
     * @param livingPaymentQO
     * @return
     */
    void add(LivingPaymentQO livingPaymentQO);


    /**
     * @Description: 添加订单备注
     * @author: Hu
     * @since: 2020/12/12 10:15
     * @Param:
     * @return:
     */
    void addRemark(RemarkQO remarkQO);

    /**
     * @Description: 修改订单状态
     * @author: Hu
     * @since: 2021/3/3 10:02
     * @Param:
     * @return:
     */
    void saveStatus(String out_trade_no);
}
