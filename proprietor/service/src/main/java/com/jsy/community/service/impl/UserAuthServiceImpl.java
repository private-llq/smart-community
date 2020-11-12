package com.jsy.community.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.qo.proprietor.LoginQO;
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
			String code = redisTemplate.opsForValue().get(qo.getAccount());
			if (code == null) {
				throw new ProprietorException("验证码已失效");
			}
			
			if (!code.equals(qo.getCode())) {
				throw new ProprietorException("验证码错误");
			}
			
			// 验证通过，删除验证码
			redisTemplate.delete(qo.getAccount());
			
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
}
