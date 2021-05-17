package com.jsy.community.qo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 消费类订单支付回调接参
 * @Date: 2021/5/6 11:42
 * @Version: 1.0
 **/
@Data
public class ConsumeApplyOrderNotifyQO implements Serializable {

    // 电子钱包流水号
    private String transOrderNo;

    // 原交易流水号
    private String oriTransOrderNo;

    // 商户网站唯一订单号
    private String outTradeNo;

    // 商户订单号
    private String mctOrderNo;

    // 商品编号
    private String goodsId;

    // 交易类型,100004 支付；100005 退货；
    private String transType;

    // 钱包ID
    private String walletId;

    // 钱包名称
    private String walletName;

    // 商户钱包ID
    private String merWalletId;

    // 支付方式,00：钱包余额支付；01：银行卡支付
    private String payType;

    // 交易金额
    private String transAmt;

    // 订单金额
    private String orderAmt;

    // 商户名称
    private String merName;

    // 商品名称
    private String goodsName;

    // 商品标题
    private String subject;

    // 交易日期
    private String transDate;

    // 交易时间
    private String submitTime;

    // 保留字段1
    private String reserver1;

    // 保留字段2
    private String reserver2;

    // 签名算法：SHA256WithRSA
    private String signAlg;

    // 签名密文
    private String sign;
}
