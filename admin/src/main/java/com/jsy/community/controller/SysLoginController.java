package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import com.jsy.community.entity.SysUserEntity;
import com.jsy.community.qo.admin.SysLoginQO;
import com.jsy.community.service.ISysCaptchaService;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.service.ISysUserTokenService;
import com.jsy.community.utils.JwtUtils;
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
			return CommonResult.error("验证码不正确");
		}
		
		//用户信息
		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());
		
		//账号不存在、密码错误
		if (user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
			return CommonResult.error("账号或密码不正确");
		}
		
		//账号锁定
		if (user.getStatus() == 0) {
			return CommonResult.error("账号已被锁定,请联系管理员");
		}
		
		//生成token，并保存到数据库
		return sysUserTokenService.createToken(user.getId());
	}
	
	
	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	public CommonResult<Boolean> logout() {
		Long uid = JwtUtils.getUserId();
		if (uid != null) {
			sysUserTokenService.logout(uid);
		}
		return CommonResult.ok();
	}
	
}
