package com.jsy.community.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.jsy.community.constant.ConstClasses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: com.jsy.community
 * @description: 支付宝登录工具类
 * @author: Hu
 * @create: 2021-10-13 14:02
 **/
@Slf4j
@Component
public class AlipayLoginUtils {

    /**
     * 会员信息请求类
     */
    public static AlipayUserInfoShareRequest alipayUserInfoShareRequest =  new  AlipayUserInfoShareRequest();

    //初始化证书请求类
    public static CertAlipayRequest getCert() throws Exception {
        //支付宝证书方式请求
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        //设置网关地址
        certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
        //设置应用Id
        certAlipayRequest.setAppId(ConstClasses.AliPayLoginDataEntity.appid);
        //设置应用私钥
        certAlipayRequest.setPrivateKey(ConstClasses.AliPayLoginDataEntity.privateKey);
        //设置请求格式，固定值json
        certAlipayRequest.setFormat("json");
        //设置字符集
        certAlipayRequest.setCharset("utf-8");
        //设置签名类型
        certAlipayRequest.setSignType("RSA2");
		if(System.getProperty("os.name").startsWith("Win")){
			//设置应用公钥证书路径
			certAlipayRequest.setCertPath("D:/ali_cert/appCertPublicKey_2021002119679359.crt");
			//设置支付宝公钥证书路径
			certAlipayRequest.setAlipayPublicCertPath("D:/ali_cert/alipayCertPublicKey_RSA2.crt");
			//设置支付宝根证书路径
			certAlipayRequest.setRootCertPath("D:/ali_cert/alipayRootCert.crt");
		}else{
        //设置应用公钥证书路径
        certAlipayRequest.setCertPath(ConstClasses.AliPayLoginDataEntity.certPath);
        //设置支付宝公钥证书路径
        certAlipayRequest.setAlipayPublicCertPath(ConstClasses.AliPayLoginDataEntity.alipayPublicCertPath);
        //设置支付宝根证书路径
        certAlipayRequest.setRootCertPath(ConstClasses.AliPayLoginDataEntity.rootCertPath);
		}
        return certAlipayRequest;
    }


    //获取证书请求客户端
    public static AlipayClient getDefaultCertClient(){
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(getCert());
        } catch (AlipayApiException e) {
            System.out.println("支付宝证书请求客户端构建失败");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alipayClient;
    }
}
