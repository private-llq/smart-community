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
import com.jsy.community.vo.CommonResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenMapper, SysUserTokenEntity> implements ISysUserTokenService {

	@Resource
	private RedisUtils redisUtils;
	
	@Override
	public CommonResult<Object> createToken(SysUserEntity user) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		
		try {
			redisUtils.setUserToken(token,user);
		} catch (Exception e){
			throw new JSYException(JSYError.INTERNAL);
		}
		
		Map<String, Object> result = new HashMap<>(1);
		result.put("token", token);
		return CommonResult.ok(result);
	}
	
}
