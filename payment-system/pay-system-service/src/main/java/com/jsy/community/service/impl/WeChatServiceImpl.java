package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ILivingPaymentOperationService;
import com.jsy.community.api.IWeChatService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.mapper.WeChatMapper;
import com.jsy.community.qo.payment.WechatRefundQO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
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

    @Override
    public WeChatOrderEntity getSignature(String serviceOrderNo) {
        return weChatMapper.selectOne(new QueryWrapper<WeChatOrderEntity>().eq("service_order_no",serviceOrderNo).eq("pay_type",9).eq("order_status",1).eq("arrive_status",1));
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
    /**
     * @Description: 微信退款订单状态
     * @author: Hu
     * @since: 2021/3/3 14:35
     * @Param:
     * @return:
     */
    @Override
    public void orderRefundStatus(Map<String,String> map) {
        WeChatOrderEntity entity = weChatMapper.selectById(map.get("out_trade_no"));
        if (entity!=null){
            entity.setOrderStatus(3);
            entity.setArriveStatus(3);
            entity.setRefundNum(map.get("refund_id"));
            entity.setRefundTime(LocalDateTime.now());
            weChatMapper.updateById(entity);
        }
    }

    /**
     * @param serviceOrderNo : 外部订单号
     * @author: Pipi
     * @description: 查询微信支付订单ID
     * @return: java.lang.String
     * @date: 2021/9/16 9:58
     **/
    @Override
    public WeChatOrderEntity quereIdByServiceOrderNo(String serviceOrderNo) {
        QueryWrapper<WeChatOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_order_no", serviceOrderNo);
        return weChatMapper.selectOne(queryWrapper);
    }


    /**
     * @Description: 条件查询支付订单
     * @author: Hu
     * @since: 2021/9/18 14:59
     * @Param: [wechatRefundQO]
     * @return: com.jsy.community.entity.payment.WeChatOrderEntity
     */
    @Override
    public WeChatOrderEntity getOrderByQuery(WechatRefundQO wechatRefundQO) {
        return weChatMapper.selectById(wechatRefundQO.getOrderNum());
    }

    /**
     * @param id             : 订单ID
     * @param serviceOrderNo : 外部订单编号
     * @author: Pipi
     * @description: 查询订单支付状态
     * @return: java.lang.Boolean
     * @date: 2021/9/17 17:07
     **/
    @Override
    public Boolean checkPayStatus(String id, String serviceOrderNo) {
        QueryWrapper<WeChatOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_order_no", serviceOrderNo);
        queryWrapper.eq("id", id);
        WeChatOrderEntity weChatOrderEntity = weChatMapper.selectOne(queryWrapper);
        if (weChatOrderEntity != null && weChatOrderEntity.getOrderStatus() == 2) {
            return true;
        }
        return false;
    }
}
