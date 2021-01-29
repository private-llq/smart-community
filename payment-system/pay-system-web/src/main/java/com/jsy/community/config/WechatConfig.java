package com.jsy.community.config;

/**
 * @program: wechat-pay
 * @description:  微信支付参数
 * @author: Hu
 * @create: 2021-01-20 11:05
 **/
public class WechatConfig {
    /**
     * 服务号相关信息
     */
    // 企业付款
    public static final String TRANSFERS_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    //服务号的应用号
    public final static String APPID = "wxe84d22f50370bbda";
    //私钥所在路径
    public final static String FILE_NAME = "C:/Users/jsy/Desktop/cert/1605856544_20210120_cert/apiclient_key.pem";
    //商户号
    public final static String MCH_ID = "1605856544";
    //APIKEY 32位密钥
    public final static String PRIVATE_KEY = "zhsj99WfUhf88fjk66fhUFH98FHJzhsj";
    //APIv3 32位密钥
    public final static String API_V3_KEY = "ZongHenShiJiLiuBi666CqZhSjYxGsWc";
    //证书编号
    public final static String MCH_SERIAL_NO = "30F573A0B080D2A1447BAD282E8CF975AC2B4D27";


}
