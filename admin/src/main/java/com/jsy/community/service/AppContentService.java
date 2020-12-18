package com.jsy.community.service;

import com.jsy.community.entity.RegionEntity;

import java.util.List;

/**
 * @author chq459799974
 * @description APP内容控制
 * @since 2020-12-14 18:05
 **/
public interface AppContentService {
	
	/**
	* @Description: 设置推荐城市
	 * @Param: [hotCityList]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	boolean setHotCity(List<RegionEntity> hotCityList);
}
