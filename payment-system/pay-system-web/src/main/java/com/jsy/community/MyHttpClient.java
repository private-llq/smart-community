package com.jsy.community;

import com.jsy.community.config.WechatConfig;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-27 11:24
 **/

public class MyHttpClient {
    private static CloseableHttpClient httpClient;
    public static CloseableHttpClient createHttpClient(){
        PrivateKey merchantPrivateKey = null;
        try {
            merchantPrivateKey = PemUtil
                    .loadPrivateKey(new ByteArrayInputStream(WechatConfig.API_KEY.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3秘钥）
        AutoUpdateCertificatesVerifier verifier = null;
        try {
            verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(WechatConfig.MCH_ID, new PrivateKeySigner(WechatConfig.MCH_SERIAL_NO, merchantPrivateKey)),WechatConfig.API_V3_KEY.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 初始化httpClient
        return httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(WechatConfig.MCH_ID, WechatConfig.MCH_SERIAL_NO, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
    }

    public static void closeHttp(CloseableHttpClient httpClient){
        if (httpClient!=null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
