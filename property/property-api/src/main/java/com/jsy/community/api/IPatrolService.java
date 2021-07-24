package com.jsy.community.api;

import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.entity.property.PatrolPointEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

/**
 * @author chq459799974
 * @description 物业巡检Service
 * @since 2021-07-23 15:55
 **/
public interface IPatrolService {
	
	/**
	* @Description: 添加巡检设备
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	boolean addEquip(PatrolEquipEntity entity);
	
	/**
	* @Description: 巡检设备 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolEquipEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	PageInfo<PatrolEquipEntity> queryEquipPage(BaseQO<PatrolEquipEntity> baseQO);
	
	/**
	* @Description: 修改巡检设备
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	boolean updateEquip(PatrolEquipEntity entity);
	
	/**
	* @Description: 删除巡检设备
	 * @Param: [id, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	boolean deleteEquip(Long id,Long communityId);
	
	/**
	* @Description: 新增巡检点位
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	boolean addPoint(PatrolPointEntity entity);
	
	/**
	* @Description: 巡检点位 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolPointEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	PageInfo<PatrolPointEntity> queryPointPage(BaseQO<PatrolPointEntity> baseQO);
	
	/**
	* @Description: 修改巡检点位
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	boolean updatePoint(PatrolPointEntity entity);
	
	/**
	* @Description: 删除巡检点位
	 * @Param: [id, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	boolean deletePoint(Long id,Long communityId);
}
