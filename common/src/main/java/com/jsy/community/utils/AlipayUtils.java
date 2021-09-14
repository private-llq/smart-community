package com.jsy.community.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.jsy.community.constant.ConstClasses;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.InputStream;

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
	public static  AlipayUserInfoShareRequest alipayUserInfoShareRequest =  new  AlipayUserInfoShareRequest();
	
	//初始化证书请求类
	public static CertAlipayRequest getCert() throws Exception {
		//支付宝证书方式请求
		CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
		//设置网关地址
		certAlipayRequest.setServerUrl("https://openapi.alipay.com/gateway.do");
		//设置应用Id
		certAlipayRequest.setAppId(ConstClasses.AliPayDataEntity.appid);
		//设置应用私钥
		certAlipayRequest.setPrivateKey(ConstClasses.AliPayDataEntity.privateKey);
		//设置请求格式，固定值json
		certAlipayRequest.setFormat("json");
		//设置字符集
		certAlipayRequest.setCharset("utf-8");
		//设置签名类型
		certAlipayRequest.setSignType("RSA2");
//		if(System.getProperty("os.name").startsWith("Win")){
//			//设置应用公钥证书路径
//			certAlipayRequest.setCertPath("D:/ali_cert/appCertPublicKey_2021002119679359.crt");
//			//设置支付宝公钥证书路径
//			certAlipayRequest.setAlipayPublicCertPath("D:/ali_cert/alipayCertPublicKey_RSA2.crt");
//			//设置支付宝根证书路径
//			certAlipayRequest.setRootCertPath("D:/ali_cert/alipayRootCert.crt");
//		}else{
			//设置应用公钥证书路径
			certAlipayRequest.setCertContent(getPrivateKey(ConstClasses.AliPayDataEntity.certPath));
			//设置支付宝公钥证书路径
			certAlipayRequest.setAlipayPublicCertContent(getPrivateKey(ConstClasses.AliPayDataEntity.alipayPublicCertPath));
			//设置支付宝根证书路径
			certAlipayRequest.setRootCertContent(getPrivateKey(ConstClasses.AliPayDataEntity.rootCertPath));
//		}
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
	
	/**
	 * 下载文件，返回输入流。
	 *
	 * @param apiUrl api接口
	 * @return (文件)输入流
	 * @throws Exception
	 */
	public static InputStream getStreamDownloadOutFile(String apiUrl) throws Exception {
		InputStream is = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();//创建默认http客户端
		RequestConfig requestConfig= RequestConfig.DEFAULT;//采用默认请求配置
		HttpGet request = new HttpGet(apiUrl);//通过get方法下载文件流
		request.setConfig(requestConfig);//设置请头求配置
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(request);//执行请求，接收返回信息
			int statusCode = httpResponse.getStatusLine().getStatusCode();//获取执行状态
			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
				request.abort();
			} else {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity) {
					is = entity.getContent();//获取返回内容
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.abort();
		}
		return is;
	}
	
	/**
	 * 获取私钥。
	 *
	 * @param url 私钥文件路径  (required)
	 * @return 私钥对象
	 */
	public static String getPrivateKey(String url) throws Exception {
		return IOUtils.toString(getStreamDownloadOutFile(url));
	}
	
}
