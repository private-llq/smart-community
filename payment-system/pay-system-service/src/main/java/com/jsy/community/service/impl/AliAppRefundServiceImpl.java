package com.jsy.community.service.impl;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IAliAppRefundService;
import com.jsy.community.api.IPayConfigureService;
import com.jsy.community.api.PaymentException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.mapper.AiliAppPayRecordDao;
import com.jsy.community.utils.AlipayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: com.jsy.community
 * @description: 支付宝退款
 * @author: Hu
 * @create: 2021-12-15 16:03
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_payment)
public class AliAppRefundServiceImpl implements IAliAppRefundService {


    @Autowired
    private AlipayUtils alipayUtils;

    @Autowired
    private AiliAppPayRecordDao ailiAppPayRecordDao;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPayConfigureService payConfigureService;

    /**
     * @Description: 单查详情
     * @author: Hu
     * @since: 2021/12/15 16:10
     * @Param: [orderNo]
     * @return: com.jsy.community.entity.lease.AiliAppPayRecordEntity
     */
    @Override
    public AiliAppPayRecordEntity selectByOrder(String orderNo) {
        return ailiAppPayRecordDao.selectOne(new QueryWrapper<AiliAppPayRecordEntity>().eq("order_no",orderNo));
    }



    /**
     * @Description: 退款
     * @author: Hu
     * @since: 2021/12/15 16:16
     * @Param: [orderNo]
     * @return: void
     */
    @Override
    public void refund(AiliAppPayRecordEntity ailiAppPayRecordEntity) {
        PayConfigureEntity entity = payConfigureService.getCompanyConfig(Long.parseLong(ailiAppPayRecordEntity.getCompanyId()));
        if (entity != null) {
            ConstClasses.AliPayDataEntity.setConfig(entity);

            AlipayClient client = AlipayUtils.getDefaultCertClient();

            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no",ailiAppPayRecordEntity.getOrderNo());
            bizContent.put("refund_amount", ailiAppPayRecordEntity.getTradeAmount());
            request.setBizContent(bizContent.toString());
            AlipayTradeRefundResponse response = null;
            try {
                response = client.certificateExecute(request);
                log.info("订单{}退款响应信息:{}", ailiAppPayRecordEntity.getOrderNo(), response);
                if (response.isSuccess()){
                    log.info("订单:{}退款成功", ailiAppPayRecordEntity.getOrderNo());

                    if (response.getMsg().equals("Success")) {
                        AiliAppPayRecordEntity payRecordEntity = ailiAppPayRecordDao.selectOne(new QueryWrapper<AiliAppPayRecordEntity>().eq("order_no", response.getOutTradeNo()));
                        if (payRecordEntity != null) {
                            payRecordEntity.setTradeStatus(3);
                            ailiAppPayRecordDao.updateById(payRecordEntity);
                        } else {
                            throw new PaymentException("系统错误！");
                        }
                    } else {
                        throw new PaymentException("退款失败！");
                    }

                }else {
                    log.info("订单:{}退款失败", ailiAppPayRecordEntity.getOrderNo());
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        } else {
            throw new PaymentException("系统错误！");
        }
    }
}
