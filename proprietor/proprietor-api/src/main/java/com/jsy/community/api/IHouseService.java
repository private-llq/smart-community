package com.jsy.community.api;


import com.jsy.community.entity.HouseEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author chq459799974
 * @description 楼栋房屋
 * @since 2020-12-16 14:12
 **/
public interface IHouseService {
	
	/**
	 * @Description: 查询房间
	 * @Param: [list]
	 * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	 **/
	List<HouseEntity> queryHouses(Collection<Long> list);
	
	/**
	 * @Description: 查找父节点
	 * @Param: [tempEntity]
	 * @Return: com.jsy.community.entity.HouseEntity
	 * @Author: chq459799974
	 * @Date: 2020/12/17
	 **/
	HouseEntity getParent(HouseEntity tempEntity);
	
}
