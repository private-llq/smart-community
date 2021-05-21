package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.DistanceUtil;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;


/**
 * @author chq459799974
 * @description 社区实现类
 * @since 2020-11-19 16:57
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper,CommunityEntity> implements ICommunityService {
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Autowired
	private IUserHouseService userHouseService;
	
	@Override
	public PageInfo<CommunityEntity> queryCommunity(BaseQO<CommunityQO> baseQO){
		Page<CommunityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<CommunityEntity>().select("*");
		CommunityQO query = baseQO.getQuery();
		if(query != null){
			if(query.getId() != null){
				queryWrapper.eq("id",query.getId());
			}
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
			if(query != null
				&& query.getLon() != null && query.getLat() != null
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
		PageInfo<CommunityEntity> PageInfo = new PageInfo<>();
		BeanUtils.copyProperties(communityEntityPage,PageInfo);
		return PageInfo;
	}

	/**
	 * 通过社区名称和城市id查询相关的社区数据
	 * @author YuLF
	 * @since  2020/11/23 11:21
	 * @Param  communityEntity 	查询参数实体
	 * @return 返回通过社区名称和城市id查询结果
	 */
	@Override
	public List<CommunityEntity> getCommunityByName(CommunityQO communityQO) {
		CommunityEntity communityEntity = new CommunityEntity();
		BeanUtil.copyProperties(communityQO , communityEntity);
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
	
	/**
	* @Description: 小区定位
	 * @Param: [uid, location]
	 * @Return: com.jsy.community.entity.CommunityEntity
	 * @Author: chq459799974
	 * @Date: 2020/11/25
	**/
	@Override
	public CommunityEntity locateCommunity(String uid,Map<String,Double> location){
		// 查业主房屋id和所属社区id
		List<UserHouseEntity> userHouseList = userHouseService.queryUserHouseIdsAndCommunityIds(uid);
		List<Long> communityIds = new LinkedList<>();
		CommunityEntity communityEntity;
		//暂未绑定小区
		if(CollectionUtils.isEmpty(userHouseList)){
			communityEntity = communityMapper.locateCommunity(communityIds, location);
			communityEntity.setName("暂未认证房屋");
			communityEntity.setId(0L); // 设置为通用小区
			return communityEntity;
		}
		//小区对应的最新一套已认证房屋id map
		Map<Long, Long> communityHouseMap = new HashMap<>();
		for(UserHouseEntity userHouseEntity : userHouseList){
			communityIds.add(userHouseEntity.getCommunityId());
			if(communityHouseMap.get(userHouseEntity.getCommunityId()) == null){
				communityHouseMap.put(userHouseEntity.getCommunityId(),userHouseEntity.getHouseId());
			}
		}
		//定位
		communityEntity = communityMapper.locateCommunity(communityIds, location);
		communityEntity.setHouseId(communityHouseMap.get(communityEntity.getId())); //家属界面默认房屋用
		return communityEntity;
	}
	
	/**
	 * @Description: 根据社区id批量查询社区名
	 * @Param: [ids]
	 * @Return: java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2020/12/16
	 **/
	@Override
	public Map<String,Map<String,Object>> queryCommunityNameByIdBatch(Collection<Long> ids){
		return communityMapper.queryCommunityNameByIdBatch(ids);
	}
	
	/**
	 * @Description: id单查社区
	 * @Param: [id]
	 * @Return: com.jsy.community.entity.CommunityEntity
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	 **/
	@Override
	public CommunityEntity queryCommunityById(Long id){
		return communityMapper.selectById(id);
	}
	
}
