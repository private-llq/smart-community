package com.jsy.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * 业主实现
 *
 * @author ling
 * @since 2020-11-11 18:12
 */
@DubboService(version = Const.version, group = Const.group)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserAuthService userAuthService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommonService commonService;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public UserInfoVo login(LoginQO qo) {
		Long uid = userAuthService.checkUser(qo);
		UserEntity user = baseMapper.queryUserInfoByUid(uid);
		if (user.getDeleted() == 1) {
			throw new ProprietorException("账号不存在");
		}
		
		UserInfoVo userInfoVo = new UserInfoVo();
		BeanUtil.copyProperties(user, userInfoVo);
		
		// 设置省市区
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		if (user.getProvinceId() != null) {
			userInfoVo.setProvince(ops.get(user.getProvinceId().toString()));
		}
		if (user.getCityId() != null) {
			userInfoVo.setCity(ops.get(user.getCityId().toString()));
		}
		if (user.getAreaId() != null) {
			userInfoVo.setArea(ops.get(user.getAreaId().toString()));
		}
		
		return userInfoVo;
	}
	
	@Override
	public UserInfoVo register(RegisterQO qo) {
		commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
		
		// 添加业主
		UserEntity user = new UserEntity();
		save(user);
		
		// 添加认证数据
		UserAuthEntity userAuth = new UserAuthEntity();
		userAuth.setUid(user.getId());
		if (RegexUtils.isMobile(qo.getAccount())) {
			userAuth.setMobile(qo.getAccount());
		} else if (RegexUtils.isEmail(qo.getAccount())) {
			userAuth.setEmail(qo.getAccount());
		} else {
			userAuth.setUsername(qo.getAccount());
		}
		userAuthService.save(userAuth);
		
		UserInfoVo vo = new UserInfoVo();
		vo.setId(user.getId());
		vo.setSex(0);
		vo.setIsRealAuth(0);
		return vo;
	}
}