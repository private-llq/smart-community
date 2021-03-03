package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.payment.WeChatOrderEntity;

/**
 * @program: com.jsy.community
 * @description:  微信支付
 * @author: Hu
 * @create: 2021-01-26 14:23
 **/
public interface IWeChatService extends IService<WeChatOrderEntity> {
    void insertOrder(WeChatOrderEntity msg);

    WeChatOrderEntity getOrderOne(String msg);

    void deleteByOrder(String msg);

    WeChatOrderEntity saveOrder(String orderId);

    /**
     * @Description: 生活缴费修改订单状态
     * @author: Hu
     * @since: 2021/3/3 9:59
     * @Param:
     * @return:
     */
    void saveStatus(String out_trade_no);

    /**
     * @Description: 微信订单状态
     * @author: Hu
     * @since: 2021/3/3 14:32
     * @Param:
     * @return:
     */
    void orderStatus(String out_trade_no);
}
