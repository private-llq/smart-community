package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.ICaptchaService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.entity.SmsEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.mapper.SmsMapper;
import com.jsy.community.utils.SmsUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author ling
 * @since 2020-11-12 10:39
 */
@DubboService(version = Const.version, group = Const.group_property)
@RefreshScope
public class CaptchaServiceImpl implements ICaptchaService {
	
	@Value(value = "${jsy.mobileCodeExpiredTime}")
	private Integer mobileExpiredTime;
	
	@Resource
	private SmsMapper smsMapper;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
	@Override
	public boolean sendMobile(String mobile, Integer type) {
		if (type == UserAuthEntity.CODE_TYPE_REGISTER || type == UserAuthEntity.CODE_TYPE_CHANGE_MOBILE) {
			// 验证是否已存在
			boolean isExists = adminUserService.isExistsByMobile(mobile);
			if (isExists) {
				throw new PropertyException("您已注册账号");
			}
		} else if (type == UserAuthEntity.CODE_TYPE_LOGIN || type == UserAuthEntity.CODE_TYPE_FORGET_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PWD || type == UserAuthEntity.CODE_TYPE_CHANGE_PAY_PWD) {
			boolean isExists = adminUserService.isExistsByMobile(mobile);
			if (!isExists) {
				throw new PropertyException("您还没有注册");
			}
		} else{
			throw new PropertyException("不支持的验证码类型");
		}
		
		//发短信
		SmsEntity smsEntity = smsMapper.selectOne(new QueryWrapper<SmsEntity>().eq("deleted", 0));
		ConstClasses.AliYunDataEntity.setConfig(smsEntity);
		String code = SmsUtil.sendVcode(mobile, BusinessConst.SMS_VCODE_LENGTH_DEFAULT, smsEntity.getSmsSign());
		//5分钟有效期
		redisTemplate.opsForValue().set("vCodeAdmin:" + mobile, code, mobileExpiredTime, TimeUnit.MINUTES);
		
		// 验证码暂时固定1111
//		String code = "1111";
//		redisTemplate.opsForValue().set("vCode:" + mobile, code); //测试阶段不过期
		
		return true;
	}
	
}
