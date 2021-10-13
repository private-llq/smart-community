package com.jsy.community.service.impl;

import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.service.AdminException;
import com.jsy.community.service.ICaptchaService;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author DKS
 * @since 2021-10-12 16:01
 */
@Slf4j
@Service("captchaService")
public class CaptchaServiceImpl implements ICaptchaService {
	
	@Value(value = "${jsy.mobileCodeExpiredTime}")
	private Integer mobileExpiredTime;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Resource
	private ISysUserService sysUserService;
	
	@Override
	public boolean sendMobile(String mobile, Integer type) {
		if (type == UserAuthEntity.CODE_TYPE_REGISTER || type == UserAuthEntity.CODE_TYPE_CHANGE_MOBILE) {
			// 验证是否已存在
			boolean isExists = sysUserService.isExistsByMobile(mobile);
			if (isExists) {
				throw new AdminException("您已注册账号");
			}
		} else if (type == UserAuthEntity.CODE_TYPE_LOGIN || type == UserAuthEntity.CODE_TYPE_FORGET_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PAY_PWD) {
			boolean isExists = sysUserService.isExistsByMobile(mobile);
			if (!isExists) {
				throw new AdminException("您还没有注册");
			}
		} else{
			throw new AdminException("不支持的验证码类型");
		}
		
		//发短信
		String code = SmsUtil.sendVcode(mobile, BusinessConst.SMS_VCODE_LENGTH_DEFAULT);
		//5分钟有效期
		redisTemplate.opsForValue().set("vCodeAdmin:" + mobile, code, mobileExpiredTime, TimeUnit.MINUTES);
		
		// 验证码暂时固定1111
//		String code = "1111";
//		redisTemplate.opsForValue().set("vCode:" + mobile, code); //测试阶段不过期
		
		return true;
	}
	
}
