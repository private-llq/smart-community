package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IPatrolService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.entity.property.PatrolLineEntity;
import com.jsy.community.entity.property.PatrolPointEntity;
import com.jsy.community.mapper.PatrolEquipMapper;
import com.jsy.community.mapper.PatrolLineMapper;
import com.jsy.community.mapper.PatrolPointMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author chq459799974
 * @description 物业巡检实现类
 * @since 2021-07-23 15:41
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PatrolServiceImpl implements IPatrolService {
	
	@Autowired
	private PatrolPointMapper patrolPointMapper;
	
	@Autowired
	private PatrolLineMapper patrolLineMapper;
	
	@Autowired
	private PatrolEquipMapper patrolEquipMapper;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IHouseService houseService;
	
	//===================== 巡检设备start ======================
	/**
	* @Description: 添加巡检设备
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@Override
	public boolean addEquip(PatrolEquipEntity entity){
		entity.setId(SnowFlake.nextId());
		//TODO 硬件品牌ID暂时固定为1
		entity.setBrandId(1L);
		return patrolEquipMapper.insert(entity) == 1;
	}
	
	/**
	 * @Description: 巡检设备 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolEquipEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	 **/
	@Override
	public PageInfo<PatrolEquipEntity> queryEquipPage(BaseQO<PatrolEquipEntity> baseQO){
		Page<PatrolEquipEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,number,name");
		PatrolEquipEntity query = baseQO.getQuery();
		if(!StringUtils.isEmpty(query.getId())){
			//查详情
			queryWrapper.eq("id",query.getId());
		}
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.like("name",query.getName());
		}
		queryWrapper.eq("community_id",query.getCommunityId());
		Page<PatrolEquipEntity> pageData = patrolEquipMapper.selectPage(page,queryWrapper);
		PageInfo<PatrolEquipEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	/**
	* @Description: 修改巡检设备
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@Override
	public boolean updateEquip(PatrolEquipEntity entity){
		return patrolEquipMapper.updateEquip(entity) == 1;
	}
	
	
	/**
	* @Description: 删除巡检设备
	 * @Param: [id, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@Override
	public boolean deleteEquip(Long id,Long communityId){
		return patrolEquipMapper.delete(new QueryWrapper<PatrolEquipEntity>().eq("id",id).eq("community_id",communityId)) == 1;
	}
	//===================== 巡检设备end ======================
	//===================== 巡检点位start ======================
	/**
	* @Description: 新增巡检点位
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@Override
	public boolean addPoint(PatrolPointEntity entity){
		entity.setId(SnowFlake.nextId());
		//TODO 硬件品牌ID暂时固定为1
		entity.setBrandId(1L);
		return patrolPointMapper.insert(entity) == 1;
	}
	
	/**
	* @Description: 巡检点 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolPointEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@Override
	public PageInfo<PatrolPointEntity> queryPointPage(BaseQO<PatrolPointEntity> baseQO){
		Page<PatrolPointEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,name,building_id,unit_id,address,number");
		PatrolPointEntity query = baseQO.getQuery();
		if(!StringUtils.isEmpty(query.getId())){
			//查详情
			queryWrapper.eq("id",query.getId());
			queryWrapper.select("id,name,building_id,unit_id,address,number,lon,lat,remark");
		}
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.like("name",query.getName());
		}
		queryWrapper.eq("community_id",query.getCommunityId());
		Page<PatrolPointEntity> pageData = patrolPointMapper.selectPage(page,queryWrapper);
		//查询楼栋、单元名称
		Set<Long> idSet =  new HashSet<>();
		for(PatrolPointEntity entity : pageData.getRecords()){
			idSet.add(entity.getBuildingId());
			idSet.add(entity.getUnitId());
		}
		Map<Long, HouseEntity> idAndHouseMap = houseService.queryIdAndHouseMap(idSet);
		for(PatrolPointEntity entity : pageData.getRecords()){
			entity.setBuildingName(idAndHouseMap.get(entity.getBuildingId()).getBuilding());
			entity.setUnitName(idAndHouseMap.get(entity.getUnitId()).getUnit());
		}
		PageInfo<PatrolPointEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	/**
	* @Description: 修改巡检点位
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@Override
	public boolean updatePoint(PatrolPointEntity entity){
		return patrolPointMapper.updatePoint(entity) == 1;
	}
	
	/**
	* @Description: 删除巡检点位
	 * @Param: [id, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@Override
	public boolean deletePoint(Long id,Long communityId){
		//TODO 检查是否已被添加到巡检线路
		return patrolPointMapper.delete(new QueryWrapper<PatrolPointEntity>().eq("id",id).eq("community_id",communityId)) == 1;
	}
	
	/**
	 * 检查巡检点位数据真实性
	 */
	private void checkPointList(List<Long> idList,Long communityId){
		Integer count = patrolPointMapper.selectCount(new QueryWrapper<PatrolPointEntity>().in("id", idList).eq("community_id", communityId));
		if(count != idList.size()){
			throw new PropertyException("巡检点数据有误，请检查");
		}
	}
	//===================== 巡检点位end ======================
	
	//===================== 巡检线路start ======================
	/**
	 * @Description: 添加巡检线路
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean addLine(PatrolLineEntity entity){
		entity.setId(SnowFlake.nextId());
		//TODO 硬件品牌ID暂时固定为1
		entity.setBrandId(1L);
		int lineAddResult = patrolLineMapper.insert(entity);
		//添加巡检点
		if(!CollectionUtils.isEmpty(entity.getPointIdList())){
			//验证巡检点数据真实性
			checkPointList(entity.getPointIdList(),entity.getCommunityId());
			//绑定巡检点位
			patrolLineMapper.addLinePoint(entity.getId(),entity.getPointIdList());
		}
		return lineAddResult == 1;
	}
	
	/**
	* @Description: 巡检线路 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.property.PatrolLineEntity>
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@Override
	public PageInfo<PatrolLineEntity> queryLinePage(BaseQO<PatrolLineEntity> baseQO){
		Page<PatrolLineEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,name,start_time,end_time,remark");
		PatrolLineEntity query = baseQO.getQuery();
		if(!StringUtils.isEmpty(query.getId())){
			//查详情
			queryWrapper.eq("id",query.getId());
		}
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.like("name",query.getName());
		}
		queryWrapper.eq("community_id",query.getCommunityId());
		Page<PatrolLineEntity> pageData = patrolLineMapper.selectPage(page,queryWrapper);
		if(!CollectionUtils.isEmpty(pageData.getRecords())){
			if(!StringUtils.isEmpty(query.getId())){
				//查询并设置线路已绑定的巡更点
				pageData.getRecords().get(0).setPointIdList(patrolLineMapper.queryBindPointList(query.getId()));
			}
		}
		PageInfo<PatrolLineEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}
	
	/**
	 * @Description: 修改巡检线路
	 * @Param: [entity]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateLine(PatrolLineEntity entity){
		//修改
		boolean updateResult = patrolLineMapper.updateLine(entity) == 1;
		if(!updateResult){
			return false;
		}
		//修改绑定的巡检点位
		if(!CollectionUtils.isEmpty(entity.getPointIdList())){
			//验证巡检点数据真实性
			checkPointList(entity.getPointIdList(),entity.getCommunityId());
			//清空原有的
			patrolLineMapper.clearLinePoint(entity.getId());
			//绑定新的
			patrolLineMapper.addLinePoint(entity.getId(),entity.getPointIdList());
		}
		return true;
	}
	
	/**
	 * @Description: 删除巡检线路
	 * @Param: [id, communityId]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteLine(Long id,Long communityId){
		//删除线路
		boolean deleteResult = patrolLineMapper.delete(new QueryWrapper<PatrolLineEntity>().eq("id", id).eq("community_id", communityId)) == 1;
		if(!deleteResult){
			return false;
		}
		//删除已绑定的巡检点
		patrolLineMapper.clearLinePoint(id);
		return true;
	}
	//===================== 巡检线路end ======================
}
