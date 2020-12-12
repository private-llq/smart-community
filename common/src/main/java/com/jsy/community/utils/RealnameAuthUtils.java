package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 实名认证
 * @since 2020-12-11 15:57
 **/
@Component
public class RealnameAuthUtils {
	
	/**
	* @Description: 实名认证 二要素
	 * @Param: [name, idCard]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/11
	**/
	//TODO 三方接口商家待定
	public boolean twoElements(String name,String idCard){
		
		String appCode = "xxxxxxxxxxxxxxxxxxxxx";
		String url = "https://idcert.market.alicloudapi.com/idcard";
		//新建请求，设置参数
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("name",name);
		paramsMap.put("idCard",idCard);
		HttpGet httpGet = MyHttpUtils.httpGet(url,paramsMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + appCode);
		MyHttpUtils.setHeader(httpGet,headers);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpGet);
		//执行请求，返回结果
		String httpResult = MyHttpUtils.exec(httpGet);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
		if(result != null && "01".equals(result.getString("status"))){
			return true;
		}
		return false;
	}
	
}