package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 天气数据 - 24h天气预报
 * @since 2021-02-27 17:59
 **/
@Data
public class WeatherHourlyVO  implements Serializable {
	private String condition; //天气文字 如 晴
	private String hour; //小时
	private String temp; //气温
	private String iconDay;//天气图标(取白天)
	private String iconUrl;//天气图标url
}
