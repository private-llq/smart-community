package com.jsy.community.config;

/**
 * @Author: Pipi
 * @Description: 光大银行配置
 * @Date: 2021/11/10 14:37
 * @Version: 1.0
 **/
public class CebBankConfig {
    // 商户自己的私钥
    public static String privateKey;
    // 商户自己的公钥
    public static String publicKey;
    // 云缴费客户端分配给对方的接入渠道标识
    public static String siteCode = "aaa";
    // 云缴费客户端接口版本号默认1.0.0
    public static String version = "1.0.0";
    // reqdata编码字符集
    public static String charset = "UTF-8";
}
