package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.payment.WeChatOrderEntity;

import java.util.Map;

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

//    WeChatOrderEntity saveOrder(String orderId);

    /**
     * @Description: 生活缴费修改订单状态
     * @author: Hu
     * @since: 2021/3/3 9:59
     * @Param:
     * @return:
     */
    void saveStatus(String outTradeNo);

    /**
     * @Description: 微信订单状态
     * @author: Hu
     * @since: 2021/3/3 14:32
     * @Param:
     * @return:
     */
    void orderStatus(Map<String,String> map);

    /**
     * @author: Pipi
     * @description: 查询微信支付订单ID
     * @param serviceOrderNo: 外部订单号
     * @return: java.lang.String
     * @date: 2021/9/16 9:58
     **/
    WeChatOrderEntity quereIdByServiceOrderNo(String serviceOrderNo);
}
