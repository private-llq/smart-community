package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IPatrolService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.mapper.PatrolEquipMapper;
import com.jsy.community.mapper.PatrolLineMapper;
import com.jsy.community.mapper.PatrolPointMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
			queryWrapper.eq("id",query.getId());
		}
		if(!StringUtils.isEmpty(query.getName())){
			queryWrapper.like("name",query.getName());
		}
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
	public void deleteEquip(Long id,Long communityId){
		patrolEquipMapper.delete(new QueryWrapper<PatrolEquipEntity>().eq("id",id).eq("community_id",communityId));
	}
	//===================== 巡检设备end ======================
	
	//===================== 巡检点位start ======================
	//===================== 巡检点位end ======================
	//===================== 巡检线路start ======================
	//===================== 巡检线路end ======================
}
