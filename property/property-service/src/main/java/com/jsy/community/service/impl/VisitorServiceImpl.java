package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.VisitorHistoryMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.mapper.VisitorPersonRecordMapper;
import com.jsy.community.mapper.VisitorStrangerMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author chq459799974
 * @description 物业端访客实现类
 * @since 2021-04-12 13:35
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class VisitorServiceImpl implements IVisitorService {
	
	@Autowired
	private VisitorMapper visitorMapper;
	
	@Autowired
	private VisitorPersonRecordMapper visitorPersonRecordMapper;
	
	@Autowired
	private VisitorHistoryMapper visitorHistoryMapper;
	
	@Autowired
	private VisitorStrangerMapper visitorStrangerMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	// @DubboReference(version = Const.version, group = Const.group_property, check = false)
	@Autowired
	private IUserService userService;

	// @DubboReference(version = Const.version, group = Const.group_property, check = false)
	@Autowired
	private IAdminUserService adminUserService;
	
	
	/**
	 * @Description: 访客记录 分页查询(现在主表数据是t_visitor,连表查询，以后主表可能会改为t_visitor_history)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorHistoryEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/14
	 **/
	@Override
	public PageInfo<VisitorHistoryEntity> queryVisitorPage(BaseQO<VisitorHistoryEntity> baseQO){
		VisitorHistoryEntity query = baseQO.getQuery();
		Page<VisitorHistoryEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		Page<VisitorHistoryEntity> pageData = visitorHistoryMapper.queryPage(page,query);
		if(CollectionUtils.isEmpty(pageData.getRecords())){
			return new PageInfo<>();
		}
		Set<Long> visitorIds = new HashSet<>();
		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
			visitorIds.add(historyEntity.getVisitorId());
		}
		//查随行人员表统计随行人员数量
		Map<Long, Map<Long,Long>> countMap = visitorPersonRecordMapper.getFollowPersonBatch(visitorIds);
		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
			historyEntity.setFollowCount(countMap.get(historyEntity.getVisitorId()) == null ? null : countMap.get(historyEntity.getVisitorId()).get("count"));
		}
		PageInfo pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}

	/**
	 * @param baseQO : 分页参数
	 * @author: Pipi
	 * @description: 访客管理分页查询
	 * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorEntity>
	 * @date: 2021/8/13 17:26
	 **/
	@Override
	public PageInfo<VisitorEntity> visitorPage(BaseQO<VisitorEntity> baseQO) {
		Page<VisitorEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,baseQO);
		VisitorEntity query = baseQO.getQuery() == null ? new VisitorEntity() : baseQO.getQuery();
		QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("*, TIMESTAMPDIFF(MINUTE, start_time, end_time) as effectiveMinutes");
		if (!StringUtils.isEmpty(query.getName())) {
			queryWrapper.like("name", query.getName());
		}
		if (query.getBuildingId() != null) {
			queryWrapper.eq("building_id", query.getBuildingId());
		}
		if (query.getCheckType() != null) {
			queryWrapper.eq("check_type", query.getCheckType());
		}
		Page<VisitorEntity> visitorEntityPage = visitorMapper.selectPage(page, queryWrapper);
		if (!CollectionUtils.isEmpty(visitorEntityPage.getRecords())) {
			for (VisitorEntity visitorEntity : visitorEntityPage.getRecords()) {
				if (visitorEntity.getUid() != null) {
					if (visitorEntity.getCheckType() == 1) {
						//业主审核,查询业主信息
						UserEntity userEntity = userService.selectOne(visitorEntity.getUid());
						if (userEntity != null) {
							visitorEntity.setNameOfAuthorizedPerson(userEntity.getRealName());
							visitorEntity.setMobileOfAuthorizedPerson(userEntity.getMobile());
						}
					} else {
						// 物业审核,查询物业管理员信息
						AdminUserEntity adminUserEntity = adminUserService.queryByUid(visitorEntity.getUid());
						if (adminUserEntity != null) {
							visitorEntity.setNameOfAuthorizedPerson(adminUserEntity.getRealName());
							visitorEntity.setMobileOfAuthorizedPerson(adminUserEntity.getMobile());
						}
					}
				}

			}
		}
		PageInfo<VisitorEntity> visitorEntityPageInfo = new PageInfo<>();
		BeanUtils.copyProperties(visitorEntityPage, visitorEntityPageInfo);

		return visitorEntityPageInfo;
	}

	/**
	* @Description: 查询单次访客邀请的随行人员列表
	 * @Param: [visitorId]
	 * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@Override
	public List<VisitorPersonRecordEntity> queryFollowPersonListByVisitorId(Long visitorId){
		return visitorPersonRecordMapper.selectList(new QueryWrapper<VisitorPersonRecordEntity>().select("name,mobile").eq("visitor_id",visitorId));
	}
	
	/**
	* @Description: 访客记录 分页查询(需要待入园数据必须是t_visitor，但是此方案主表是t_visitor_history，暂时搁置)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorEntity>
	 * @Author: chq459799974
	 * @Date: 2021/4/12
	**/
//	@Override
//	public PageInfo<VisitorHistoryEntity> queryVisitorPage(BaseQO<VisitorEntity> baseQO){
//		VisitorEntity query = baseQO.getQuery();
//		Page<VisitorHistoryEntity> page = new Page<>();
//		MyPageUtils.setPageAndSize(page,baseQO);
//		QueryWrapper<VisitorHistoryEntity> queryWrapper = new QueryWrapper<>();
//		queryWrapper.select("id,visitor_id,name,in_time,out_time,start_time");
//		queryWrapper.eq("community_id",query.getCommunityId());
//		queryWrapper.orderByDesc("start_time");
//		if(!StringUtils.isEmpty(query.getName())){
//			queryWrapper.like("name",query.getName());
//		}
//		if(!StringUtils.isEmpty(query.getContact())){
//			queryWrapper.like("contact",query.getContact());
//		}
////		if(!StringUtils.isEmpty(query.getCarPlate())){
////			queryWrapper.like("car_plate",query.getCarPlate());
////		}
//		//1.查主表-进出历史表
//		Page<VisitorHistoryEntity> pageData = visitorHistoryMapper.selectPage(page, queryWrapper);
//		if(CollectionUtils.isEmpty(pageData.getRecords())){
//			return new PageInfo<>();
//		}
//		Set<Long> visitorIds = new HashSet<>();
//		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
//			visitorIds.add(historyEntity.getVisitorId());
//		}
//		//2.查访客登记表数据
//		Map<Long, VisitorEntity> visitorMap = visitorMapper.queryVisitorMapBatch(visitorIds);
//		//3.查随行人员表统计随行人员数量
//		Map<Long, Map<Long,Long>> countMap = visitorPersonRecordMapper.getFollowPersonBatch(visitorIds);
//		//4.复制数据
//		for(VisitorHistoryEntity historyEntity : pageData.getRecords()){
//			BeanUtils.copyProperties(visitorMap.get(historyEntity.getVisitorId()),historyEntity,"id","name","createTime","startTime");
//			historyEntity.setFollowCount(countMap.get(historyEntity.getVisitorId()) == null ? null : countMap.get(historyEntity.getVisitorId()).get("count"));
//		}
//		PageInfo<VisitorHistoryEntity> pageInfo = new PageInfo<>();
//		BeanUtils.copyProperties(pageData,pageInfo);
//		return pageInfo;
//	}
	
	/**
	* @Description: 批量新增访客进出记录
	 * @Param: [jsonObject]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021/7/2
	**/
	@Override
	public void addVisitorRecordBatch(JSONObject jsonObject){
		System.out.println(jsonObject);
		//TODO mybatisplus批量插入
	}
	
	/**
	* @Description: 陌生人脸上传
	 * @Param: [jsonObject]
	 * @Return: void
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	@Override
	public void saveStranger(JSONObject jsonObject){
		String picBase64 = jsonObject.getString("picBase64");
		MultipartFile multipartFile = Base64Util.base64StrToMultipartFile(picBase64.substring(picBase64.indexOf(",") + 1));
		String snapUrl = null;
		if(multipartFile != null){
			snapUrl = MinioUtils.uploadByFaceMachine(multipartFile, "stranger");
		}
		long snapTime = jsonObject.getLongValue("snapTime");
		VisitorStrangerEntity entity = new VisitorStrangerEntity();
		entity.setId(SnowFlake.nextId());
		entity.setCommunityId(jsonObject.getLong("communityId"));
		entity.setSnapUrl(snapUrl);
		entity.setSnapTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(snapTime), ZoneId.of("GMT+8")));
		entity.setMachineId(jsonObject.getString("machineId"));
		//TODO 查询设备名称
		visitorStrangerMapper.insert(entity);
	}
	
	/**
	* @Description: 陌生人记录 分页查询
	 * @Param: [qo]
	 * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.VisitorStrangerEntity>
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	@Override
	public PageInfo<VisitorStrangerEntity> queryStrangerPage(BaseQO<VisitorStrangerEntity> qo){
		Page<VisitorStrangerEntity> page = new Page<>();
		MyPageUtils.setPageAndSize(page,qo);
		VisitorStrangerEntity query = qo.getQuery();
		QueryWrapper<VisitorStrangerEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id,snap_time,snap_url,machine_name");
		queryWrapper.eq("community_id",query.getCommunityId());
		if(!StringUtils.isEmpty(query.getMachineName())){
			queryWrapper.like("machine_name",query.getMachineName());
		}
		Page<VisitorStrangerEntity> pageData = visitorStrangerMapper.selectPage(page,queryWrapper);
		PageInfo<VisitorStrangerEntity> pageInfo = new PageInfo<>();
		BeanUtils.copyProperties(pageData,pageInfo);
		return pageInfo;
	}

	/**
	 * @param visitorEntity : 访客表实体
	 * @author: Pipi
	 * @description: 添加访客邀请
	 * @return: java.lang.Integer
	 * @date: 2021/8/13 14:17
	 **/
	@Override
	public Integer addVisitor(VisitorEntity visitorEntity) {
		visitorEntity.setId(SnowFlake.nextId());
		int resultNum = visitorMapper.insert(visitorEntity);
		// 添加成功且有人脸信息
		if (!StringUtils.isEmpty(visitorEntity.getFaceUrl()) && resultNum > 0) {
			JSONObject pushMap = new JSONObject();
			pushMap.put("op","editPerson");
			pushMap.put("uid",visitorEntity.getContact());
			pushMap.put("faceUrl",visitorEntity.getFaceUrl());
			HashSet<Object> hashSet = new HashSet<>();
			hashSet.add(visitorEntity.getCommunityId());
			pushMap.put("communityIdSet", hashSet);
			pushMap.put("sex", 0);
			pushMap.put("realName",visitorEntity.getName());
			rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, pushMap);
		}
		return resultNum;
	}
}
