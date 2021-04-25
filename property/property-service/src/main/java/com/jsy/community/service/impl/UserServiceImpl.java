package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 业主 服务实现类
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group_property)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {
	
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
}
