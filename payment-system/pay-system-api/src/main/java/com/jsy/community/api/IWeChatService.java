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
}
