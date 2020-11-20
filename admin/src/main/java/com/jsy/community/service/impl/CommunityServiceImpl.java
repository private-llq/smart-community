package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.service.ICommunityService;
import com.jsy.community.utils.MyPageUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq459799974
 * @description 社区实现类
 * @since 2020-11-19 16:57
 **/
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper,CommunityEntity> implements ICommunityService {
	
	@Resource
	private CommunityMapper communityMapper;
	
	@Override
	public boolean addCommunity(CommunityEntity communityEntity){
		int result = communityMapper.insert(communityEntity);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteCommunity(Long id){
		int result = communityMapper.deleteById(id);
		if(result == 1){
			return true;
		}
		return false;
	}
	
	@Override
	public Map<String,Object> updateCommunity(CommunityEntity communityEntity){
		Map<String, Object> returnMap = new HashMap<>();
		if(communityEntity.getId() == null){
			returnMap.put("result",false);
			returnMap.put("msg","缺少id");
		}
		int result = communityMapper.updateById(communityEntity);
		if(result == 1){
			returnMap.put("result",true);
		}
		return returnMap;
	}
	
	@Override
	public Page<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO){
		Page<CommunityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<CommunityEntity>().select("*");
		CommunityQO query = baseQO.getQuery();
		if(query != null){
			if(!StringUtils.isEmpty(query.getName())){
				queryWrapper.like("name",query.getName());
			}
			if(query.getProvinceId() != null){
				queryWrapper.eq("province_id",query.getProvinceId());
			}
			if(query.getCityId() != null){
				queryWrapper.eq("city_id",query.getCityId());
			}
			if(query.getAreaId() != null){
				queryWrapper.eq("area_id",query.getAreaId());
			}
		}
		return communityMapper.selectPage(page,queryWrapper);
	}
	
}
