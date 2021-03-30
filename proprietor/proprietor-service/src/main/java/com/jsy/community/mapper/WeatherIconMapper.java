package com.jsy.community.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 天气图标
 * @since 2021-03-29 17:57
 **/
public interface WeatherIconMapper {
	
	/**
	* @Description: 获取最新一版天气图标
	 * @Param: []
	 * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.String>>
	 * @Author: chq459799974
	 * @Date: 2021/3/30
	**/
	@MapKey("num")
	@Select("select edition,num,url from t_weather_icon where edition = (select max(edition) from t_weather_icon)")
	Map<String,Map<String,String>> getLatestIcon();
	
}
