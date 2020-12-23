package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 调用三方天气接口
 * @since 2020-12-23 18:02
 **/
@Component
public class WeatherUtils {
	
	//TODO 三方接口商家待定 暂时用墨迹天气
	public static boolean getWeatherNow(String lon,String lat){
		
		String appCode = "xxxxxxxxxxxxxxxxxx";
		String url = "http://aliv8.data.moji.com/whapi/json/aliweather/condition";
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("lon", lon);
		paramsMap.put("lat", lat);
//		paramsMap.put("token", "c712899b393c7b262dd7984f6eb52657");
		//设置body参数
		Map<String, String> bodyMap = new HashMap<>();
//		bodyMap.put("lon", "116.403874");
//		bodyMap.put("lat", "39.9148801");
//		bodyMap.put("token", "c712899b393c7b262dd7984f6eb52657");
		HttpPost httpPost = MyHttpUtils.httpPost(url,paramsMap,null);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + appCode);
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//		headers.put("Content-Type", "application/json");
		MyHttpUtils.setHeader(httpPost,headers);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		//执行请求，返回结果
		String httpResult = MyHttpUtils.exec(httpPost);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
		System.out.println(result);
//		if(result != null && "01".equals(result.getString("status"))){
//			return true;
//		}
		return false;
	}
	
	public static void main(String[] args) {
		getWeatherNow("106.514787","29.622701");
	}
}
