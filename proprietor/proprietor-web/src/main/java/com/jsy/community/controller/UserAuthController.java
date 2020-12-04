package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.ThirdPlatformQo;
import com.jsy.community.qo.proprietor.AddPasswordQO;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.qo.proprietor.RegisterQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ThirdPlatformVo;
import com.jsy.community.vo.UserAuthVo;
import com.jsy.community.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户认证
 *
 * @author ling
 * @since 2020-11-12 10:30
 */
@RequestMapping("user/auth")
@Api(tags = "用户认证控制器")
@RestController
@ApiJSYController
public class UserAuthController {
	
	@Autowired
	private UserUtils userUtils;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	@SuppressWarnings("unused")
	private ICaptchaService captchaService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	@SuppressWarnings("unused")
	private IUserService userService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	@SuppressWarnings("unused")
	private IUserAuthService userAuthService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	@SuppressWarnings("unused")
	private ICommonService commonService;
	
	/**
	 * 发送验证码
	 */
	@ApiOperation("发送验证码，手机或邮箱，参数不可同时为空")
	@GetMapping("/send/code")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query"),
		@ApiImplicitParam(name = "type", value = UserAuthEntity.CODE_TYPE_NOTE, required = true,
			allowableValues = "1,2,3", paramType = "query")
	})
	public CommonResult<Boolean> sendCode(@RequestParam String account,
	                                      @RequestParam Integer type) {
		boolean b;
		if (RegexUtils.isMobile(account)) {
			b = captchaService.sendMobile(account, type);
		} else if (RegexUtils.isEmail(account)) {
			b = captchaService.sendEmail(account, type);
		} else {
			throw new ProprietorException(JSYError.REQUEST_PARAM);
		}
		
		return b ? CommonResult.ok() : CommonResult.error("验证码发送失败");
	}
	
	@ApiOperation("登录")
	@PostMapping("/login")
	public CommonResult<UserAuthVo> login(@RequestBody LoginQO qo) {
		ValidatorUtils.validateEntity(qo);
		if (StrUtil.isEmpty(qo.getCode()) && StrUtil.isEmpty(qo.getPassword())) {
			throw new ProprietorException("验证码和密码不能同时为空");
		}
		
		UserInfoVo infoVo = userService.login(qo);
		
		//生成带token和用户信息的的UserAuthVo
		UserAuthVo userAuthVo = userService.createAuthVoWithToken(infoVo);
		return CommonResult.ok(userAuthVo);
	}
	
	@ApiOperation("注册")
	@PostMapping("/register")
	public CommonResult<UserAuthVo> register(@RequestBody RegisterQO qo) {
		ValidatorUtils.validateEntity(qo);
		
		String uid = userService.register(qo);
		UserInfoVo userInfoVo = new UserInfoVo();
		userInfoVo.setUid(uid);
		
		//生成带token和用户信息的的UserAuthVo(注册后设置密码用)
		UserAuthVo userAuthVo = userService.createAuthVoWithToken(userInfoVo);
		return CommonResult.ok(userAuthVo);
	}
	
	@ApiOperation(value = "注册后设置密码", notes = "需要登录")
	@PostMapping("/password")
	@Login
	public CommonResult<Boolean> addPassword(@RequestBody AddPasswordQO qo) {
		String uid = UserUtils.getUserId();
		
		boolean b = userAuthService.addPassword(uid, qo);
		return b ? CommonResult.ok() : CommonResult.error("密码设置失败");
	}
	
	@ApiOperation(value = "敏感操作短信验证", notes = "忘记密码等")
	@GetMapping("/check/code")
	public CommonResult<Map<String,Object>> checkCode(@RequestParam String account, @RequestParam String code) {
		commonService.checkVerifyCode(account, code);
		String token = userUtils.setRedisTokenWithTime("Auth", account,1, TimeUnit.HOURS);
		Map<String, Object> map = new HashMap<>();
		map.put("token",token);
		map.put("msg","验证通过，请在1小时内完成操作");
		return CommonResult.ok(map);
	}
	
	@ApiOperation("重置密码")
	@PostMapping("/reset/password")
	@Auth
	public CommonResult<Boolean> resetPassword(@RequestAttribute(value = "body") String body) {
		ResetPasswordQO qo = JSONObject.parseObject(body, ResetPasswordQO.class);
		ValidatorUtils.validateEntity(qo);
		boolean b = userAuthService.resetPassword(qo);
		if(b){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String token = request.getHeader("token");
			if (StrUtil.isBlank(token)) {
				token = request.getParameter("token");
			}
			//销毁Auth token
			userUtils.destroyToken("Auth",token);
		}
		return b ? CommonResult.ok() : CommonResult.error("重置失败");
	}
	
	////////////////////////////////////////////////三方登录//////////////////////////////////////////////////////////////
	
	/**
	 * 登录类型
	 */
	@GetMapping("/login/type")
	@ApiOperation("获取三方登录列表")
	public CommonResult<List<ThirdPlatformVo>> getLoginType() {
		List<ThirdPlatformVo> list = userAuthService.getThirdPlatformInfo();
		return CommonResult.ok(list);
	}
	
	
	/**
	 * 登录类型
	 */
	@GetMapping("/login/{oauthType}")
	@ApiOperation("三方登录")
	public CommonResult<String> thirdPlatformLogin(@PathVariable String oauthType) {
		String url = userAuthService.thirdPlatformLogin(oauthType);
		return CommonResult.ok(url);
	}
	
	@GetMapping("/login/{oauthType}/callback")
	@ApiOperation("三方登录回调")
	public CommonResult<Object> thirdPlatformLoginCallback(@PathVariable String oauthType, @RequestBody ThirdPlatformQo callback) {
		Object obj = userAuthService.thirdPlatformLoginCallback(oauthType, callback);
		return CommonResult.ok(obj);
	}
}
