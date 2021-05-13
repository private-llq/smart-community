package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityTypeService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.vo.hk.FacilityTypeVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2021-03-12
 */
@DubboService(version = Const.version, group = Const.group_facility)
public class FacilityTypeServiceImpl extends ServiceImpl<FacilityTypeMapper, FacilityTypeEntity> implements IFacilityTypeService {
	
	@Resource
	private FacilityTypeMapper facilityTypeMapper;
	
	@Resource
	private FacilityMapper facilityMapper;
	
	@Override
	public void addFacilityType(FacilityTypeEntity facilityTypeEntity) {
		facilityTypeMapper.insert(facilityTypeEntity);
	}
	
	@Override
	public void updateFacilityType(FacilityTypeEntity facilityTypeEntity) {
		if (facilityTypeEntity.getId() == null) {
			throw new PropertyException("请选择要修改的设备分类");
		}
		
		QueryWrapper<FacilityTypeEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", facilityTypeEntity.getCommunityId()).eq("id", facilityTypeEntity.getId());
		FacilityTypeEntity typeEntity = facilityTypeMapper.selectOne(wrapper);
		if (typeEntity != null) {
			facilityTypeMapper.updateById(facilityTypeEntity);
		}
	}
	
	@Override
	public void deleteFacilityType(Long id, Long communityId) {
		FacilityTypeEntity entity = facilityTypeMapper.selectById(id);
		if (entity != null) {
			QueryWrapper<FacilityTypeEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("pid", id).eq("community_id", communityId);
			
			List<FacilityTypeEntity> list = facilityTypeMapper.selectList(wrapper);
			if (!CollectionUtils.isEmpty(list)) {
				throw new PropertyException("\"" + entity.getName() + "\"" + "已有下级节点，不可删除");
			}
			
			QueryWrapper<FacilityEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("facility_type_id", entity.getId()).eq("community_id", communityId);
			List<FacilityEntity> entities = facilityMapper.selectList(queryWrapper);
			if (!CollectionUtils.isEmpty(entities)) {
				throw new PropertyException("\"" + entity.getName() + "\"" + "已有属于该分类的设备，不可删除");
			}
			
			if (!entity.getCommunityId().equals(communityId)) {
				throw new PropertyException("该社区下没有该设备分类");
			}
			facilityTypeMapper.deleteById(id);
		}
	}
	
	@Override
	public List<FacilityTypeVO> listFacilityType(Long communityId) {
		//1. 查询所有设备分类
		QueryWrapper<FacilityTypeEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		List<FacilityTypeEntity> allFacility = facilityTypeMapper.selectList(wrapper);
		
		List<FacilityTypeVO> allFacilityVO = new ArrayList<>();
		for (FacilityTypeEntity facilityTypeEntity : allFacility) {
			FacilityTypeVO facilityTypeVO = new FacilityTypeVO();
			BeanUtils.copyProperties(facilityTypeEntity, facilityTypeVO);
			facilityTypeVO.setLabel(facilityTypeEntity.getName());
			allFacilityVO.add(facilityTypeVO);
		}
		
		//2. 查询所有根节点
		List<FacilityTypeVO> parents = new ArrayList<>();
		for (FacilityTypeVO entity : allFacilityVO) {
			if (entity.getPid() == 0) {
				// 获取并设置该节点的人数
				Long typeId = entity.getId();
				QueryWrapper<FacilityEntity> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("facility_type_id", typeId);
				Integer count = facilityMapper.selectCount(queryWrapper);
				entity.setCount(count);
				parents.add(entity);
			}
		}
		
		//3. 为根节点设置子节点，getClild是递归调用的
		for (FacilityTypeVO parent : parents) {
			List<FacilityTypeVO> childList = getChild(parent.getId(), allFacilityVO);
			parent.setChildren(childList);
		}
		
		return parents;
	}
	
	@Override
	public FacilityTypeEntity getFacilityType(Long id) {
		return facilityTypeMapper.selectById(id);
	}
	
	/**
	 * 获取子节点
	 *
	 * @param id            父节点id
	 * @param allFacilityVO 所有菜单列表
	 * @return 每个根节点下，所有子菜单列表
	 */
	public List<FacilityTypeVO> getChild(Long id, List<FacilityTypeVO> allFacilityVO) {
		//子菜单
		List<FacilityTypeVO> childList = new ArrayList<FacilityTypeVO>();
		for (FacilityTypeVO nav : allFacilityVO) {
			// 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
			//相等说明：为该根节点的子节点。
			if (id.equals(nav.getPid())) {
				Long typeId = nav.getId();
				QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
				wrapper.eq("facility_type_id", typeId);
				Integer count = facilityMapper.selectCount(wrapper);
				nav.setCount(count);
				
				childList.add(nav);
			}
		}
		//递归
		for (FacilityTypeVO nav : childList) {
			nav.setChildren(getChild(nav.getId(), allFacilityVO));
		}
		//如果节点下没有子节点，返回一个空List（递归退出）
		if (childList.size() == 0) {
			return new ArrayList<FacilityTypeVO>();
		}
		return childList;
	}
}
