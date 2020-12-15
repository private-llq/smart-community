package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.admin.SysUserEntity;
import com.jsy.community.entity.admin.SysUserTokenEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.SysUserTokenMapper;
import com.jsy.community.service.ISysUserTokenService;
import com.jsy.community.utils.RedisUtils;
import com.jsy.community.utils.TokenGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenMapper, SysUserTokenEntity> implements ISysUserTokenService {

	@Resource
	private RedisUtils redisUtils;
	
	@Override
	public String createToken(SysUserEntity user) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		
		try {
			redisUtils.setUserToken(token,user);
		} catch (Exception e){
			throw new JSYException(JSYError.INTERNAL);
		}
		
		return token;
	}
	
}
