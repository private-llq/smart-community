package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 消费类订单支付回调接参
 * @Date: 2021/5/6 11:42
 * @Version: 1.0
 **/
@Data
@ApiModel("消费类订单支付回调接参")
public class ConsumeApplyOrderNotifyQO implements Serializable {

    @ApiModelProperty("电子钱包流水号")
    private String transOrderNo;

    @ApiModelProperty("原交易流水号")
    private String oriTransOrderNo;

    @ApiModelProperty("商户网站唯一订单号")
    private String outTradeNo;

    @ApiModelProperty("商户订单号")
    private String mctOrderNo;

    @ApiModelProperty("商品编号")
    private String goodsId;

    @ApiModelProperty("交易类型,100004 支付；100005 退货；")
    private String transType;

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("商户钱包ID")
    private String merWalletId;

    @ApiModelProperty("支付方式,00：钱包余额支付；01：银行卡支付")
    private String payType;

    @ApiModelProperty("交易金额")
    private String transAmt;

    @ApiModelProperty("订单金额")
    private String orderAmt;

    @ApiModelProperty("商户名称")
    private String merName;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品标题")
    private String subject;

    @ApiModelProperty("交易日期")
    private String transDate;

    @ApiModelProperty("交易时间")
    private String submitTime;

    @ApiModelProperty("保留字段1")
    private String reserver1;

    @ApiModelProperty("保留字段2")
    private String reserver2;

    @ApiModelProperty("签名算法：SHA256WithRSA")
    private String signAlg;

    @ApiModelProperty("签名密文")
    private String sign;
}
