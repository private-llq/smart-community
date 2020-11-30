package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.UserHouseVO;
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
 * @author lihao
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
	public PageInfo<UserHouseVO> selectUserHouse(BaseQO<UserHouseEntity> baseQO, Long communityId) {
		List<UserHouseVO> userHouseVOS = new ArrayList<>();
		
		QueryWrapper<UserHouseEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("community_id", communityId);
		wrapper.orderByAsc("check_status");
		Page<UserHouseEntity> page = new Page<>(baseQO.getPage(),baseQO.getSize());
		List<UserHouseEntity> list = userHouseMapper.selectPage(page,wrapper).getRecords();
		
		for (UserHouseEntity userHouseEntity : list) {
			UserHouseVO houseVo = new UserHouseVO();
			
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
			
			userHouseVOS.add(houseVo);
		}
		
		long current = baseQO.getPage();
		long size = baseQO.getSize();
		long total = list.size();
		return PageVoUtils.page(current, total, size, userHouseVOS);
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
			// 修改审核状态    PS：业主与房屋的关系是他们做的  在审核之前就绑定了关系   所以这里审核只是把状态改下  2020年11月30日10:04:18
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
	
	@Override
	public Boolean removeUserHouse(Long id) {
		int count = userHouseMapper.deleteById(id);
		return count!=0;
	}
}
