package com.jsy.community.constant;

/**
 * @Author: Pipi
 * @Description: 光大银行常量
 * @Date: 2021/11/10 15:05
 * @Version: 1.0
 **/
public interface CebBankConst {
    // 注册接口交易名
    String LOGIN = "YAOYAOAPP_INTERFACE_AUTOLOGINBYCANAL";
    // 查询城市接口交易名
    String QUERY_CITY = "YAOYAOAPP_INTERFACE_QUERYCITYS";
    // 查询城市下缴费类别接口交易名
    String QUERY_CITY_CONTRIBUTION_CATEGORY = "YAOYAOAPP_INTERFACE_QUERYCATEGORYTYPES";
    // 查询缴费类别下缴费项目接口交易名
    String QUERY_CONTRIBUTION_PROJECT = "YAOYAOAPP_INTERFACE_QUERYPAYITEMS";
    // 查询缴费账单信息接口交易名
    String QUERY_BILL_INFO = "YAOYAOAPP_INTERFACE_QUERYPAYBILLINFO";
    // 查询手机充值缴费账单接口交易名
    String QUERY_MOBILE_BILL = "YAOYAOAPP_INTERFACE_QUERYMOBILEPAYBILLINFO";
    // 创建收银台接口交易名
    String CREATE_CASHIER_DESK = "TMRI_ORDER_CREATECASHIER";
    // 查询缴费记录接口交易名
    String QUERY_CONTRIBUTION_RECORD = "TMRI_ORDER_QUERYPAYRECORD_HK";
    // 支付结果异步推送交易名
    String PAY_CALLBACK = "TMRI_ORDER_RESULT";
    // 查询缴费记录详情接口交易名
    String QUERY_CONTRIBUTION_RECORD_INFO = "TMRI_ORDER_QUERYPAYRECORD_HKORDERS_INFO";

    // 1-PC个人电脑
    String DEVICE_TYPE_PC = "1";
    // 2-手机终端
    String DEVICE_TYPE_MOBILE = "2";
    // 3-微信公众号
    String DEVICE_TYPE_WECHAT_PUBLIC = "3";
    // 4-支付宝
    String DEVICE_TYPE_ALIPAY = "4";
    // 5-微信小程序
    String DEVICE_TYPE_UNIAPP = "5";
}
