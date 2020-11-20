package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.DistanceUtil;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


/**
 * @author chq459799974
 * @description 社区实现类
 * @since 2020-11-19 16:57
 **/
@DubboService(version = Const.version, group = Const.group)
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper,CommunityEntity> implements ICommunityService {
	
	@Autowired
	private CommunityMapper communityMapper;
	
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
		Page<CommunityEntity> communityEntityPage = communityMapper.selectPage(page, queryWrapper);
		//根据经纬度设置距离
		for(CommunityEntity communityEntity : communityEntityPage.getRecords()){
			if(query.getLon() != null && query.getLat() != null
				&& communityEntity.getLon() !=null && communityEntity.getLat() != null){
				Map<String, Object> distanceMap = DistanceUtil.getDistance(query.getLon().doubleValue(), query.getLat().doubleValue(),
					communityEntity.getLon().doubleValue(), communityEntity.getLat().doubleValue());
				if(distanceMap.size() != 0){
					communityEntity.setDistanceDouble(Double.valueOf(String.valueOf(distanceMap.get("distanceDouble"))));
					communityEntity.setDistanceString(String.valueOf(distanceMap.get("distanceString")));
				}
			}
		}
		//附近功能 按distance排序
		Collections.sort(communityEntityPage.getRecords(),new Comparator<CommunityEntity>() {
			@Override
			public int compare(CommunityEntity o1, CommunityEntity o2) {
				if(o1.getDistanceDouble() != null && o2.getDistanceDouble() != null){
					return o1.getDistanceDouble().compareTo(o2.getDistanceDouble());
				}
				return 0;
			}
		});
		return communityEntityPage;
	}
	
}
