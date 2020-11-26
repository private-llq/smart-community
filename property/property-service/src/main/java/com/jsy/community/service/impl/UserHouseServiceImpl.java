package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
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
import com.jsy.community.utils.PageVoUtils;
import com.jsy.community.vo.UserHouseVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业主房屋认证 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class UserHouseServiceImpl extends ServiceImpl<UserHouseMapper, UserHouseEntity> implements IUserHouseService {
	
	@Autowired
	private UserHouseMapper userHouseMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private CommunityMapper communityMapper;
	
	@Autowired
	private HouseMapper houseMapper;
	
	@Override
	public PageInfo<UserHouseVo> selectUserHouse(BaseQO<UserHouseEntity> baseQO, Long communityId) {
		List<UserHouseVo> userHouseVos = new ArrayList<>();
		
		QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		wrapper.orderByAsc("check_status");
		List<UserHouseEntity> list = userHouseMapper.selectList(wrapper);
		
		for (UserHouseEntity userHouseEntity : list) {
			UserHouseVo houseVo = new UserHouseVo();
			
			// 业主房屋认证信息
			BeanUtils.copyProperties(userHouseEntity, houseVo);
			
			// 业主名称
			Long uid = userHouseEntity.getUid();
			UserEntity userEntity = userMapper.selectById(uid);
			String nickname = userEntity.getNickname();
			houseVo.setNickname(nickname);
			
			// 所属社区
			CommunityEntity communityEntity = communityMapper.selectById(communityId);
			String name = communityEntity.getName();
			houseVo.setName(name);
			
			// 社区楼栋信息
			Long houseId = userHouseEntity.getHouseId();
			HouseEntity houseEntity = houseMapper.selectById(houseId);
			BeanUtils.copyProperties(houseEntity, houseVo);
			
			userHouseVos.add(houseVo);
		}
		
		long current = baseQO.getPage();
		long size = baseQO.getSize();
		long total = list.size();
		return PageVoUtils.page(current, total, size, userHouseVos);
	}
	
	@Override
	public Boolean saveUserHouse(Long uid, Long communityId, Long houseId) {
		UserHouseEntity houseEntity = new UserHouseEntity();
		houseEntity.setUid(uid);
		houseEntity.setCommunityId(communityId);
		houseEntity.setHouseId(houseId);
		houseEntity.setCheckStatus(2);//审核中
		int res = userHouseMapper.insert(houseEntity);
		return res > 0;
	}
	
	@Override
	public Boolean pass(Long id) {
		UserHouseEntity houseEntity = userHouseMapper.selectById(id);
		if (houseEntity != null) {
			houseEntity.setCheckStatus(1);
			userHouseMapper.updateById(houseEntity);
			return true;
		}
		return false;
	}
	
	@Override
	public Boolean notPass(Long id) {
		UserHouseEntity houseEntity = userHouseMapper.selectById(id);
		if (houseEntity != null) {
			houseEntity.setCheckStatus(0);
			userHouseMapper.updateById(houseEntity);
			return true;
		}
		return false;
	}
}
