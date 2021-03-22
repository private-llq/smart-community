package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.admin.AdminCaptchaEntity;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.util.MyCaptchaUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 登录相关
 */
@Slf4j
@RestController
public class AdminLoginController {
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserTokenService adminUserTokenService;
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminCaptchaService adminCaptchaService;
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICaptchaService captchaService;
	
	@Autowired
	private MyCaptchaUtil captchaUtil;
	
	@Autowired
	private UserUtils userUtils;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 防频繁调用验证码
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid) throws IOException {
		if (StrUtil.isBlank(uuid)) {
			throw new JSYException("uuid不能为空");
		}
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");
		//获取图片验证码
		Map<String, Object> captchaMap = captchaUtil.getCaptcha();
		//保存验证码
		AdminCaptchaEntity captchaEntity = new AdminCaptchaEntity();
		captchaEntity.setUuid(uuid);
		captchaEntity.setCode(String.valueOf(captchaMap.get("code")));
		adminCaptchaService.saveCaptcha(captchaEntity);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write((BufferedImage)captchaMap.get("image"), "jpg", out);
		IoUtil.close(out);
	}
	
	/**
	 * 发送手机验证码
	 */
	@ApiOperation("发送手机验证码")
	@GetMapping("/send/code")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query"),
		@ApiImplicitParam(name = "type", value = UserAuthEntity.CODE_TYPE_NOTE, required = true,
			allowableValues = "1,2,3,4,5", paramType = "query")
	})
	public CommonResult<Boolean> sendCode(@RequestParam String account,
	                                      @RequestParam Integer type) {
		boolean b;
		if (RegexUtils.isMobile(account)) {
			b = captchaService.sendMobile(account, type);
		} else {
			throw new PropertyException(JSYError.REQUEST_PARAM);
		}
		
		return b ? CommonResult.ok() : CommonResult.error("验证码发送失败");
	}
	
	/**
	 * 登录
	 */
	@PostMapping("/sys/login")
	public CommonResult<?> login(@RequestBody AdminLoginQO form) {
//		boolean captcha = adminCaptchaService.validate(form.getUsername(), form.getCaptcha());
//		if (!captcha) {
//			return CommonResult.error("验证码无效");
//		}
		ValidatorUtils.validateEntity(form);
		if (StrUtil.isEmpty(form.getCode()) && StrUtil.isEmpty(form.getPassword())) {
			throw new PropertyException("验证码和密码不能同时为空");
		}
		log.info(form.getAccount() + "开始登录");
		//用户信息
		AdminUserEntity user;
		if(RegexUtils.isMobile(form.getAccount())){
			user = adminUserService.queryByMobile(form.getAccount());
		}else if(RegexUtils.isEmail(form.getAccount())){
			user = adminUserService.queryByEmail(form.getAccount());
		}else{
			user = adminUserService.queryByUserName(form.getAccount());
		}
		
		//账号不存在、密码错误
		if (user == null) {
			return CommonResult.error("账号或密码不正确");
		}else if(!StringUtils.isEmpty(form.getCode())){
			checkVerifyCode(form.getAccount(),form.getCode());
		}else if(!user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())){
			return CommonResult.error("账号或密码不正确");
		}
		
		//账号锁定
		if (user.getStatus() == 1) {
			return CommonResult.error("账号已被锁定,请联系管理员");
		}
		
		//生成token，并保存到redis
		String token = adminUserTokenService.createToken(user);
		user.setToken(token);
		//查询用户菜单
		List<AdminMenuEntity> menuList = adminConfigService.queryUserMenu(user.getId());
		user.setMenuList(menuList);
		return CommonResult.ok(user);
	}
	
	private void checkVerifyCode(String mobile, String code) {
		Object oldCode = redisTemplate.opsForValue().get("vCodeAdmin:" + mobile);
		if (oldCode == null) {
			throw new PropertyException("验证码已失效");
		}
		
		if (!oldCode.equals(code)) {
			throw new PropertyException("验证码错误");
		}
		
		// 验证通过后删除验证码
//        redisTemplate.delete(account);
	}
	
	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	@Login
	public CommonResult<Boolean> logout() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader("token");
		if (StrUtil.isBlank(token)) {
			token = request.getParameter("token");
		}
		userUtils.destroyToken("Admin:Login",token);
		return CommonResult.ok();
	}
	
}
