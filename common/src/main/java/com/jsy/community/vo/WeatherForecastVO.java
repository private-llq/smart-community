package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 天气数据 - 15天天气预报
 * @since 2021-03-01 09:44
 **/
@Data
public class WeatherForecastVO implements Serializable {
	private String conditionDay; //白天天气文字 如 晴
	private String conditionNight; //夜间天气文字 如 阴
	private String predictDate; //预报日期
	private String tempDay; //白天温度
	private String tempNight; //夜间温度
	private String windDirDay; //风向
	private String windLevelDay; //风力等级
}
