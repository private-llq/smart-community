package com.jsy.community.utils;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @Description: Apache HTTP工具类封装 仅适合流行的json请求和返回
 * @Author: chq459799974
 * @Date: 2020/12/11
**/
public class MyHttpUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(MyHttpUtils.class);
	
	//本地连通性测试
	public static void main(String[] args) throws Exception {
		System.out.println(ping(""));
	}
	public static boolean ping(String ipAddress) throws Exception {
        int  timeOut =  3000 ;  //超时应该在3钞以上        
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }

	//设置Header
	public static void setHeader(HttpRequest httpRequest,Map<String,String> headers) {
//		httpRequest.setHeader("Content-Type", "application/json;charset=utf-8");
//		httpRequest.setHeader("Accept", "application/json");
		//设置params
		if(headers != null){
			for(Map.Entry<String,String> entry : headers.entrySet()){
				httpRequest.setHeader(entry.getKey(),entry.getValue());
			}
		}
	}
	
	//Http请求配置
	public static void setRequestConfig(HttpRequestBase httpRequest){
		httpRequest.setConfig(getDefaultRequestConfig());
	}
	public static void setRequestConfig(HttpRequestBase httpRequest,RequestConfig requestConfig){
		httpRequest.setConfig(requestConfig);
	}
	
	// 默认配置
	private static RequestConfig getDefaultRequestConfig(){
		return RequestConfig.custom().setConnectTimeout(2000)
			.setConnectionRequestTimeout(2000)
			.setSocketTimeout(2000).build();
	}
	
	//构建URL
	private static URIBuilder buildURL(String url){
		URIBuilder builder = null;
		try {
			builder = new URIBuilder(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return builder;
	}
	
	//构建HttpGet
	public static HttpGet httpGet(String url){
		return httpGet(url,null);
	}
	public static HttpGet httpGet(String url, Map<String,String> paramsMap){
		URIBuilder uriBuilder = buildURL(url);
		//设置params
		if(paramsMap != null){
			for(Map.Entry<String,String> entry : paramsMap.entrySet()){
				uriBuilder.setParameter(entry.getKey(),entry.getValue());
			}
		}
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(uriBuilder.build());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return httpGet;
	}
	
	//TODO 构建HttpPost
	
	//获取连接
	public static CloseableHttpClient getConn() {
		return HttpClientBuilder.create().build();
	}
	
	//执行请求，返回结果
	public static String exec(HttpRequestBase httpRequestBase) {
		String httpResult = "";
		HttpResponse response = null;
		try {
			response = getConn().execute(httpRequestBase);
		} catch (Exception e) {
			logger.error("http执行出错", e.getMessage());
			httpRequestBase.abort();
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (HttpStatus.SC_OK == statusCode) {// 如果响应码是 200
			try {
				httpResult = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				logger.error("http解析返回结果出错", e.getMessage());
			}
		} else {
			logger.error("http错误，返回状态码：" + statusCode);
			httpRequestBase.abort();
		}
		logger.info("http请求返回：", httpResult);
		return httpResult;
	}
}
