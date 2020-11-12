package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.api.ICaptchaService;
import com.jsy.community.api.IUserService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.proprietor.LoginQO;
import com.jsy.community.utils.JwtUtils;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserInfoVo;
import com.jsy.community.vo.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户认证
 *
 * @author ling
 * @date 2020-11-12 10:30
 */
@RequestMapping("user/auth")
@Api(tags = "用户认证控制器")
@RestController
public class UserAuthController {
	@Resource
	private JwtUtils jwtUtils;
	
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICaptchaService captchaService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserService userService;
	
	/**
	 * 发送验证码
	 */
	@ApiOperation("发送验证码，手机或邮箱，参数不可同时为空")
	@GetMapping("/send/code")
	@ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query")
	public CommonResult<Boolean> sendCode(@RequestParam String account) {
		boolean result;
		if (RegexUtils.isMobile(account)) {
			result = captchaService.sendMobile(account);
		} else if (RegexUtils.isEmail(account)) {
			result = captchaService.sendEmail(account);
		} else {
			throw new ProprietorException(JSYError.REQUEST_PARAM);
		}
		
		return result ? CommonResult.ok() : CommonResult.error("验证码发送失败");
	}
	
	@ApiOperation("登录")
	@PostMapping("/login")
	public CommonResult<UserLoginVo> login(@RequestBody LoginQO qo) {
		ValidatorUtils.validateEntity(qo);
		if (StrUtil.isEmpty(qo.getCode()) && StrUtil.isEmpty(qo.getPassword())) {
			throw new ProprietorException("验证码和密码不能同时为空");
		}
		
		UserInfoVo infoVo = userService.login(qo);
		UserLoginVo loginVo = jwtUtils.generateToken(infoVo);
		return CommonResult.ok(loginVo);
	}
}
