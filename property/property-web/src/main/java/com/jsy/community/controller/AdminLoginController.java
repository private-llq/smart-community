package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminCaptchaService;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IAdminUserTokenService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminCaptchaEntity;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.util.MyCaptchaUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
	
	@Autowired
	private MyCaptchaUtil captchaUtil;
	
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 验证码
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
	 * 登录
	 */
	@PostMapping("/sys/login")
	public CommonResult<?> login(@RequestBody AdminLoginQO form) {
		boolean captcha = adminCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if (!captcha) {
			return CommonResult.error("验证码无效");
		}
		
		//用户信息
		AdminUserEntity user;
		if(form.getUsername().contains("@")){
			user = adminUserService.queryByEmail(form.getUsername());
		}else{
			user = adminUserService.queryByUserName(form.getUsername());
		}
		
		//账号不存在、密码错误
		if (user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
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
