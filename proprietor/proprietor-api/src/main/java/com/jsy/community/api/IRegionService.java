package com.jsy.community.api;

import com.jsy.community.entity.RegionEntity;

import java.util.List;

public interface IRegionService {
	
	/**
	* @Description: 根据区域编号获取子区域 (中国编号为100000)
	 * @Param: [id]
	 * @Return: java.util.List<com.jsy.community.entity.RegionEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/13
	**/
	List<RegionEntity> getSubRegion(String id);
	
}
