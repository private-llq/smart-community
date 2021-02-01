package com.jsy.community.utils;

import com.jsy.community.config.PublicConfig;
import com.jsy.community.config.WechatConfig;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @program: com.jsy.community
 * @description:  构建微信支付请求
 * @author: Hu
 * @create: 2021-01-27 11:24
 **/

public class MyHttpClient {
    private static CloseableHttpClient httpClient;
    public static CloseableHttpClient createHttpClient(){
        PrivateKey merchantPrivateKey = null;
        try {
            merchantPrivateKey = PublicConfig.getPrivateKey(WechatConfig.FILE_NAME);

        } catch (IOException e) {
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


    /**
     * @Description: 带证书请求
     * @author: Hu
     * @since: 2021/1/29 17:32
     * @Param:
     * @return:
     */
    public static CloseableHttpClient getSSLConnectionSocket() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        FileInputStream instream = null;//加载本地的证书进行https加密传输
        try {
            instream = new FileInputStream(new File("C:/Users/jsy/Desktop/cert/1605856544_20210120_cert/apiclient_cert.p12"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            try {
                keyStore.load(instream, WechatConfig.MCH_ID.toCharArray());//设置证书密码
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }	// Trust own CA and all self-signed certs
        SSLContext sslcontext = null;	// Allow TLSv1 protocol only
        try {
            sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, WechatConfig.MCH_ID.toCharArray())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,			new String[]{"TLSv1"},			null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }




}
