package com.jsy.community.service.impl;
import java.time.LocalDateTime;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.entity.CommunityHardWareEntity;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserFaceSyncRecordEntity;
import com.jsy.community.mapper.CommunityHardWareMapper;
import com.jsy.community.mapper.UserFaceSyncRecordMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 业主 服务实现类
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group_property)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseMemberService houseMemberService;

	@Autowired
	private UserFaceSyncRecordMapper userFaceSyncRecordMapper;

	@Autowired
	private CommunityHardWareMapper hardWareMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override
	public UserEntity selectOne(String uid) {
		QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid",uid);
		UserEntity userEntity = baseMapper.selectOne(wrapper);
		return userEntity;
	}

	@Override
	public UserEntity queryUserDetailByUid(String uid) {
		return null;
	}

	@Override
	public String selectUserUID(String phone, String username) {
		QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("mobile",phone);
		wrapper.eq("real_name",username);
		UserEntity userEntity = baseMapper.selectOne(wrapper);
		String UUID="";
		if (userEntity!=null) {
			UUID=userEntity.getUid();
		}


		return UUID;
	}

	//此方法不会进入，因为本端有同名Service，所以必须实现
	@Override
	public Map<String, Map<String,String>> queryNameByUidBatch(Collection<String> uids){
		return null;
	}
	//此方法不会进入，因为本端有同名Service，所以必须实现
	@Override
	public List<String> queryUidOfNameLike(List<String> uids, String nameLike){
		return null;
	}

	/**
	 * @param communityId : 社区ID
	 * @param facilityId: 设备序列号
	 * @author: Pipi
	 * @description: 查询社区未同步的人脸信息
	 * @return: java.util.List<com.jsy.community.entity.UserEntity>
	 * @date: 2021/8/19 15:41
	 **/
	@Override
	public List<UserEntity> queryUnsyncFaceUrlList(Long communityId, String facilityId) {
		// 查询所有需要同步的数据
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.isNotNull("face_url");
		// 查询所有该设备已同步的人脸信息
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
		recordEntityQueryWrapper.eq("community_id", communityId);
		recordEntityQueryWrapper.eq("facility_id", facilityId);
		List<UserFaceSyncRecordEntity> faceSyncRecordEntities = userFaceSyncRecordMapper.selectList(recordEntityQueryWrapper);
		if (!CollectionUtils.isEmpty(faceSyncRecordEntities)) {
			List<String> faceUrlList = new ArrayList<>();
			for (UserFaceSyncRecordEntity faceSyncRecordEntity : faceSyncRecordEntities) {
				faceUrlList.add(faceSyncRecordEntity.getFaceUrl());
			}
			queryWrapper.notIn("face_url", faceUrlList);
		}
		List<UserEntity> userEntities = new ArrayList<>();
		// 查询社区所有的人
		List<HouseMemberEntity> houseMemberEntities = houseMemberService.queryByCommunityId(communityId);
		// 获取所有人的uid
		if (!CollectionUtils.isEmpty(houseMemberEntities)) {
			HashSet<String> uidSet = new HashSet<>();
			for (HouseMemberEntity houseMemberEntity : houseMemberEntities) {
				uidSet.add(houseMemberEntity.getUid());
			}
			queryWrapper.in("uid", uidSet);
		} else {
			// 社区没有人,返回空
			return new ArrayList<UserEntity>();
		}
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * @param baseQO : 查询条件
	 * @author: Pipi
	 * @description: 人脸管理查询人脸分页列表
	 * @return: java.util.List<com.jsy.community.entity.UserEntity>
	 * @date: 2021/9/8 16:35
	 **/
	@Override
	public PageInfo<UserEntity> facePageList(BaseQO<UserEntity> baseQO) {
		PageInfo<UserEntity> pageInfo = new PageInfo<>();
		Long startNum = (baseQO.getPage() - 1) * baseQO.getSize();
		List<UserEntity> userEntityList = baseMapper.queryFacePageList(baseQO.getQuery(), startNum, baseQO.getSize());
		if (!CollectionUtils.isEmpty(userEntityList)) {
			Set<String> uidSet = new HashSet<>();
			for (UserEntity userEntity : userEntityList) {
				uidSet.add(userEntity.getUid());
			}
			List<HouseMemberEntity> houseMemberEntities = houseMemberService.queryByCommunityIdAndUids(baseQO.getQuery().getCommunityId(), uidSet);
			Map<String, Set<String>> relationMap = new HashMap<>();
			for (HouseMemberEntity houseMemberEntity : houseMemberEntities) {
				String relationStr = BusinessEnum.RelationshipEnum.getCodeName(houseMemberEntity.getRelation());
				if (relationMap.containsKey(houseMemberEntity.getUid())) {
					relationMap.get(houseMemberEntity.getUid()).add(relationStr);
				} else {
					Set<String> relationString = new HashSet<>();
					relationString.add(relationStr);
					relationMap.put(houseMemberEntity.getUid(), relationString);
				}
			}
			for (UserEntity userEntity : userEntityList) {
				userEntity.setRelationSet(relationMap.get(userEntity.getUid()));
			}
		}
		Integer count = 0;
		count = baseMapper.queryFacePageListCount(baseQO.getQuery()) == null ? 0 : baseMapper.queryFacePageListCount(baseQO.getQuery());
		pageInfo.setSize(baseQO.getSize());
		pageInfo.setTotal(count);
		pageInfo.setCurrent(baseQO.getPage());
		pageInfo.setRecords(userEntityList);
		return pageInfo;
	}

	/**
	 * @param userEntity :
	 * @author: Pipi
	 * @description: 人脸操作(启用 / 禁用人脸)
	 * @return: java.lang.Integer
	 * @date: 2021/9/22 10:35
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer faceOpration(UserEntity userEntity, Long communityId) {
		// 查询用户信息
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", userEntity.getUid());
		queryWrapper.eq("face_deleted", 0);
		UserEntity userEntityResult = baseMapper.selectOne(queryWrapper);
		if (userEntityResult == null) {
			throw new PropertyException("未找到该用户");
		}
		if (userEntityResult.getFaceEnableStatus() == userEntity.getFaceEnableStatus()) {
			throw new PropertyException("人脸已经启用/禁用,请勿重复操作");
		}
		// 查询设备
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
		if (CollectionUtils.isEmpty(communityHardWareEntities)) {
			throw new PropertyException("需要推送的设备为空");
		}
		Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
		// 删除原有的同步记录
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
		recordEntityQueryWrapper.eq("uid", userEntity.getUid());
		recordEntityQueryWrapper.eq("community_id", communityId);
		userFaceSyncRecordMapper.delete(recordEntityQueryWrapper);
		// 设置人脸启用状态
		userEntityResult.setFaceEnableStatus(userEntity.getFaceEnableStatus());
		// 更新人脸启用状态
		int updateResult = baseMapper.updateById(userEntityResult);

		// 发送消息到消息队列
		if (updateResult == 1) {
			XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
			if (userEntity.getFaceEnableStatus() == 1) {
				// 启用操作
				xuFaceEditPersonDTO.setOperator("editPerson");
				xuFaceEditPersonDTO.setName(userEntityResult.getRealName());
				xuFaceEditPersonDTO.setPersonType(0);
				xuFaceEditPersonDTO.setTempCardType(0);
				xuFaceEditPersonDTO.setPicURI(userEntityResult.getFaceUrl());
				// 新增同步记录
				List<UserFaceSyncRecordEntity> userFaceSyncRecordEntities = new ArrayList<>();
				for (String hardwareId : hardwareIds) {
					UserFaceSyncRecordEntity userFaceSyncRecordEntity = new UserFaceSyncRecordEntity();
					userFaceSyncRecordEntity.setUid(userEntity.getUid());
					userFaceSyncRecordEntity.setCommunityId(communityId);
					userFaceSyncRecordEntity.setFaceUrl(userEntityResult.getFaceUrl());
					userFaceSyncRecordEntity.setFacilityId(hardwareId);
					userFaceSyncRecordEntity.setId(SnowFlake.nextId());
					userFaceSyncRecordEntity.setDeleted(0);
					userFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
					userFaceSyncRecordEntities.add(userFaceSyncRecordEntity);
				}
				userFaceSyncRecordMapper.insertBatchRecord(userFaceSyncRecordEntities);
			} else {
				// 禁用操作
				xuFaceEditPersonDTO.setOperator("DelPerson");
			}
			xuFaceEditPersonDTO.setCustomId(userEntityResult.getMobile());
			xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
			xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
			rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
		}
		return updateResult;
	}

	/**
	 * @author: Pipi
	 * @description: 删除人脸
	 * @param userEntity:
	 * @param communityId:
	 * @return: java.lang.Integer
	 * @date: 2021/9/23 17:34
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer deleteFace(UserEntity userEntity, Long communityId) {
		// 查询用户信息
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", userEntity.getUid());
		queryWrapper.eq("face_deleted", 0);
		UserEntity userEntityResult = baseMapper.selectOne(queryWrapper);
		if (userEntityResult == null) {
			throw new PropertyException("未找到该用户");
		}
		// 删除原有的同步记录
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();;
		recordEntityQueryWrapper.eq("uid", userEntity.getUid());
		recordEntityQueryWrapper.eq("community_id", communityId);
		userFaceSyncRecordMapper.delete(recordEntityQueryWrapper);
		// 设置人脸删除状态
		userEntityResult.setFaceDeleted(1);
		// 更新人脸删除状态
		int updateResult = baseMapper.updateById(userEntityResult);
		// 查询设备
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
		if (!CollectionUtils.isEmpty(communityHardWareEntities) && updateResult == 1) {
			Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
			// 删除小区设备的人脸照片
			XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
			xuFaceEditPersonDTO.setOperator("DelPerson");
			xuFaceEditPersonDTO.setCustomId(userEntityResult.getMobile());
			xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
			xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
			rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
		}
		return updateResult;
	}

	/**
	 * @param userEntity  :
	 * @param communityId :
	 * @author: Pipi
	 * @description: 新增人脸
	 * @return: java.lang.Integer
	 * @date: 2021/9/23 17:58
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer addFace(UserEntity userEntity, Long communityId) {
		// 查询用户信息
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("real_name", userEntity.getRealName());
		queryWrapper.eq("mobile", userEntity.getMobile());
		UserEntity userEntityResult = baseMapper.selectOne(queryWrapper);
		if (userEntityResult == null) {
			throw new PropertyException("未找到该用户");
		}
		// 设置用户人脸
		userEntityResult.setFaceDeleted(0);
		userEntityResult.setFaceUrl(userEntity.getFaceUrl());
		userEntityResult.setFaceEnableStatus(1);
		userEntityResult.setFaceEnableStatus(1);
		// 更新用户人脸
		int updateResult = baseMapper.updateById(userEntityResult);
		// 删除原有的同步记录
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();;
		recordEntityQueryWrapper.eq("uid", userEntity.getUid());
		recordEntityQueryWrapper.eq("community_id", communityId);
		userFaceSyncRecordMapper.delete(recordEntityQueryWrapper);
		// 查询设备
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
		if (!CollectionUtils.isEmpty(communityHardWareEntities) && updateResult == 1) {
			Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
			// 新增同步记录
			ArrayList<UserFaceSyncRecordEntity> recordEntities = new ArrayList<>();
			for (String hardwareId : hardwareIds) {
				UserFaceSyncRecordEntity userFaceSyncRecordEntity = new UserFaceSyncRecordEntity();
				userFaceSyncRecordEntity.setUid(userEntityResult.getUid());
				userFaceSyncRecordEntity.setCommunityId(communityId);
				userFaceSyncRecordEntity.setFaceUrl(userEntityResult.getFaceUrl());
				userFaceSyncRecordEntity.setFacilityId(hardwareId);
				userFaceSyncRecordEntity.setId(SnowFlake.nextId());
				userFaceSyncRecordEntity.setDeleted(0);
				userFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
				recordEntities.add(userFaceSyncRecordEntity);
			}
			userFaceSyncRecordMapper.insertBatchRecord(recordEntities);
			// 启用人脸
			XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
			xuFaceEditPersonDTO.setOperator("editPerson");
			xuFaceEditPersonDTO.setName(userEntityResult.getRealName());
			xuFaceEditPersonDTO.setPersonType(0);
			xuFaceEditPersonDTO.setTempCardType(0);
			xuFaceEditPersonDTO.setPicURI(userEntityResult.getFaceUrl());
			xuFaceEditPersonDTO.setCustomId(userEntityResult.getMobile());
			xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
			xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
			rabbitTemplate.convertAndSend(TopicExConfig.EX_FACE_XU, TopicExConfig.TOPIC_FACE_XU_SERVER, JSON.toJSONString(xuFaceEditPersonDTO));
		}
		return updateResult;
	}
}
