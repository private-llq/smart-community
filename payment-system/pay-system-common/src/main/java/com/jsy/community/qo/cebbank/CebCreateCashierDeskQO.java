package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 创建收银台QO
 * @Date: 2021/11/12 14:52
 * @Version: 1.0
 **/
@Data
public class CebCreateCashierDeskQO extends CebBaseQO {
    // 商户订单号-必填
    private String merOrderNo;

    // 商户订单日期-必填
    // 格式YYYYMMDD
    private String merOrderDate;

    // 支付金额-必填
    // NUMBER(16,2)
    private String payAmount;

    // 缴费项目编号-必填
    private String paymentItemCode;

    // 缴费项目id-必填
    private String paymentItemId;

    // 缴费号码-必填
    private String billKey;

    // 用户标识-必填
    private String sessionId;

    // 账单金额-必填
    private String billAmount;

    // 账单交易码-必填
    private String queryAcqSsn;

    // 客户姓名-必填
    // 从账单获取判断originalCustomerName是否有值，有则取此值，无则取值customerName
    private String customerName;

    // 合同号-必填
    private String contractNo;

    // 备用字段-非必填
    // 为用户输入信息（查询账单时输入的信息，有就上送）
    private String filed1;

    // 应用名称-必填
    // 云缴费（第三方商户调用云缴费客户端收银台的产品名称，商户自行定义参数）
    private String appName;

    // 应用版本-必填
    // 默认1.0.0
    private String appVersion;

    // 跳转地址-必填
    // 支付完成用于前端页面跳转，返回商户页面
    private String redirectUrl;

    // 通知地址-必填
    // 接受云缴费客户端支付结果异步通知回调地址。
    private String notifyUrl;

    // 退款通知地址-非必填
    // 接受云缴费客户端退款结果异步通知回调地址。
    private String refundUrl;

    // 用户选择的账单信息（既通过查询账单接口返回的账单）-必填
    // Json格式的字符串reqdata示例中billQueryResultDataModel字段的值
    private CebBillQueryResultDataModelQO billQueryResultDataModel;

    // 手机充值标记-必填
    // 手机充值该字段必传1。手机充值时可以不用传filed,qryAcnSsn,contractNo、这些5接口没有返回的值
    private String type;

    // 用户当前IP地址-非必填
    private String createIp;
}
