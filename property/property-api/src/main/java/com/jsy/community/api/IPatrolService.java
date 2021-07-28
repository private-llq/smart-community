package com.jsy.community.api;

import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.entity.property.PatrolLineEntity;
import com.jsy.community.entity.property.PatrolPointEntity;
import com.jsy.community.entity.property.PatrolRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;

import java.util.List;

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
	
	/**
	* @Description: 新增巡检线路
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	boolean addLine(PatrolLineEntity entity);
	
	/**
	* @Description: 巡检线路 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolLineEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	PageInfo<PatrolLineEntity> queryLinePage(BaseQO<PatrolLineEntity> baseQO);
	
	/**
	* @Description: 修改巡检线路
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	boolean updateLine(PatrolLineEntity entity);
	
	/**
	* @Description: 删除巡检线路
	 * @Param: [id, communityId]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	boolean deleteLine(Long id,Long communityId);
	
	/**
	* @Description: 巡检记录入库
	 * @Param: [recordList, brandId, equipNumer]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021-07-27
	**/
	String addRecord(List<PatrolRecordEntity> recordList, Long brandId, String equipNumer);
	
	/**
	* @Description: 巡检记录 分页/列表查询
	 * @Param: [baseQO,queryType]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolRecordEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-27
	**/
	Object queryRecordPage(BaseQO<PatrolRecordEntity> baseQO, int queryType);
}
