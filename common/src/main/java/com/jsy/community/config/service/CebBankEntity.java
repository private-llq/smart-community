package com.jsy.community.config.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Pipi
 * @Description: 光大银行配置实体
 * @Date: 2021/11/10 16:05
 * @Version: 1.0
 **/
@Configuration
public class CebBankEntity {
    // 商户自己的私钥
    public static String privateKey;
    // 商户自己的公钥
    public static String publicKey;
    /**
     * 光大银行云缴费平台公钥
     */
    public static String cebBankPublicKey;
    // 接口地址
    public static String requestUrl;
    // 云缴费客户端分配给对方的接入渠道标识
    public static String siteCode;
    // 云缴费客户端接口版本号默认1.0.0
    public static String version;
    // reqdata编码字符集
    public static String charset;

    /**
     * 成功code
     */
    public static String successCode;

    @Value("${cebbank.private_key}")
    public void setPrivateKey(String privateKey) {
        CebBankEntity.privateKey = privateKey;
    }
    @Value("${cebbank.public_key}")
    public void setPublicKey(String publicKey) {
        CebBankEntity.publicKey = publicKey;
    }
    @Value("${cebbank.ceb_bank_public_key}")
    public void setCebBankPublicKey(String cebBankPublicKey) {
        CebBankEntity.cebBankPublicKey = cebBankPublicKey;
    }
    @Value("${cebbank.request_url}")
    public void setRequestUrl(String requestUrl) {
        CebBankEntity.requestUrl = requestUrl;
    }
    @Value("${cebbank.site_code}")
    public void setSiteCode(String siteCode) {
        CebBankEntity.siteCode = siteCode;
    }
    @Value("${cebbank.version}")
    public void setVersion(String version) {
        CebBankEntity.version = version;
    }
    @Value("${cebbank.charset}")
    public void setCharset(String charset) {
        CebBankEntity.charset = charset;
    }
    @Value("${cebbank.success_code}")
    public void setSuccessCode(String successCode) {
        CebBankEntity.successCode = successCode;
    }
}
