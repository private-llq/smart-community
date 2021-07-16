package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ILivingPaymentOperationService;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.mapper.WeChatMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  微信支付
 * @author: Hu
 * @create: 2021-01-26 14:26
 **/
@DubboService(version = Const.version, group = Const.group_payment)
public class WeChatServiceImpl extends ServiceImpl<WeChatMapper, WeChatOrderEntity> implements IWeChatService {
    @Autowired
    private WeChatMapper weChatMapper;
    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ILivingPaymentOperationService iLivingPaymentOperationService;

    @Override
    public void insertOrder(WeChatOrderEntity msg) {
        weChatMapper.insert(msg);
    }

    @Override
    public WeChatOrderEntity getOrderOne(String msg) {
        return weChatMapper.selectById(msg);
    }

    @Override
    public void deleteByOrder(String msg) {
        weChatMapper.deleteById(msg);
    }

    /**
     * @Description: 生活缴费订单状态
     * @author: Hu
     * @since: 2021/3/3 14:35
     * @Param:
     * @return:
     */
    @Override
    public void saveStatus(String outTradeNo) {
        iLivingPaymentOperationService.saveStatus(outTradeNo);
    }

    /**
     * @Description: 微信支付订单状态
     * @author: Hu
     * @since: 2021/3/3 14:35
     * @Param:
     * @return:
     */
    @Override
    public void orderStatus(Map<String,String> map) {
        WeChatOrderEntity entity = weChatMapper.selectById(map.get("out_trade_no"));
        if (entity!=null){
            entity.setOrderStatus(2);
            entity.setArriveStatus(2);
            entity.setTransactionId(map.get("transaction_id"));
            weChatMapper.updateById(entity);
        }
    }
}
