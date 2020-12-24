package com.jsy.community.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(WeatherUtils.class);
	
	//天气实况
	public JSONObject getWeatherNow(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/condition");
	}
	//天气预报
	public JSONObject getWeatherForDays(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/forecast15days");
	}
	
	//TODO 三方接口商家待定 暂时用墨迹天气
	public JSONObject getWeather(String lon,String lat,String url){
		
		String appCode = "xxxxxxxxxxxxxxxxxx";
		appCode = "17e38ac209824aab9d0e82097f59ba11";
//		String url = "http://aliv8.data.moji.com/whapi/json/aliweather/condition";
		//params参数
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("lon", lon);
		paramsMap.put("lat", lat);
		HttpPost httpPost = MyHttpUtils.httpPostWithoutBody(url,paramsMap);
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
		if(result != null && "0".equals(result.getString("code"))){
			try{
				JSONObject data = result.getJSONObject("data");
				return data;
			}catch (Exception e){
				logger.error("获取天气data出错：" + result);
				e.printStackTrace();
			}
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		getWeatherNow("106.514787","29.622701");
//	}
}
