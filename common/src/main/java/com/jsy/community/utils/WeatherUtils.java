package com.jsy.community.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.constant.BusinessEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chq459799974
 * @description 调用三方天气接口
 * @since 2020-12-23 18:02
 **/
@Slf4j
@Component
public class WeatherUtils {
	
	//天气实况
	public JSONObject getWeatherNow(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/condition");
	}
	//天气预报15天
	public JSONObject getWeatherForDays(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/forecast15days");
	}
	//天气预报24小时
	public JSONObject getWeatherFor24hours(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/forecast24hours");
	}
	//空气质量
	public JSONObject getAirQuality(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/aqi");
	}
	//生活指数
	public JSONObject getLivingIndex(String lon,String lat){
		return getWeather(lon,lat,"http://aliv8.data.moji.com/whapi/json/aliweather/index");
	}
	
	//TODO 三方接口商家待定 暂时用墨迹天气
	public JSONObject getWeather(String lon,String lat,String url){
		
		String appCode = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		//params参数
		Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("lon", lon);
		paramsMap.put("lat", lat);
		HttpPost httpPost = MyHttpUtils.httpPostWithoutBody(url,paramsMap);
		//设置header
		Map<String,String> headers = new HashMap<>();
		headers.put("Authorization","APPCODE " + appCode);
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		MyHttpUtils.setHeader(httpPost,headers);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost);
		//执行请求，返回结果
		String httpResult = (String)MyHttpUtils.exec(httpPost,MyHttpUtils.ANALYZE_TYPE_STR);
		//解析结果
		JSONObject result = JSONObject.parseObject(httpResult);
		if(result != null && "0".equals(result.getString("code"))){
			try{
				JSONObject data = result.getJSONObject("data");
				return data;
			}catch (Exception e){
				log.error("获取天气data出错：" + result);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//首页天气假数据
	public static JSONObject getTempWeather(){
		//首页天气假数据-临时展示用
		JSONObject tempData = tempWeather.getJSONObject("data");
		//处理默认的15天天气预报，只截取未来3天的
		dealForecastToAnyDays(tempData,3);
		//假数据动态修改时间
		dealDateForTempData(tempData);
		//补上星期几
		addDayOfWeek(tempData);
		return tempData;
	}
	
	//天气详情假数据
	public static JSONObject getTempWeatherDetails(){
		return tempWeatherDetails;
	}
	
	//TODO 临时方法 动态修改时间 后期删除
	public static void dealDateForTempData(JSONObject tempData){
		String updatetimeStr = sdf.format(System.currentTimeMillis());
		JSONObject conditionJson = tempData.getJSONObject("condition");
		conditionJson.put("updatetime",updatetimeStr);
		JSONArray forecast = tempData.getJSONArray("forecast");
		long oneDay = 24*60*60*1000;
		for(int i = 0;i<forecast.size();i++){
			JSONObject day = forecast.getJSONObject(i);
			day.put("updatetime",sdf.format(System.currentTimeMillis() + oneDay*(i+1)));
		}
	}
	
	//处理默认的15天天气预报，只返回3天的(天气预报返回结果从昨天开始)
	public static void dealForecastToAnyDays(JSONObject data,int scale){
		JSONArray forecast = data.getJSONArray("forecast");
		if(scale > 15 || scale < 1 ||scale > forecast.size() - 2){
			log.error("三方接口返回数据有更改，需重新确认调整");
			return;
		}
		List<Object> forecastList = new ArrayList<>();
		//截取未来N天天气
		for(int i=0;i<scale;i++){
			forecastList.add(forecast.get(i+2));
		}
		forecast.clear();
		forecast.addAll(forecastList);
		data.put("forecast",forecast);
	}
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat sdfForShow = new SimpleDateFormat("MM/dd");  //前端天气展示用格式
	private static final String[] daysOfWeek = {"周日","周一","周二","周三","周四","周五","周六"};
	
	//为结果添加星期几
	public static void addDayOfWeek(JSONObject data){
		JSONObject conditionJson = data.getJSONObject("condition");
		String toDayStr = conditionJson.getString("updatetime");
		//处理今日结果
		Date today = null;
		try {
			today = sdf.parse(toDayStr);
		} catch (ParseException e) {
			log.error("时间解析出错，需检查三方接口返回数据是否更改");
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		conditionJson.put("dayOfWeek",daysOfWeek[dayOfWeek]);
		conditionJson.put("updateDay",sdfForShow.format(today));
		JSONArray forecast = data.getJSONArray("forecast");
		//处理预报结果
		Date dayDate = null;
		for(int i = 0;i<forecast.size();i++){
			JSONObject day = forecast.getJSONObject(i);
			String dayStr = day.getString("updatetime");
			try {
				dayDate = sdf.parse(dayStr);
			} catch (ParseException e) {
				log.error("时间解析出错，需检查三方接口返回数据是否更改");
				e.printStackTrace();
			}
			calendar.setTime(dayDate);
			day.put("dayOfWeek",daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
			day.put("updateDay",sdfForShow.format(dayDate));
		}
	}
	
	//根据空气质量指数，补充空气质量名称
	public static void addAQINameByAQIValue(JSONObject data, Double lon, Double lat, String cityId){
		JSONObject aqiJson = data.getJSONObject("aqi");
		aqiJson.put("aqiName", BusinessEnum.AQIEnum.getAQIName(aqiJson.getIntValue("value"),lon,lat,cityId));
	}
	
	//假天气数据
	private static final JSONObject tempWeather = new JSONObject();
	private static final JSONObject tempWeatherDetails = new JSONObject();
	
	//项目模块绝对路径
	public static String getClassesPath() {
		return WeatherUtils.class.getResource("/").getPath().replaceFirst("/","");
	}
	
	//linux绝对路径
	private static final String OS_LINUX_PATH = "/mnt/db/smart-community/file";
	
	//加载假天气数据
	static {
		try {
			FileReader fileInputStream;
			FileReader fileInputStream2;
			if(System.getProperty("os.name").startsWith("Win")){
				fileInputStream = new FileReader(new File(getClassesPath() + "/temp_weather.txt"));
				fileInputStream2 = new FileReader(new File(getClassesPath() + "/temp_weather_details.txt"));
//				fileInputStream = new FileReader(new File("D:" + "/temp_weather.txt"));
//				fileInputStream2 = new FileReader(new File("D:" + "/temp_weather_details.txt"));
			}else{
				fileInputStream = new FileReader(new File(OS_LINUX_PATH + "/temp_weather.txt"));
				fileInputStream2 = new FileReader(new File(OS_LINUX_PATH + "/temp_weather_details.txt"));
			}
			//首页天气
			BufferedReader reader = new BufferedReader(fileInputStream);
			StringBuffer sb = new StringBuffer();
			String str;
			while((str = reader.readLine()) != null){
				sb.append(str);
			}
			JSONObject jsonObject = JSONObject.parseObject(sb.toString());
			tempWeather.putAll(jsonObject);
			//天气详情
//			BufferedReader reader2 = new BufferedReader(fileInputStream2);
//			StringBuffer sb2 = new StringBuffer();
//			String str2;
//			while((str2 = reader2.readLine()) != null){
//				sb.append(str2);
//			}
//			JSONObject jsonObject2 = JSONObject.parseObject(sb2.toString());
//			tempWeatherDetails.putAll(jsonObject2);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
//	public static void main(String[] args) {
//		getWeatherNow("106.514787","29.622701");
//	}
}
