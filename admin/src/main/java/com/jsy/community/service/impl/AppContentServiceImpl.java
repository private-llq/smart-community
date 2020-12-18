package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.mapper.AppContentMapper;
import com.jsy.community.service.AppContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chq459799974
 * @description APP内容控制服务类
 * @since 2020-11-19 13:38
 **/
@Slf4j
@Service
public class AppContentServiceImpl implements AppContentService {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Resource
	private AppContentMapper appContentMapper;
	
	/**
	* @Description: 设置推荐城市
	 * @Param: [hotCityList]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/19
	**/
	@Override
	public boolean setHotCity(List<RegionEntity> hotCityList){
		if(CollectionUtils.isEmpty(hotCityList)){
			return false;
		}
		//备份
		List<RegionEntity> hotCitys = appContentMapper.getHotCity();
		//清空
		appContentMapper.clearHotCity();
		//新增
		int rows = appContentMapper.insertHotCity(hotCityList);
		//还原
		if(rows != hotCityList.size()){
			log.error("设置推荐城市出错：" + hotCityList,"成功条数：" + rows);
			appContentMapper.clearHotCity();
			appContentMapper.insertHotCity(hotCitys);
			return false;
		}
		//设置到redis
		redisTemplate.opsForValue().set("hotCityList", JSONObject.toJSONString(hotCityList));
		return true;
	}
}
