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
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilitySyncRecordEntity;
import com.jsy.community.mapper.*;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
	private FacilitySyncRecordMapper facilitySyncRecordMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private UserHouseMapper houseMapper;
	
	@Autowired
	private UserMapper userMapper;
	
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
	public void changeStatusBatch(Map<String,Object> messageMap){
		Long time = (Long) messageMap.get("time");
		Map<Long,Integer> data = (Map<Long, Integer>) messageMap.get("data");
		facilityMapper.updateStatusByFacilityIdBatch(data,time);
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
		facilityEntity.setAct(PropertyConsts.ACT_HK_CAMERA);
		rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_TO_COMMUNITY, TopicExConfig.QUEUE_TO_COMMUNITY + "." + facilityEntity.getCommunityId(), JSON.toJSONString(facilityEntity));
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
		facilityEntity.setAct(PropertyConsts.ACT_HK_CAMERA);
		rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_TO_COMMUNITY, TopicExConfig.QUEUE_TO_COMMUNITY + "." +communityId,JSON.toJSONString(facilityEntity));
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
		// 更新设备信息表
		facilityMapper.updateById(facilityEntity);
		//ip，账号，密码，端口号 有至少一个发生了改变    ——>需要通知小区服务器重新登录设备，开启功能
		if (!facility.getIp().equals(facilityEntity.getIp()) ||
			!facility.getUsername().equals(facilityEntity.getUsername()) ||
			!facility.getPassword().equals(facilityEntity.getPassword()) ||
			!facility.getPort().equals(facilityEntity.getPort()))
		{
			
			// 更新设备状态表[这里采用的不是更新表，而是直接删除原本条数据，重新新增数据]
			facilityMapper.deleteMiddleFacility(facilityId);
			//这里同新增一样 status暂时是0，等待异步通知修改状态
			facilityMapper.insertFacilityStatus(SnowFlake.nextId(),0,facilityId);
			//异步通知小区服务器
			facilityEntity.setOp("update");
			facilityEntity.setAct(PropertyConsts.ACT_HK_CAMERA);
			rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_TO_COMMUNITY, TopicExConfig.QUEUE_TO_COMMUNITY + "." +facilityEntity.getCommunityId(), JSON.toJSONString(facilityEntity));
		}
	}
	
	@Override
	public void flushFacility(Integer page, Integer size, String facilityTypeId, Long communityId) {
		//查询当前页的数据
		Page<FacilityEntity> entityPage = new Page<>(page, size);
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		wrapper.eq("facility_type_id", facilityTypeId);
		Page<FacilityEntity> facilityEntityPage = facilityMapper.selectPage(entityPage, wrapper);
		List<FacilityEntity> list = facilityEntityPage.getRecords();
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		//组装批量入参id集合
		List<Long> idList = new ArrayList<>();
		for (FacilityEntity facilityEntity : list) {
			idList.add(facilityEntity.getId());
		}
		//通知小区服务器刷新最新在线状态并同步到云端
		Map map = new HashMap<>();
		map.put("idList",idList);
		map.put("communityId",communityId);
		map.put("act",PropertyConsts.ACT_HK_CAMERA);
		rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_TO_COMMUNITY, TopicExConfig.QUEUE_TO_COMMUNITY + "." + communityId, JSON.toJSONString(map));
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
//		Map<Long,Map<Long,Integer>> statusMap = facilityMapper.getStatusBatch(ids);
		Map<Long,Map<Long,String>> typeNameMap = facilityTypeMapper.queryIdAndNameMap(typeIds);
		for (FacilityEntity facilityEntity : pageData.getRecords()) {
//			facilityEntity.setStatus(statusMap.get(BigInteger.valueOf(facilityEntity.getId())) == null ? null : statusMap.get(BigInteger.valueOf(facilityEntity.getId())).get("status"));
			facilityEntity.setFacilityTypeName(typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())) == null ? null : typeNameMap.get(BigInteger.valueOf(facilityEntity.getFacilityTypeId())).get("name"));
		}
		PageInfo<FacilityEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData, pageInfo);
		return pageInfo;
	}
	
	@Override
	public Map<String, Integer> getCount(Long typeId,Long communityId) {
		HashMap<String, Integer> returnMap = new HashMap<>();
		int onlineCount = 0;
		int failCount = 0;
		
		// 根据设备分类id查询其下设备的id集合
		List<Long> facilityIds = facilityMapper.getFacilityIdByTypeId(typeId,communityId);
		if(CollectionUtils.isEmpty(facilityIds)){
			returnMap.put("onlineCount", onlineCount);
			returnMap.put("failCount", failCount);
			return returnMap;
		}
		
		// 根据设备idList批量查询设备状态表的状态
		Map<Long,Map<Long,Integer>> facilityWithStatus = facilityMapper.getStatusBatch(facilityIds);
		for(Map<Long,Integer> map : facilityWithStatus.values() ){
			if(map.values().contains(1)){
				onlineCount += 1;
			}else if(map.values().contains(0)){
				failCount += 1;
			}
		}
		
		returnMap.put("onlineCount", onlineCount);
		returnMap.put("failCount", failCount);
		return returnMap;
	}
	
	@Override
	public FacilityEntity listByIp(String ip) {
		QueryWrapper<FacilityEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("ip", ip);
		return facilityMapper.selectOne(wrapper);
	}
	
	/**
	 * 单个摄像头同步(下发)人脸库数据
	 */
	@Override
	public void syncFaceData(Long id, Long communityId) {
		
		//1. 查询该社区的所有已认证的用户id
		Set<String> ids = houseMapper.listAuthUserId(communityId);
		//2. 批量查询已认证的用户数据
		List<UserEntity> userList = userMapper.listAuthUserInfo(ids);
		
		//修改摄像头 is_connect_data 字段 为 同步中，在监听中修改带条件 where is_connect_data = 同步中
		facilityMapper.updateDataConnectStatus(PropertyConsts.FACILITY_SYNC_DOING,id);
		
		//通知小区服务器同步人脸
		Map map = new HashMap<>();
		map.put("id",id);
		map.put("communityId",communityId);
		map.put("userList",userList);
		map.put("op","sync");
		map.put("act",PropertyConsts.ACT_HK_CAMERA);
		rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_TO_COMMUNITY,TopicExConfig.QUEUE_TO_COMMUNITY + "." + communityId, JSON.toJSONString(map));
	}
	
	/**
	* @Description: 设备数据同步后处理
	 * @Param: [resultCode,facilityId,communityId,msg]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/23
	**/
	@Override
	public void dealDataBySyncResult(Integer resultCode, JSONObject jsonObject){
		Long facilityId = jsonObject.getLong("facilityId");
		Long communityId = jsonObject.getLong("communityId");
		String msg = jsonObject.getString("msg");
		Long time = jsonObject.getLong("time");
		LocalDateTime localDateTime = new Date(time).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
		
		//更新设备数据同步状态
		facilityMapper.updateDataConnectStatusAndTime(resultCode,facilityId,localDateTime);
		//获取设备编号
		String number = facilityMapper.queryFacilityNumberById(facilityId);
		//添加设备同步记录
		FacilitySyncRecordEntity recordEntity = new FacilitySyncRecordEntity();
		recordEntity.setId(SnowFlake.nextId());
		recordEntity.setFacility_id(facilityId);
		recordEntity.setNumber(number);
		recordEntity.setIsSuccess(resultCode);
		recordEntity.setCommunityId(communityId);
		recordEntity.setRemark(msg);
		facilitySyncRecordMapper.insert(recordEntity);
	}
	
	/**
	* @Description: 分页查询数据同步记录 和 成功失败数统计
	 * @Param: [baseQO]
	 * @Return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: chq459799974
	 * @Date: 2021/6/24
	**/
	@Override
	public Map<String,Object> querySyncRecordPage(BaseQO<FacilitySyncRecordEntity> baseQO){
		FacilitySyncRecordEntity qo = baseQO.getQuery();
		Long communityId = qo.getCommunityId();
		Page page = new Page();
		MyPageUtils.setPageAndSize(page,baseQO);
		QueryWrapper queryWrapper = new QueryWrapper<>();
		queryWrapper.select("number,create_time,is_success,remark");
		queryWrapper.eq("community_id",communityId);
		if(qo.getIsSuccess() != null){
			queryWrapper.eq("is_success",qo.getIsSuccess());
		}
		//分页查询
		Page<FacilitySyncRecordEntity> pageData = facilitySyncRecordMapper.selectPage(page,queryWrapper);
		PageInfo<FacilitySyncRecordEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		//统计成功失败数
		List<Map<String,Object>> count = facilitySyncRecordMapper.countSuccessAndFail(communityId);
		Map countMap = new HashMap<>(count.size());
		for(Map<String,Object> map : count){
			countMap.put(map.get("type"),map.get("amount"));
		}
		Map<String,Object> map = new HashMap<>();
		map.put("pageData",pageInfo);
		map.put("count",countMap);
		return map;
	}
	
	/**
	* @Description: 根据数据同步状态统计设备数
	 * @Param: [communityId, syncStatus]
	 * @Return: java.lang.Long
	 * @Author: chq459799974
	 * @Date: 2021/6/24
	**/
	@Override
	public Long countBySyncStatus(Long communityId,Integer syncStatus){
		return facilityMapper.countBySyncStatus(communityId, syncStatus);
	}
	
	/**
	* @Description: 处理小区返回到MQ上的结果
	 * @Param: [jsonObject]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/6/29
	**/
	@Override
	public void dealResultFromCommunity(JSONObject jsonObject){
		String op = jsonObject.getString("op");
		log.info("收到海康设备返回指令：" + op);
		switch (op){
			case "addResult":
				//添加摄像头
				changeStatus(jsonObject.getInteger("status"),jsonObject.getLong("facilityId"),jsonObject.getLong("time"));
				break;
			case "updateResult":
				//编辑摄像头
				changeStatus(jsonObject.getInteger("status"),jsonObject.getLong("facilityId"),jsonObject.getLong("time"));
				break;
			case "flushResult":
				//刷新摄像头
				changeStatusBatch(jsonObject.toJavaObject(jsonObject,Map.class));
				break;
			case "syncResult":
				if("success".equals(jsonObject.getString("result"))){
					//数据同步成功
					dealDataBySyncResult(PropertyConsts.FACILITY_SYNC_DONE,jsonObject);
				}else if("fail".equals(jsonObject.getString("result"))){
					//数据同步失败
					dealDataBySyncResult(PropertyConsts.FACILITY_SYNC_HAVA_NOT,jsonObject);
				}
				//同步摄像头
				break;
			default:
				log.error("监听到小区无效海康指令：" + jsonObject.getString("op"));
				break;
		}
	}
}
