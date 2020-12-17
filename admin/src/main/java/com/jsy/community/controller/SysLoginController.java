package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.qo.sys.SysLoginQO;
import com.jsy.community.service.ISysCaptchaService;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.service.ISysUserTokenService;
import com.jsy.community.vo.CommonResult;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 登录相关
 */
@RestController
public class SysLoginController {
	@Resource
	private ISysUserService sysUserService;
	@Resource
	private ISysUserTokenService sysUserTokenService;
	@Resource
	private ISysCaptchaService sysCaptchaService;
	@Resource
	private ISysConfigService sysConfigService;
	
	/**
	 * 验证码
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid) throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");
		
		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);
		
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IoUtil.close(out);
	}
	
	/**
	 * 登录
	 */
	@PostMapping("/sys/login")
	public CommonResult<?> login(@RequestBody SysLoginQO form) {
		boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if (!captcha) {
			return CommonResult.error("验证码无效");
		}
		
		//用户信息
		SysUserEntity user;
		if(form.getUsername().contains("@")){
			user = sysUserService.queryByEmail(form.getUsername());
		}else{
			user = sysUserService.queryByUserName(form.getUsername());
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
		String token = sysUserTokenService.createToken(user);
		user.setToken(token);
		//查询用户菜单
		List<SysMenuEntity> menuList = sysConfigService.queryUserMenu(user.getId());
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
