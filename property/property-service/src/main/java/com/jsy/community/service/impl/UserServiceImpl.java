package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseMemberService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseMemberEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserFaceSyncRecordEntity;
import com.jsy.community.mapper.UserFaceSyncRecordMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
	public Integer faceOpration(UserEntity userEntity) {
		QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("real_name", userEntity.getRealName());
		queryWrapper.eq("mobile", userEntity.getMobile());
		UserEntity userEntityResult = baseMapper.selectOne(queryWrapper);
		if (userEntityResult.getFaceEnableStatus() == userEntity.getFaceEnableStatus()) {
			throw new PropertyException("人脸已经启用/禁用,请勿重复操作");
		}
		if (userEntity.getFaceEnableStatus() == 1) {
			// 启用操作
		} else {
			// 禁用操作
		}
		return null;
	}
}
