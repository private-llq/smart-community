package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityTypeService;
import com.jsy.community.api.FacilityException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.exception.JSYError;
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
		//根节点pid为0
		if(facilityTypeEntity.getPid() != 0){
			Integer count = facilityTypeMapper.selectCount(new QueryWrapper<FacilityTypeEntity>().eq("id",facilityTypeEntity.getPid()));
			if(count != 1){
				throw new FacilityException(JSYError.REQUEST_PARAM.getCode(),"父级节点不存在！");
			}
		}
		facilityTypeMapper.insert(facilityTypeEntity);
	}
	
	@Override
	public void updateFacilityType(FacilityTypeEntity facilityTypeEntity) {
		facilityTypeMapper.update(facilityTypeEntity,new UpdateWrapper<FacilityTypeEntity>().eq("id",facilityTypeEntity.getId()).eq("community_id",facilityTypeEntity.getCommunityId()));
	}
	
	@Override
	public void deleteFacilityType(Long id, Long communityId) {
		FacilityTypeEntity entity = facilityTypeMapper.selectOne(new QueryWrapper<FacilityTypeEntity>().select("*").eq("id",id).eq("community_id",communityId));
		if(entity == null){
			throw new FacilityException(JSYError.BAD_REQUEST.getCode(),"设备分类不存在！");
		}
		QueryWrapper<FacilityTypeEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("pid", id).eq("community_id", communityId);
		
		List<FacilityTypeEntity> list = facilityTypeMapper.selectList(wrapper);
		if (!CollectionUtils.isEmpty(list)) {
			throw new FacilityException("\"" + entity.getName() + "\"" + "已有下级节点，不可删除");
		}
		
		QueryWrapper<FacilityEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("facility_type_id", entity.getId()).eq("community_id", communityId);
		List<FacilityEntity> entities = facilityMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(entities)) {
			throw new FacilityException("\"" + entity.getName() + "\"" + "已有属于该分类的设备，不可删除");
		}
		
		if (!entity.getCommunityId().equals(communityId)) {
			throw new FacilityException("该社区下没有该设备分类");
		}
		facilityTypeMapper.deleteById(id);
	}
	
	@Override
	public List<FacilityTypeVO> listFacilityType(Long communityId) {
		//1. 小区ID查列表
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
		
		//2. 在列表中挑出根节点
		List<FacilityTypeVO> parents = new ArrayList<>();
		for (FacilityTypeVO entity : allFacilityVO) {
			if (entity.getPid() != null && entity.getPid() == 0) {
				// 获取并设置该节点的人数
				Long typeId = entity.getId();
				QueryWrapper<FacilityEntity> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("facility_type_id", typeId);
				Integer count = facilityMapper.selectCount(queryWrapper);
				entity.setCount(count);
				parents.add(entity);
			}
		}
		
		//3. 设置子节点
		for (FacilityTypeVO parent : parents) {
			List<FacilityTypeVO> childList = getChild(parent.getId(), allFacilityVO);
			parent.setChildren(childList);
		}
		
		// 4.返回包含了子节点的 根节点列表
		return parents;
	}
	
	@Override
	public FacilityTypeEntity getFacilityType(Long id, Long communityId) {
		return facilityTypeMapper.selectOne(new QueryWrapper<FacilityTypeEntity>().select("*").eq("id",id).eq("community_id",communityId));
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
		List<FacilityTypeVO> childList = new ArrayList<>();
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
			return new ArrayList<>();
		}
		return childList;
	}
}
