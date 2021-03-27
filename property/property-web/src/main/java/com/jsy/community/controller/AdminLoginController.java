package com.jsy.community.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.admin.AdminCaptchaEntity;
import com.jsy.community.entity.admin.AdminMenuEntity;
import com.jsy.community.entity.admin.AdminUserAuthEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.util.MyCaptchaUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

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
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
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
	* @Description: 登录获取小区列表
	 * @Param: [form]
	 * @Return: com.jsy.community.vo.CommonResult<?>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
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
			throw new PropertyException("验证码和密码不能同时为空");
		}
		log.info(form.getAccount() + "开始登录");
		//用户信息
		AdminUserAuthEntity user;
		if(!RegexUtils.isMobile(form.getAccount())){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"非法手机号");
		}
		user = adminUserService.queryLoginUserByMobile(form.getAccount());
		
		//账号不存在、密码错误
		if (user == null) {
			return CommonResult.error("账号或密码不正确");
		}else if(!StringUtils.isEmpty(form.getCode())){
			checkVerifyCode(form.getAccount(),form.getCode());
		}else if(!user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())){
			return CommonResult.error("账号或密码不正确");
		}
		
		////查询已加入小区id列表
		List<Long> idList = adminUserService.queryCommunityIdList(form.getAccount());
		//查询已加入小区列表详情
		List<CommunityEntity> communityList = communityService.queryCommunityBatch(idList);
		//生成验证key，保存redis，验证完毕后即销毁
		String communityKey = UUID.randomUUID().toString().replace("-", "");
		redisTemplate.opsForValue().set("Admin:CommunityKey:" + communityKey, form.getAccount(),12,TimeUnit.HOURS);
		//返回小区列表和下一个接口验证用的一次性key
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("communityList",communityList);
		returnMap.put("communityKey",communityKey);
		return CommonResult.ok(returnMap);
	}
	
	/**
	* @Description: 登入小区
	 * @Param: [account, communityId, communityKey]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@PostMapping("sys/enter")
	public CommonResult enterCommunity(@RequestBody JSONObject jsonObject){
		String communityKey = jsonObject.getString("communityKey");
		String account = jsonObject.getString("account");
		Long communityId = jsonObject.getLong("communityId");
		//验证
		String catchedAccount = redisTemplate.opsForValue().get("Admin:CommunityKey:" + communityKey);
		if(StringUtils.isEmpty(catchedAccount)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"登录过期，请重新登录");
		}
		if(!account.equals(catchedAccount)){
			log.error("账户试图非法访问：" + account);
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"非法访问，已拦截");
		}
		List<Long> idList = adminUserService.queryCommunityIdList(account);
		if(!idList.contains(communityId)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"没有该社区权限");
		}
		//查询该社区下用户资料、用户菜单，并返回token
		//用户资料
		AdminUserEntity user = adminUserService.queryUserByMobile(account, communityId);
		if(user.getStatus() == 1){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"账户已被禁用");
		}
		//用户菜单
		List<AdminMenuEntity> userMenu = adminConfigService.queryMenuByUid(user.getUid());
		user.setMenuList(userMenu);
		//创建token，保存redis
		String token = adminUserTokenService.createToken(user);
		user.setToken(token);
		AdminInfoVo adminInfoVo = new AdminInfoVo();
		BeanUtils.copyProperties(user,adminInfoVo);
		adminInfoVo.setUid(null);
		adminInfoVo.setStatus(null);
		return CommonResult.ok(adminInfoVo);
	}
	
	//检查手机验证码
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
