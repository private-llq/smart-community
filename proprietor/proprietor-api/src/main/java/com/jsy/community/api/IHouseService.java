package com.jsy.community.api;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.HouseEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 楼栋房屋
 * @since 2020-12-16 14:12
 **/
public interface IHouseService extends IService<HouseEntity> {
	
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
	
	/** 
	* @Description: ids批量查房屋
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	Map<String, Map<String,Object>> queryHouseByIdBatch(Collection<Long> ids);
	
}
