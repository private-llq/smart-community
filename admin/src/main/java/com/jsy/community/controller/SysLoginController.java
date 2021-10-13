package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.entity.sys.SysUserAuthEntity;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.qo.sys.SysLoginQO;
import com.jsy.community.service.*;
import com.jsy.community.utils.RSAUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.sys.SysInfoVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 */
@Slf4j
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
	@Resource
	private ICaptchaService captchaService;
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private UserUtils userUtils;
	
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
		boolean captcha = sysCaptchaService.validate(form.getUsername(), form.getCaptcha());
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
	 * 发送手机验证码
	 */
	@ApiOperation("发送手机验证码")
	@GetMapping("/send/code")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query"),
		@ApiImplicitParam(name = "type", value = UserAuthEntity.CODE_TYPE_NOTE, required = true,
			allowableValues = "1,2,3,4,5,6", paramType = "query")
	})
	public CommonResult<Boolean> sendCode(@RequestParam String account,
	                                      @RequestParam Integer type) {
		boolean b;
		if (RegexUtils.isMobile(account)) {
			b = captchaService.sendMobile(account, type);
		} else {
			throw new AdminException(JSYError.REQUEST_PARAM);
		}
		
		return b ? CommonResult.ok() : CommonResult.error("验证码发送失败");
	}
	
	/**
	 * @Description: 登录
	 * @Param: [form]
	 * @Return: com.jsy.community.vo.CommonResult<?>
	 * @Author: DKS
	 * @Date: 2021/10/12
	 **/
	@PostMapping("/sys/login")
	public CommonResult<?> login(@RequestBody AdminLoginQO form) {
		//图形验证码
//		boolean captcha = adminCaptchaService.validate(form.getUsername(), form.getCaptcha());
//		if (!captcha) {
//			return CommonResult.error("验证码无效");
//		}
		ValidatorUtils.validateEntity(form);
		if (StrUtil.isEmpty(form.getCode()) && StrUtil.isEmpty(form.getPassword())) {
			throw new AdminException("验证码和密码不能同时为空");
		}
		// 判断是不是验证码登陆,如果是,判断验证码正不正确
		if(!StringUtils.isEmpty(form.getCode())){
			checkVerifyCode(form.getAccount(),form.getCode());
		}
		log.info(form.getAccount() + "开始登录");
		//查询用户账号密码
		SysUserAuthEntity user;
		user = sysUserService.queryLoginUserByMobile(form.getAccount());
		
		//账号不存在、密码错误
		if (user == null) {
			log.error(form.getAccount() + "登录失败，原因：账号不存在");
			return CommonResult.error("账号或密码不正确");
		}
		// 如果是密码登录,判断密码正不正确
		if (StringUtils.isEmpty(form.getCode())) {
			if(!user.getPassword().equals(new Sha256Hash(RSAUtil.privateDecrypt(form.getPassword(),RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), user.getSalt()).toHex())){
				log.error(form.getAccount() + "登录失败，原因：密码不正确");
				return CommonResult.error("账号或密码不正确");
			}
		}
		//用户资料
		SysUserEntity userData = sysUserService.queryUserByMobile(form.getAccount(), null);
		
		// 查询用户角色
		SysUserRoleEntity sysUserRoleEntity = sysConfigService.queryRoleIdByUid(String.valueOf(userData.getId()));
		
		//用户菜单
		List<SysMenuEntity> userMenu = new ArrayList<>();
		//返回VO
		SysInfoVo sysInfoVo = new SysInfoVo();
		
		if (sysUserRoleEntity != null) {
			sysInfoVo.setRoleId(sysUserRoleEntity.getRoleId());
			userData.setRoleId(sysUserRoleEntity.getRoleId());
			userMenu = sysConfigService.queryMenuByUid(sysUserRoleEntity.getRoleId());
		}
		//设置菜单
		userData.setMenuList(userMenu);
		
		//清空该账号已之前的token(踢下线)
		String oldToken = redisTemplate.opsForValue().get("Sys:LoginAccount:" + form.getAccount());
		redisTemplate.delete("Sys:Login:" + oldToken);
		//创建token，保存redis
		String token = sysUserTokenService.createToken(userData);
		userData.setToken(token);
		
		//返回VO属性封装
		BeanUtils.copyProperties(userData,sysInfoVo);
		sysInfoVo.setUid(null);
		sysInfoVo.setStatus(null);
		return CommonResult.ok(sysInfoVo);
	}
	
	/**
	 * 检查手机验证码
	 */
	private void checkVerifyCode(String mobile, String code) {
		Object oldCode = redisTemplate.opsForValue().get("vCodeSys:" + mobile);
		if (oldCode == null) {
			throw new AdminException("验证码已失效");
		}
		
		if (!oldCode.equals(code)) {
			throw new AdminException("验证码错误");
		}
		
		// 验证通过后删除验证码
//        redisTemplate.delete(account);
	}
	
	/**
	 * @Description: 敏感操作短信验证(验证码和其他操作不同步,该接口返回的authToken即为中间桥梁)
	 * @Param: [account, code]
	 * @Return: com.jsy.community.vo.CommonResult<java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	 **/
	@ApiOperation(value = "敏感操作短信验证", notes = "忘记密码等")
	@GetMapping("/check/code")
	public CommonResult<Map<String,Object>> checkCode(@RequestParam String account, @RequestParam String code) {
		checkVerifyCode(account, code);
		String token = userUtils.setRedisTokenWithTime("Sys:Auth", account,1, TimeUnit.HOURS);
		Map<String, Object> map = new HashMap<>();
		map.put("authToken",token);
		map.put("msg","验证通过，请在1小时内完成操作");
		return CommonResult.ok(map);
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
		userUtils.destroyToken("Sys:Login",token);
		return CommonResult.ok();
	}
	
}
