package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.HouseMemberMapper;
import com.jsy.community.mapper.UserHouseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区 服务实现类
 *
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, CommunityEntity> implements ICommunityService {
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Autowired
	private UserHouseMapper userHouseMapper;
	
	@Autowired
	private HouseMemberMapper houseMemberMapper;
	
	@Override
	public List<CommunityEntity> listCommunityByName(String query,Integer areaId) {
		QueryWrapper<CommunityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("area_id",areaId).like("name", query).or().like("detail_address", query);
		return communityMapper.selectList(wrapper);
	}
	
	@Override
	public List<CommunityEntity> listCommunityByAreaId(Long areaId) {
		QueryWrapper<CommunityEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("area_id", areaId);
		return communityMapper.selectList(queryWrapper);
	}
	
	/**
	* @Description: 查询社区模式
	 * @Param: [id]
	 * @Return: java.lang.Integer
	 * @Author: chq459799974
	 * @Date: 2021/1/21
	**/
	@Override
	public Integer getCommunityMode(Long id){
		return communityMapper.getCommunityMode(id);
	}
	
	@Override
	@Transactional
	@LcnTransaction
	public void addCommunityEntity() {
		CommunityEntity communityEntity = new CommunityEntity();
		communityEntity.setId(140L);
		communityEntity.setName("测试分布式事物");
		communityMapper.insert(communityEntity);
	}
	
	@Override
	public CommunityEntity getCommunityNameById(Long communityId) {
		return communityMapper.selectById(communityId);
	}
	
	/**
	* @Description: ids批量查小区
	 * @Param: [idList]
	 * @Return: java.util.List<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@Override
	public List<CommunityEntity> queryCommunityBatch(List<Long> idList){
		return communityMapper.queryCommunityBatch(idList);
	}
	
	@Override
	public Map<String, Object> getElectronicMap(Long communityId) {
		HashMap<String, Object> hashMap = new HashMap<>();
		
		//1. 获取社区基本信息
		CommunityEntity communityEntity = communityMapper.selectById(communityId);
		String name = communityEntity.getName();
		String number = communityEntity.getNumber();
		String detailAddress = communityEntity.getDetailAddress();
		BigDecimal acreage = communityEntity.getAcreage();
		BigDecimal lon = communityEntity.getLon();
		BigDecimal lat = communityEntity.getLat();
		
		hashMap.put("name",name);
		hashMap.put("number",number);
		hashMap.put("detailAddress",detailAddress);
		hashMap.put("acreage",acreage);
		hashMap.put("lon",lon);
		hashMap.put("lat",lat);
		
		//2. 获取社区总人数
		QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("check_status",1);
		
		// TODO: 2021/4/19 问了组长 总人数是这么获取的
		// 认证的业主总数【根据房屋认证表获取】
		Integer userHouseCount = userHouseMapper.selectCount(wrapper);
		
		// 房间成员人数【不包含业主本人】
		Integer houseMemberCount = houseMemberMapper.selectCount(null);
		
		hashMap.put("count",userHouseCount+houseMemberCount);
		
		return hashMap;
	}
}
