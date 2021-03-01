package com.jsy.community.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chq459799974
 * @description 天气数据 - 生活指数
 * @since 2021-02-27 16:31
 **/
@Data
public class WeatherLiveIndexVO implements Serializable {
	private Integer code; // 项ID
	private String name; //项名 如 洗车
	private String status; //指数状态 如 较适宜
}
