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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

	/**
	 * 通过社区名称和城市id查询相关的社区数据
	 * @author YuLF
	 * @since  2020/11/23 11:21
	 * @Param  communityEntity 	查询参数实体
	 * @return 返回通过社区名称和城市id查询结果
	 */
	@Override
	public List<CommunityEntity> getCommunityByName(CommunityEntity communityEntity) {
		//按名称何城市id 模糊查询 所有社区
		List<CommunityEntity> communityEntityList = communityMapper.getCommunityByName(communityEntity);
		double lat = communityEntity.getLat().doubleValue();
		double lon = communityEntity.getLon().doubleValue();
		if( null != communityEntityList ){
			 //遍历每一个社区实体 获取用户到社区的距离
			for(CommunityEntity everyOne : communityEntityList){
				Map<String, Object> distance = DistanceUtil.getDistance(lon, lat, everyOne.getLon().doubleValue(), everyOne.getLat().doubleValue());
				//如果等于null getDistance出现未知异常
				if(distance == null){
					continue;
				}
				everyOne.setDistanceDouble((Double) distance.get("distanceDouble"));
				everyOne.setDistanceString(String.valueOf(distance.get("distanceString")));
			}
			//用户到社区的距离 从小到大排序返回
			communityEntityList.sort(Comparator.comparing(CommunityEntity::getDistanceDouble));
		}
		return communityEntityList;
	}

}