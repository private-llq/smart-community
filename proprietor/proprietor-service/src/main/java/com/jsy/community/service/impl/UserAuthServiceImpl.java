package com.jsy.community.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommonService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UserAuthMapper;
import com.jsy.community.qo.ThirdPlatformQo;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.vo.ThirdPlatformVo;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.integration.redis.util.RedisLockRegistry;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService(version = Const.version, group = Const.group)
@Slf4j
@RefreshScope
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuthEntity> implements IUserAuthService {
	@Resource
	private AuthRequestFactory factory;
	
	@Resource
	private RedisLockRegistry redisLockRegistry;
	
	@Resource
	private ICommonService commonService;

	@Resource
	private UserAuthMapper userAuthMapper;
	
	@Value(value = "${jsy.third-platform-domain:http://www.jsy.com}")
	private String callbackUrl;
	
	@Override
	public List<UserAuthEntity> getList(boolean a) {
		return list();
	}
	
	@Override
	public Long checkUser(LoginQO qo) {
		if (RegexUtils.isMobile(qo.getAccount())) {
			if (StrUtil.isNotEmpty(qo.getCode())) {
				// 手机验证码登录
				commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
				return baseMapper.queryUserIdByMobile(qo.getAccount());
			} else {
				return checkUserByPassword(qo, "mobile");
			}
		} else if (RegexUtils.isEmail(qo.getAccount())) {
			if (StrUtil.isNotEmpty(qo.getCode())) {
				// 邮箱验证码登录
				return null;
			} else {
				return checkUserByPassword(qo, "email");
			}
		} else {
			return checkUserByPassword(qo, "username");
		}
	}
	
	private Long checkUserByPassword(LoginQO qo, String field) {
		UserAuthEntity entity = baseMapper.queryUserByField(qo.getAccount(), field);
		String password = SecureUtil.sha256(qo.getPassword() + entity.getSalt());
		if (password.equals(entity.getPassword())) {
			return entity.getUid();
		} else {
			throw new ProprietorException("账号密码错误");
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
	
	@Override
	public List<ThirdPlatformVo> getThirdPlatformInfo() {
		List<String> strings = factory.oauthList();
		List<ThirdPlatformVo> result = new ArrayList<>();
		for (String type : strings) {
			result.add(new ThirdPlatformVo(type.toLowerCase(), String.format("%s/login/%s/callback", callbackUrl, type.toLowerCase())));
		}
		return result;
	}
	
	@Override
	public String thirdPlatformLogin(String oauthType) {
		AuthRequest authRequest = factory.get(getAuthSource(oauthType));
		return authRequest.authorize(oauthType + "::" + AuthStateUtils.createState());
	}
	
	@Override
	public Object thirdPlatformLoginCallback(String oauthType, ThirdPlatformQo callback) {
		AuthRequest authRequest = factory.get(getAuthSource(oauthType));
		AuthCallback authCallback = new AuthCallback();
		authCallback.setCode(callback.getCode());
		authCallback.setAuth_code(callback.getAuthCode());
		authCallback.setState(callback.getState());
		authCallback.setAuthorization_code(callback.getAuthorizationCode());
		
		AuthResponse<?> response = authRequest.login(authCallback);
		log.info("【response】= {}", JSONUtil.toJsonStr(response));
		return response.getData();
	}

	/**
	 *  通过用户ID查询 t_user_auth 用户手机号
	 * @author YuLF
	 * @since  2020/12/2 13:55
	 * @Param  id  用户ID
	 * @return	   返回用户手机号码
	 */
	@Override
	public String selectContactById(Long id) {
		QueryWrapper<UserAuthEntity> eq = new QueryWrapper<UserAuthEntity>().select("mobile").eq("uid", id);
		UserAuthEntity userAuthEntity = userAuthMapper.selectOne(eq);
		//未注册直接访问
		if(userAuthEntity == null){
			return null;
		}
		return userAuthEntity.getMobile();
	}

	private AuthSource getAuthSource(String type) {
		if (StrUtil.isNotBlank(type)) {
			return AuthSource.valueOf(type.toUpperCase());
		} else {
			throw new ProprietorException("不支持的登录类型");
		}
	}
}
