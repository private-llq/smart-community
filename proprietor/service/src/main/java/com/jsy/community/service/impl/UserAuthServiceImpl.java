package com.jsy.community.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@DubboService(version = Const.version, group = Const.group)
@Slf4j
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthEntity> implements IUserAuthService {
	
	@Resource
	private RedisLockRegistry redisLockRegistry;
	
	@Resource
	private ICommonService commonService;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public List<UserAuthEntity> getList(boolean a) {
		Lock lock = null;
		try {
			lock = redisLockRegistry.obtain("lock");
			boolean b1 = lock.tryLock(3, TimeUnit.SECONDS);
			log.info("b1 is : {}", b1);
			
			TimeUnit.SECONDS.sleep(5);
			
			boolean b2 = lock.tryLock(3, TimeUnit.SECONDS);
			log.info("b2 is : {}", b2);
		} catch (InterruptedException ignore) {
		} finally {
			if (lock != null) {
				lock.unlock();
				lock.unlock();
			}
		}
		return null;
	}
	
	@Override
	public Long checkUser(LoginQO qo) {
		String field;
		if (RegexUtils.isMobile(qo.getAccount())) {
			// 手机验证码登录
			commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
			return baseMapper.queryUserIdByMobile(qo.getAccount());
		} else {
			if (RegexUtils.isEmail(qo.getAccount())) {
				field = "email";
			} else {
				field = "username";
			}
			UserAuthEntity entity = baseMapper.queryUserByField(qo.getAccount(), field);
			String password = SecureUtil.sha256(qo.getPassword() + entity.getSalt());
			if (password.equals(entity.getPassword())) {
				return entity.getUid();
			} else {
				throw new ProprietorException("账号密码错误");
			}
		}
	}
	
	@Override
	public boolean addPassword(Long uid, AddPasswordQO qo) {
		if (!qo.getPassword().equals(qo.getConfirmPassword())) {
			throw new ProprietorException("密码不一致");
		}
		
		String salt = RandomUtil.randomString(8);
		String encryptedPassword = SecureUtil.sha256(qo.getPassword() + salt);
		
		UserAuthEntity entity = new UserAuthEntity();
		entity.setPassword(encryptedPassword);
		entity.setSalt(salt);
		
		LambdaQueryWrapper<UserAuthEntity> update = new LambdaQueryWrapper<>();
		update.eq(UserAuthEntity::getUid, uid);
		return update(entity, update);
	}
	
	@Override
	public boolean checkUserExists(String account, String field) {
		return baseMapper.checkUserExists(account, field) != null;
	}
	
	@Override
	public boolean resetPassword(ResetPasswordQO qo) {
		if (!qo.getPassword().equals(qo.getConfirmPassword())) {
			throw new ProprietorException("密码不一致");
		}
		
		UserAuthEntity entity;
		if (RegexUtils.isMobile(qo.getAccount())) {
			entity = baseMapper.queryUserByField(qo.getAccount(), "mobile");
		} else if (RegexUtils.isEmail(qo.getAccount())) {
			entity = baseMapper.queryUserByField(qo.getAccount(), "email");
		} else {
			entity = baseMapper.queryUserByField(qo.getAccount(), "username");
		}
		
		if (entity == null) {
			throw new ProprietorException("不存在此账号");
		}
		
		UserAuthEntity update = new UserAuthEntity();
		update.setId(entity.getId());
		update.setPassword(SecureUtil.sha256(qo.getPassword() + entity.getSalt()));
		
		return updateById(update);
	}
}
