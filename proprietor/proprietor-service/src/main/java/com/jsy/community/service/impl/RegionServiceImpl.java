package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.jsy.community.api.IRegionService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RegionEntity;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.List;

/**
 * @Description: 省市区服务类
 * @Author chq459799974
 * @Date 2020/11/13 10:38
 **/
@DubboService(version = Const.version, group = Const.group)
public class RegionServiceImpl implements IRegionService {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * @Description: 获取子区域
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq45799974
	 * @Date: 2020/11/13
	 **/
	@Override
	public List<RegionEntity> getSubRegion(Integer id){
		return JSONArray.parseObject(String.valueOf(stringRedisTemplate.opsForHash().get("Region:", id)), List.class);
	}
	
}
