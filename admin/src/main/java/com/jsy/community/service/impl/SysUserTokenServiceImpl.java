package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.SysUserTokenEntity;
import com.jsy.community.mapper.SysUserTokenMapper;
import com.jsy.community.service.ISysUserTokenService;
import com.jsy.community.utils.TokenGenerator;
import com.jsy.community.vo.CommonResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenMapper, SysUserTokenEntity> implements ISysUserTokenService {
	//12小时后过期
	private final static int EXPIRE_IN_HOUR = 12;
	
	
	@Override
	public CommonResult<Object> createToken(long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		
		//当前时间
		LocalDateTime now = LocalDateTime.now();
		//过期时间
		LocalDateTime expireTime = now.plusHours(EXPIRE_IN_HOUR);
		
		//判断是否生成过token(没有就生成，有就更新过期时间)
		SysUserTokenEntity tokenEntity = this.getById(userId);
		if (tokenEntity == null) {
			tokenEntity = new SysUserTokenEntity();
			tokenEntity.setUserId(userId);
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);
			
			//保存token
			this.save(tokenEntity);
		} else {
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);
			
			//更新token
			this.updateById(tokenEntity);
		}
		
		Map<String, Object> result = new HashMap<>(2);
		result.put("token", token);
		result.put("expire", EXPIRE_IN_HOUR * 3600);
		return CommonResult.ok(result);
	}
	
	@Override
	public void logout(long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		
		//修改token
		SysUserTokenEntity tokenEntity = new SysUserTokenEntity();
		tokenEntity.setUserId(userId);
		tokenEntity.setToken(token);
		this.updateById(tokenEntity);
	}
}
