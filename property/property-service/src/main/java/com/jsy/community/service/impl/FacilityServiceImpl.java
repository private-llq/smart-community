package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.mapper.FacilityMapper;
import com.jsy.community.mapper.FacilityTypeMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	/**
	 * 更新在线状态(写库)
	 */
	@Override
	public void changeStatus(Integer status,Long facilityId,Long time){
		//指令执行时间与最近修改时间/创建时间比较，若是最近修改时间之前的操作，则抛弃
		FacilityEntity entity = facilityMapper.getStatusTime(facilityId);
		if(entity == null){
			//可能场景：前端连续操作添加-删除，小区服务器在前端已删除后 才联网收到添加和删除的连续指令
			throw new PropertyException("没有此设备状态信息，无法更新设备状态");
		}
		//指令执行时间与最近修改时间比较，若是最近修改时间之前的操作，则抛弃
		if(entity.getUpdateTime() != null){
			//获取时间戳，转化为LocalDateTime
			LocalDateTime opTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
			if(opTime.isBefore(entity.getUpdateTime())){
				return;
			}
		}
		facilityMapper.updateStatus(status,facilityId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addFacility(FacilityEntity facilityEntity) {
		//设备表新增数据
		facilityEntity.setId(SnowFlake.nextId());
		facilityMapper.insert(facilityEntity);
		//设备状态表新增数据，此时添加成功但设备未登录，等待异步通知修改状态
		facilityMapper.insertFacilityStatus(SnowFlake.nextId(), 0, facilityEntity.getId());
		//MQ通知小区服务器登录设备
		facilityEntity.setOp("add");
		rabbitTemplate.convertAndSend(TopicExConfig.EX_HK_CAMERA, TopicExConfig.TOPIC_HK_CAMERA_OP, JSONObject.parseObject(JSON.toJSONString(facilityEntity)));
	}
	
	@Override
	public void deleteFacility(Long id,Long communityId) {
		//验证设备是否归属本小区
		Integer count = facilityMapper.selectCount(new QueryWrapper<FacilityEntity>().eq("id",id).eq("community_id",communityId));
		if(count < 1){
			throw new PropertyException("没有该设备");
		}
		//异步通知小区服务器撤防和注销
		FacilityEntity facilityEntity = new FacilityEntity();
		facilityEntity.setId(id);
		facilityEntity.setCommunityId(communityId);
		facilityEntity.setOp("del");
		rabbitTemplate.convertAndSend(TopicExConfig.EX_HK_CAMERA, TopicExConfig.TOPIC_HK_CAMERA_OP,JSONObject.parseObject(JSON.toJSONString(facilityEntity)));
		//直接删除设备，返回成功(反正也查不到了，不用等待异步注销成功)
		facilityMapper.deleteFacilityById(id);
		// 删除设备状态信息
		facilityMapper.deleteMiddleFacility(id);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFacility(FacilityEntity facilityEntity) {
		// 判断此次修改是否没有修改ip，账号，密码，端口号。若没有修改就只更新基本信息即可
		Long facilityId = facilityEntity.getId();
		FacilityEntity facility = facilityMapper.selectOne(new QueryWrapper<FacilityEntity>().eq("id",facilityId).eq("community_id",facilityEntity.getCommunityId()));
		if (facility == null) {
			throw new PropertyException("你选择的设备不存在");
		}
		//ip，账号，密码，端口号 有至少一个发生了改变    ——>需要通知小区服务器重新登录设备，开启功能
		if (!facility.getIp().equals(facilityEntity.getIp()) ||
			!facility.getUsername().equals(facilityEntity.getUsername()) ||
			!facility.getPassword().equals(facilityEntity.getPassword()) ||
			!facility.getPort().equals(facilityEntity.getPort()))
		{
			//异步通知小区服务器
			facilityEntity.setOp("update");
			rabbitTemplate.convertAndSend(TopicExConfig.EX_HK_CAMERA, TopicExConfig.TOPIC_HK_CAMERA_OP, JSONObject.parseObject(JSON.toJSONString(facilityEntity)));
			
			// 更新设备状态表[这里采用的不是更新表，而是直接删除原本条数据，重新新增数据]
			facilityMapper.deleteMiddleFacility(facilityId);
			//这里同新增一样 status暂时是0，等待异步通知修改状态
			facilityMapper.insertFacilityStatus(SnowFlake.nextId(),0,facilityId);
		}
		// 更新设备信息表
		facilityMapper.updateById(facilityEntity);
	}
	
	@Override
	public PageInfo<FacilityEntity> listFacility(BaseQO<FacilityQO> baseQO) {
		FacilityQO qo = baseQO.getQuery();
		Page<FacilityEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<FacilityEntity> pageData = facilityMapper.listFacility(qo, page);
		if(pageData.getRecords().size() == 0){
			return new PageInfo<>();
		}
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
	public Map<String, Integer> getCount(Long typeId,Long communityId) {
		// 根据设备分类id查询其下设备的id集合
		List<Long> facilityIds = facilityMapper.getFacilityIdByTypeId(typeId,communityId);
		
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
