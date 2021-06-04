package com.jsy.community.service.impl;

import com.jsy.community.api.ICaptchaService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.utils.SmsUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author ling
 * @since 2020-11-12 10:39
 */
@DubboService(version = Const.version, group = Const.group)
@RefreshScope
public class CaptchaServiceImpl implements ICaptchaService {
	
	@Value(value = "${jsy.mobileCodeExpiredTime}")
	private Integer mobileExpiredTime;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserAuthService userAuthService;
	
	@Override
	public boolean sendMobile(String mobile, Integer type) {
		if (type == UserAuthEntity.CODE_TYPE_REGISTER || type == UserAuthEntity.CODE_TYPE_CHANGE_MOBILE) {
			// 验证是否已存在
			boolean isExists = userAuthService.checkUserExists(mobile, "mobile");
			if (isExists) {
				throw new ProprietorException("您已注册账号");
			}
		} else if (type == UserAuthEntity.CODE_TYPE_LOGIN || type == UserAuthEntity.CODE_TYPE_FORGET_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PAY_PWD) {
			boolean isExists = userAuthService.checkUserExists(mobile, "mobile");
			if (!isExists) {
				throw new ProprietorException("您还没有注册");
			}
		} else if(type == UserAuthEntity.CODE_TYPE_BIND_MOBILE){
			//三方登录绑定手机 暂不做处理
		} else{
			throw new ProprietorException("不支持的验证码类型");
		}
		
		//发短信
		String code = SmsUtil.sendVcode(mobile);
		//5分钟有效期
		redisTemplate.opsForValue().set("vCode:" + mobile, code, mobileExpiredTime, TimeUnit.MINUTES);
		
		// 验证码暂时固定1111
//		String code = "1111";
//		redisTemplate.opsForValue().set("vCode:" + mobile, code); //测试阶段不过期
		
		return true;
	}
	
	@Override
	public boolean sendEmail(String email, Integer type) {
		return false;
	}
}
