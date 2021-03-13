package com.jsy.community.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.jsy.community.constant.ConstClasses;
import org.springframework.stereotype.Component;

/**
 * @author chq459799974
 * @description 支付宝工具类
 * @since 2021-01-12 09:15
 **/
@Component
public class AlipayUtils {
	
	/**
	 * 会员信息请求类
	 */
	public static final AlipayUserInfoShareRequest alipayUserInfoShareRequest =  new  AlipayUserInfoShareRequest();
	
	//appid
	private static final String appid = ConstClasses.AliPayDataEntity.appid;
	//秘钥
	private static final String privateKey = ConstClasses.AliPayDataEntity.privateKey;
	
	//应用公钥证书路径
	private static final String certPath = ConstClasses.AliPayDataEntity.certPath;
	//支付宝公钥证书路径
	private static final String alipayPublicCertPath = ConstClasses.AliPayDataEntity.alipayPublicCertPath;
	//支付宝根证书路径
	private static final String rootCertPath = ConstClasses.AliPayDataEntity.rootCertPath;
	
	//支付宝证书方式请求
	private static final CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
	//初始化证书请求类
	static {
		//设置网关地址
		certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
		//设置应用Id
		certAlipayRequest.setAppId(appid);
		//设置应用私钥
		certAlipayRequest.setPrivateKey(privateKey);
		//设置请求格式，固定值json
		certAlipayRequest.setFormat("json");
		//设置字符集
		certAlipayRequest.setCharset("utf-8");
		//设置签名类型
		certAlipayRequest.setSignType("RSA2");
		//设置应用公钥证书路径
		certAlipayRequest.setCertPath("E:/ali_cert/appCertPublicKey_2021002119679359.crt");
//		certAlipayRequest.setCertPath(certPath);
		//设置支付宝公钥证书路径
		certAlipayRequest.setAlipayPublicCertPath("E:/ali_cert/alipayCertPublicKey_RSA2.crt");
//		certAlipayRequest.setAlipayPublicCertPath(alipayPublicCertPath);
		//设置支付宝根证书路径
		certAlipayRequest.setRootCertPath("E:/ali_cert/alipayRootCert.crt");
//		certAlipayRequest.setRootCertPath(rootCertPath);
	}
	
	//获取证书请求客户端
	public AlipayClient getDefaultCertClient(){
		AlipayClient alipayClient = null;
		try {
			alipayClient = new DefaultAlipayClient(AlipayUtils.certAlipayRequest);
		} catch (AlipayApiException e) {
			System.out.println("支付宝证书请求客户端构建失败");
			e.printStackTrace();
		}
		return alipayClient;
	}
	
}
