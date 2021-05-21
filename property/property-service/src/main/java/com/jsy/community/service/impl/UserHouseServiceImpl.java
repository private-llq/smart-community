package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.CommunityMapper;
import com.jsy.community.mapper.HouseMapper;
import com.jsy.community.mapper.UserHouseMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.UserHouseVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业主房屋认证 服务实现类
 * </p>
 *
 * @author lihao
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group_property)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {
	
	@Autowired
	private UserHouseMapper userHouseMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 待审核
	 **/
	private static final Integer WAITPASS = 0;
	
	/**
	 * 已通过
	 **/
	private static final Integer PASS = 1;
	
	/**
	 * 未通过
	 **/
	private static final Integer NOPASS = 2;
	
	@Override
	public PageInfo<UserHouseVO> selectUserHouse(BaseQO<UserHouseEntity> baseQO, Long communityId) {
		List<UserHouseVO> userHouseVOS = new ArrayList<>();
		
		QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		wrapper.orderByAsc("check_status");
		Page<UserHouseEntity> page = new Page<>(baseQO.getPage(), baseQO.getSize());
		List<UserHouseEntity> list = userHouseMapper.selectPage(page, wrapper).getRecords();
		
		
		for (UserHouseEntity userHouseEntity : list) {
			UserHouseVO houseVo = new UserHouseVO();
			
			// 业主房屋认证信息
			BeanUtils.copyProperties(userHouseEntity, houseVo);

			
			// 封装数据
			// 业主名称
			String uid = userHouseEntity.getUid();
			QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("uid", uid);
			UserEntity userEntity = userMapper.selectOne(queryWrapper);
			if (userEntity != null) {
				String nickname = userEntity.getNickname();
				houseVo.setNickname(nickname);
				
				// 所属社区
				CommunityEntity communityEntity = communityMapper.selectById(communityId);
				String name = communityEntity.getName();
				houseVo.setName(name);
				
				// 社区楼栋信息
				Long houseId = userHouseEntity.getHouseId();
				HouseEntity houseEntity = houseMapper.selectById(houseId);
				if (houseEntity != null) {
					// 封装楼栋信息
					BeanUtils.copyProperties(houseEntity, houseVo);
					houseVo.setId(userHouseEntity.getId());
				}
				userHouseVOS.add(houseVo);
			}
		}
		PageInfo<UserHouseVO> info = new PageInfo<>();
		BeanUtils.copyProperties(page, info);
		info.setRecords(userHouseVOS);
		return info;
	}
	
	@Override
	public Boolean saveUserHouse(String uid, List<HouseEntity> houseEntityList) {
		if (!CollectionUtils.isEmpty(houseEntityList)) {
			for (HouseEntity houseEntity : houseEntityList) {
				Long communityId = houseEntity.getCommunityId();
				Long id = houseEntity.getId();
				
				UserHouseEntity userHouseEntity = new UserHouseEntity();
				userHouseEntity.setUid(uid);
				userHouseEntity.setCommunityId(communityId);
				userHouseEntity.setHouseId(id);
				userHouseEntity.setCheckStatus(WAITPASS);//审核中
				userHouseEntity.setId(SnowFlake.nextId());
				
				userHouseMapper.insert(userHouseEntity);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * @return java.lang.Boolean
	 * @Author lihao
	 * @Description 通过认证后，将其信息(基本信息及人脸信息)存入redis。设备同步数据的时候，获取redis里面存入的业主数据将其更新到设备上。
	 * @Date 2021/5/12 10:36
	 * @Param [id]
	 **/
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean pass(Long id) {
		UserHouseEntity houseEntity = userHouseMapper.selectById(id);
		if (houseEntity != null) {
			if (houseEntity.getCheckStatus() != WAITPASS) {
				throw new PropertyException("您选择的房屋不存在或已经审核完成");
			}
			houseEntity.setCheckStatus(PASS);
			// 修改审核状态    PS：业主与房屋的关系是他们做的  在审核之前就绑定了关系   所以这里审核只是把状态改下  2020年11月30日10:04:18
			userHouseMapper.updateById(houseEntity);
			
			// 获取业主的信息，存到redis
			String userId = houseEntity.getUid();
			QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
			wrapper.eq("uid",userId);
			UserEntity userInfo = userMapper.selectOne(wrapper);
			
			String userInfoJson = JSON.toJSONString(userInfo);
			redisTemplate.opsForList().leftPush("userInfo",userInfoJson);
			return true;
		}
		return false;
	}
	
	@Override
	public Boolean notPass(Long id) {
		UserHouseEntity houseEntity = userHouseMapper.selectById(id);
		if (houseEntity != null) {
			if (houseEntity.getCheckStatus() != WAITPASS) {
				throw new PropertyException("您的订单不存在或已经审核完成");
			}
			houseEntity.setCheckStatus(NOPASS);
			userHouseMapper.updateById(houseEntity);
			return true;
		}
		return false;
	}
	
	@Override
	public Boolean removeUserHouse(Long id) {
		int count = userHouseMapper.deleteById(id);
		return count != 0;
	}
	
}
