package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IAdminUserTokenService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.admin.AdminUserTokenEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.AdminUserTokenMapper;
import com.jsy.community.util.RedisUtils;
import com.jsy.community.util.TokenGenerator;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService(version = Const.version, group = Const.group_property)
public class AdminUserTokenServiceImpl extends ServiceImpl<AdminUserTokenMapper, AdminUserTokenEntity> implements IAdminUserTokenService {

	@Resource
	private RedisUtils redisUtils;
	
	@Override
	public String createToken(AdminUserEntity user) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		
		try {
			redisUtils.setUserToken(token,user);
		} catch (Exception e){
			throw new PropertyException(JSYError.INTERNAL.getCode(),"redis超时");
		}
		
		return token;
	}
	
}
