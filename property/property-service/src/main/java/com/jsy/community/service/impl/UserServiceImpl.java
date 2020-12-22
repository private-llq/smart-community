package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;

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
}
