package com.jsy.community.service.impl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.api.PropertyUserService;
import com.jsy.community.config.PropertyTopicNameEntity;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceEditPersonDTO;
import com.jsy.community.entity.*;
import com.jsy.community.mapper.CommunityHardWareMapper;
import com.jsy.community.mapper.UserFaceMapper;
import com.jsy.community.mapper.UserFaceSyncRecordMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 业主 服务实现类
 * @author YuLF
 * @since 2020-11-25
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyUserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements PropertyUserService {

	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseMemberService houseMemberService;

	@Autowired
	private UserFaceSyncRecordMapper userFaceSyncRecordMapper;

	@Autowired
	private CommunityHardWareMapper hardWareMapper;

	@Autowired
	private UserFaceMapper userFaceMapper;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService baseUserInfoRpcService;
	
	@Override
	public UserEntity selectOne(String uid) {
		UserDetail userDetail = baseUserInfoRpcService.getUserDetail(uid);
		RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(uid);
		UserEntity userEntity = new UserEntity();
		userEntity.setUid(uid);
		userEntity.setIsRealAuth(0);
		if (userDetail != null) {
			userEntity.setNickname(userDetail.getNickName());
			userEntity.setAvatarUrl(userDetail.getAvatarThumbnail());
			userEntity.setMobile(userDetail.getPhone());
			userEntity.setSex(userDetail.getSex());
		}
		if (idCardRealInfo != null) {
			log.info("用户详情实名");
			userEntity.setRealName(idCardRealInfo.getIdCardName());
			userEntity.setIdCard(idCardRealInfo.getIdCardNumber());
			userEntity.setIsRealAuth(2);
		}
		log.info("用户详情:{}", userEntity);

		/*QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("uid",uid);
		UserEntity userEntity = baseMapper.selectOne(wrapper);*/
		return userEntity;
	}

	@Override
	public UserEntity queryUserDetailByUid(String uid) {
		return null;
	}

	@Override
	public String selectUserUID(String phone, String username) {
		Set<String> strings = baseUserInfoRpcService.queryRealUserDetail(phone, username);
		if (!CollectionUtils.isEmpty(strings) && strings.size() != 1) {
			throw new PropertyException("查找到的相关用户不唯一");
		}
		String UUID="";
		if (!CollectionUtils.isEmpty(strings)) {
			UUID = strings.iterator().next();
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
		QueryWrapper<UserFaceEntity> queryWrapper = new QueryWrapper<>();
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
		List<UserFaceEntity> userFaceEntities = userFaceMapper.selectList(queryWrapper);
		if (!CollectionUtils.isEmpty(userFaceEntities)) {
			List<String> uidList = userFaceEntities.stream().map(UserFaceEntity::getUid).collect(Collectors.toList());
			Map<String, RealUserDetail> uidMap = getRealUserDetailsMapByUid(uidList);
			for (UserFaceEntity userFaceEntity : userFaceEntities) {
				UserEntity userEntity = new UserEntity();
				BeanUtils.copyProperties(userFaceEntity, userEntity);
				RealUserDetail realUserDetail = uidMap.get(userFaceEntity.getUid());
				if (realUserDetail != null) {
					userEntity.setRealName(realUserDetail.getRealName());
					userEntity.setMobile(realUserDetail.getPhone());
				}
				userEntities.add(userEntity);
			}
		}
		return userEntities;
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
		Set<String> uidList = new HashSet<>();
		if (StringUtil.isNotBlank(baseQO.getQuery().getKeyword())) {
			uidList = baseUserInfoRpcService.queryRealUserDetail(baseQO.getQuery().getKeyword(), baseQO.getQuery().getKeyword());
			if (CollectionUtils.isEmpty(uidList)) {
				return pageInfo;
			}
		}
		List<UserFaceEntity> userFaceEntities = userFaceMapper.queryFacePageList(baseQO.getQuery(), uidList, startNum, baseQO.getSize());
		List<UserEntity> userEntities = new ArrayList<>();
		if (!CollectionUtils.isEmpty(userFaceEntities)) {
			Set<String> uidSet = userFaceEntities.stream().map(UserFaceEntity::getUid).collect(Collectors.toSet());
			Map<String, RealUserDetail> uidMap = getRealUserDetailsMapByUid(uidSet);
			for (UserFaceEntity userEntity : userFaceEntities) {
				UserEntity userEntity1 = new UserEntity();
				BeanUtils.copyProperties(userEntity, userEntity1);
				RealUserDetail realUserDetail = uidMap.get(userEntity.getUid());
				if (realUserDetail != null) {
					userEntity1.setMobile(realUserDetail.getPhone());
					userEntity1.setRealName(realUserDetail.getRealName());
				}
				userEntities.add(userEntity1);
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
			for (UserEntity userEntity : userEntities) {
				if (String.valueOf(userEntity.getId()).equals(userEntity.getUid())) {
					HashSet<String> hashSet = new HashSet<>();
					hashSet.add("物业");
					userEntity.setRelationSet(hashSet);
				} else {
					userEntity.setRelationSet(relationMap.get(userEntity.getUid()));
				}
			}
		}
		Integer count = userFaceMapper.queryFacePageListCount(baseQO.getQuery(), uidList);
		count = count == null ? 0 : count;
		pageInfo.setSize(baseQO.getSize());
		pageInfo.setTotal(count);
		pageInfo.setCurrent(baseQO.getPage());
		pageInfo.setRecords(userEntities);
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
		QueryWrapper<UserFaceEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", userEntity.getUid());
		UserFaceEntity userEntityResult = userFaceMapper.selectOne(queryWrapper);
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
		int updateResult = userFaceMapper.updateById(userEntityResult);

		// 发送消息到消息队列
		if (updateResult == 1) {
			XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
			if (userEntity.getFaceEnableStatus() == 1) {
				// 启用操作
				xuFaceEditPersonDTO.setOperator("editPerson");
				RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(userEntity.getUid());
				if (idCardRealInfo != null) {
					xuFaceEditPersonDTO.setName(idCardRealInfo.getIdCardName());
				}
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
					userFaceSyncRecordEntity.setDeleted(0L);
					userFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
					userFaceSyncRecordEntities.add(userFaceSyncRecordEntity);
				}
				userFaceSyncRecordMapper.insertBatchRecord(userFaceSyncRecordEntities);
			} else {
				// 禁用操作
				xuFaceEditPersonDTO.setOperator("DelPerson");
			}
			UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userEntity.getUid());
			if (userDetail != null) {
				xuFaceEditPersonDTO.setCustomId(userDetail.getPhone());
			}
			xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
			xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
			rabbitTemplate.convertAndSend(PropertyTopicNameEntity.exFaceXu, PropertyTopicNameEntity.topicFaceXuServer, JSON.toJSONString(xuFaceEditPersonDTO));
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
		QueryWrapper<UserFaceEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", userEntity.getUid());
		UserFaceEntity userEntityResult = userFaceMapper.selectOne(queryWrapper);
		if (userEntityResult == null) {
			log.info("未找到该用户");
			return 0;
		}
		// 删除原有的同步记录
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();;
		recordEntityQueryWrapper.eq("uid", userEntity.getUid());
		recordEntityQueryWrapper.eq("community_id", communityId);
		userFaceSyncRecordMapper.delete(recordEntityQueryWrapper);
		// 删除人脸
		int updateResult = baseMapper.deleteById(userEntityResult);
		// 查询设备
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
		if (!CollectionUtils.isEmpty(communityHardWareEntities) && updateResult == 1) {
			Set<String> hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
			// 删除小区设备的人脸照片
			UserDetail userDetail = baseUserInfoRpcService.getUserDetail(userEntity.getUid());
			if (userDetail != null) {
				deleteFaceMechine(userDetail.getPhone(), String.valueOf(communityId), hardwareIds);
			}
		}
		return updateResult;
	}

	/**
	 * @author: Pipi
	 * @description: 删除人脸设备的人脸
	 * @param mobile: 用户电话
	     * @param communityId: 小区ID
	     * @param hardwareIds: 设备序列号列表
	 * @return:
	 * @date: 2021/12/22 18:58
	 **/
	public void deleteFaceMechine(String mobile, String communityId, Set<String> hardwareIds) {
		XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
		xuFaceEditPersonDTO.setOperator("DelPerson");
		xuFaceEditPersonDTO.setCustomId(mobile);
		xuFaceEditPersonDTO.setHardwareIds(hardwareIds);
		xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
		rabbitTemplate.convertAndSend(PropertyTopicNameEntity.exFaceXu, PropertyTopicNameEntity.topicFaceXuServer, JSON.toJSONString(xuFaceEditPersonDTO));
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
		Set<String> uidSet = baseUserInfoRpcService.queryRealUserDetail(userEntity.getMobile(), userEntity.getRealName());
		if (CollectionUtils.isEmpty(uidSet)) {
			throw new PropertyException("未找到该用户");
		}
		if (uidSet.size() != 1) {
			throw new PropertyException("找到的用户不唯一");
		}
		String uid = uidSet.iterator().next();
		QueryWrapper<UserFaceEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("uid", uid);
		UserFaceEntity userEntityResult = userFaceMapper.selectOne(queryWrapper);
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityId(communityId);
		Set<String> hardwareIds = new HashSet<>();
		if (!CollectionUtils.isEmpty(communityHardWareEntities)) {
			hardwareIds = communityHardWareEntities.stream().map(CommunityHardWareEntity::getHardwareId).collect(Collectors.toSet());
		}
		if (userEntityResult != null) {
			userFaceMapper.deleteById(userEntityResult.getId());
			if (!CollectionUtils.isEmpty(hardwareIds)) {
				deleteFaceMechine(userEntity.getMobile(), String.valueOf(communityId), hardwareIds);
			}
		}
		// 设置用户人脸
		UserFaceEntity userFaceEntity = new UserFaceEntity();
		userFaceEntity.setFaceUrl(userEntity.getFaceUrl());
		userFaceEntity.setUid(uid);
		userFaceEntity.setFaceEnableStatus(userEntity.getFaceEnableStatus());
		userFaceEntity.setId(SnowFlake.nextId());
		// 新增用户人脸
		int updateResult = userFaceMapper.insert(userFaceEntity);
		if (userEntity.getFaceEnableStatus() == 1) {
			syncFace(userEntity, new ArrayList<>(Arrays.asList(communityId)));
		}
		return updateResult;
	}

	/**
	 * @param userEntity   :
	 * @param communityIds :
	 * @author: Pipi
	 * @description: 下发用户人脸数据操作
	 * @return: void
	 * @date: 2021/10/8 17:58
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void syncFace(UserEntity userEntity, List<Long> communityIds) {
		// 删除原有的同步记录
		QueryWrapper<UserFaceSyncRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();;
		recordEntityQueryWrapper.eq("uid", userEntity.getUid());
		recordEntityQueryWrapper.in("community_id", communityIds);
		userFaceSyncRecordMapper.delete(recordEntityQueryWrapper);
		// 查询设备
		List<CommunityHardWareEntity> communityHardWareEntities = hardWareMapper.selectAllByCommunityIds(communityIds);
		if (!CollectionUtils.isEmpty(communityHardWareEntities)) {
			Map<Long, Set<String>> hardwareMap = new HashMap<>();
			for (CommunityHardWareEntity communityHardWareEntity : communityHardWareEntities) {
				if (hardwareMap.containsKey(communityHardWareEntity.getCommunityId())) {
					hardwareMap.get(communityHardWareEntity.getCommunityId()).add(communityHardWareEntity.getHardwareId());
				} else {
					Set<String> hardwareIds = new HashSet<>();
					hardwareIds.add(communityHardWareEntity.getHardwareId());
					hardwareMap.put(communityHardWareEntity.getCommunityId(), hardwareIds);
				}
			}
			// 新增同步记录
			ArrayList<UserFaceSyncRecordEntity> recordEntities = new ArrayList<>();
			for (Long communityId : hardwareMap.keySet()) {
				for (String hardwareId : hardwareMap.get(communityId)) {
					UserFaceSyncRecordEntity userFaceSyncRecordEntity = new UserFaceSyncRecordEntity();
					userFaceSyncRecordEntity.setUid(userEntity.getUid());
					userFaceSyncRecordEntity.setCommunityId(communityId);
					userFaceSyncRecordEntity.setFaceUrl(userEntity.getFaceUrl());
					userFaceSyncRecordEntity.setFacilityId(hardwareId);
					userFaceSyncRecordEntity.setId(SnowFlake.nextId());
					userFaceSyncRecordEntity.setDeleted(0L);
					userFaceSyncRecordEntity.setCreateTime(LocalDateTime.now());
					recordEntities.add(userFaceSyncRecordEntity);
				}
				// 下发人脸到设备
				XUFaceEditPersonDTO xuFaceEditPersonDTO = new XUFaceEditPersonDTO();
				xuFaceEditPersonDTO.setOperator("editPerson");
				xuFaceEditPersonDTO.setName(userEntity.getRealName());
				xuFaceEditPersonDTO.setPersonType(0);
				xuFaceEditPersonDTO.setTempCardType(0);
				xuFaceEditPersonDTO.setPicURI(userEntity.getFaceUrl());
				xuFaceEditPersonDTO.setCustomId(userEntity.getMobile());
				xuFaceEditPersonDTO.setHardwareIds(hardwareMap.get(communityId));
				xuFaceEditPersonDTO.setCommunityId(String.valueOf(communityId));
				rabbitTemplate.convertAndSend(PropertyTopicNameEntity.exFaceXu, PropertyTopicNameEntity.topicFaceXuServer, JSON.toJSONString(xuFaceEditPersonDTO));
			}
			userFaceSyncRecordMapper.insertBatchRecord(recordEntities);
		}
	}

	/**
	 * @author: Pipi
	 * @description: 通过用户uid列表获取用户电话号码和实名信息
	 * @param uidList: 用户uid列表
	 * @return: {@link Map< String, RealUserDetail>}
	 * @date: 2021/12/23 14:06
	 **/
	public Map<String, RealUserDetail> getRealUserDetailsMapByUid(Collection<String> uidList) {
		List<RealUserDetail> realUserDetails = baseUserInfoRpcService.getRealUserDetails(uidList);
		return realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
	}
}
