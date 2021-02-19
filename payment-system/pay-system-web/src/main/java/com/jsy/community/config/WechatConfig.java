package com.jsy.community.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: wechat-pay
 * @description:  微信支付参数
 * @author: Hu
 * @create: 2021-01-20 11:05
 **/
@Component
public class WechatConfig {
    // 企业付款
    public static final String TRANSFERS_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    //企业查询
    public static final String QUERY_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo";
    //支付查询
    public static final String WXPAY_PAY = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";
    //服务号的应用号
    public static String APPID;
    //私钥所在路径
    public static String APICLIENT_KEY;
    //apiclient_cert.p12所在路劲
    public static String APICLIENT_CRET;
    //商户号
    public static String MCH_ID;
    //APIKEY 32位密钥
    public static String PRIVATE_KEY;
    //APIv3 32位密钥
    public static String API_V3_KEY;
    //证书编号
    public static String MCH_SERIAL_NO;


    @Value("${wechat.app_id}")
    public void setAPPID(String APPID) {
        WechatConfig.APPID = APPID;
    }
    @Value("${wechat.apiclient_key}")
    public void setApiclientKey(String apiclientKey) {
        WechatConfig.APICLIENT_KEY = apiclientKey;
    }
    @Value("${wechat.apiclient_cret}")
    public void setApiclientCret(String apiclientCret) {
        WechatConfig.APICLIENT_CRET = apiclientCret;
    }
    @Value("${wechat.mch_id}")
    public void setMchId(String mchId) {
        WechatConfig.MCH_ID = mchId;
    }
    @Value("${wechat.private_key}")
    public void setPrivateKey(String privateKey) {
        WechatConfig.PRIVATE_KEY = privateKey;
    }
    @Value("${wechat.private_v3_key}")
    public void setApiV3Key(String apiV3Key) {
        WechatConfig.API_V3_KEY = apiV3Key;
    }
    @Value("${wechat.mch_serial_no}")
    public void setMchSerialNo(String mchSerialNo) {
        WechatConfig.MCH_SERIAL_NO = mchSerialNo;
    }
}
