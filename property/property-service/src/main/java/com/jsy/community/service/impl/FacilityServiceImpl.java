package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * @author chq459799974
 * @description 设备相关实现类
 * @since 2021-06-12 10:29
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Slf4j
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, FacilityEntity> implements IFacilityService {
	
	@Autowired
	private FacilityMapper facilityMapper;
	
	@Autowired
	private FacilityTypeMapper facilityTypeMapper;
	
	@Override
//	@Transactional
	public void addFacility(FacilityEntity facilityEntity) {
	}
	
	@Override
	public void deleteFacility(Long id) {
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFacility(FacilityEntity facilityEntity) {
	}
	
	@Override
	public PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> baseQO) {
		FacilityQO qo = baseQO.getQuery();
		Page<FacilityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<FacilityEntity> pageData = facilityMapper.listFacility(qo, page);
		ArrayList<Long> ids = new ArrayList<>();
		Set<Long> typeIds = new HashSet<>();
		for (FacilityEntity facilityEntity : pageData.getRecords()) {
			ids.add(facilityEntity.getId());
			typeIds.add(facilityEntity.getFacilityTypeId());
		}
		//查询和设置设备状态、设备类型名
		Map<Long,Map<Long,Integer>> statusMap = facilityMapper.getStatusBatch(ids);
		Map<Long,Map<Long,String>> typeNameMap = facilityTypeMapper.queryIdAndNameMap(typeIds);
		for (FacilityEntity facilityEntity : pageData.getRecords()) {
			facilityEntity.setStatus(statusMap.get(BigInteger.valueOf(facilityEntity.getId())) == null ? null : statusMap.get(BigInteger.valueOf(facilityEntity.getId())).get("status"));
			facilityEntity.setFacilityTypeName(typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())) == null ? null : typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())).get("name"));
		}
		PageInfo<FacilityEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	@Override
	public Map<String, Integer> getCount(Long typeId) {
		// 根据设备分类id查询其下设备的id集合
		//TODO 查询条件加社区id
		List<Long> facilityIds = facilityMapper.getFacilityIdByTypeId(typeId);
		
		int onlineCount = 0;
		int failCount = 0;
		// 根据设备id查询设备状态表的状态
		for (Long facilityId : facilityIds) {
			int status = facilityMapper.getStatus(facilityId);
			if (status == 0) {
				onlineCount += 1;
			} else {
				failCount += 1;
			}
		}
		HashMap<String, Integer> map = new HashMap<>();
		map.put("onlineCount", onlineCount);
		map.put("failCount", failCount);
		return map;
	}
	
	@Override
//	@Transactional(rollbackFor = Exception.class)
	public void flushFacility(Integer page, Integer size, String facilityTypeId) {
	}
	
	@Override
	public FacilityEntity listByIp(String ip) {
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip", ip);
		return facilityMapper.selectOne(wrapper);
	}
	
	@Override
	public void connectData(Long id, Long communityId) {
	}
}
