package com.jsy.community.controller;

import com.jsy.community.api.IAdminCaptchaService;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.IAdminUserTokenService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	
	/**
	 * 验证码
	 */
//	@GetMapping("captcha.jpg")
//	public void captcha(HttpServletResponse response, String uuid) throws IOException {
//		response.setHeader("Cache-Control", "no-store, no-cache");
//		response.setContentType("image/jpeg");
//
//		//获取图片验证码
//		BufferedImage image = adminCaptchaService.getCaptcha(uuid);
//
//		ServletOutputStream out = response.getOutputStream();
//		ImageIO.write(image, "jpg", out);
//		IoUtil.close(out);
//	}
	
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
	public CommonResult<Boolean> logout() {
		
		return CommonResult.ok();
	}
	
}
