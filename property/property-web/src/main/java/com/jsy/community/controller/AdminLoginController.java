package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.admin.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.util.MyCaptchaUtil;
import com.jsy.community.utils.RSAUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@ApiJSYController
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
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyCompanyService iPropertyCompanyService;
	
	@Autowired
	private MyCaptchaUtil captchaUtil;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	//	@Value("${propertyLoginExpireHour}")
//	private long loginExpireHour = 12;
	
//	/**
//	 * 防频繁调用验证码
//	 */
//	@GetMapping("captcha.jpg")
//	public void captcha(HttpServletResponse response, String uuid) throws IOException {
//		if (StrUtil.isBlank(uuid)) {
//			throw new JSYException("uuid不能为空");
//		}
//		response.setHeader("Cache-Control", "no-store, no-cache");
//		response.setContentType("image/jpeg");
//		//获取图片验证码
//		Map<String, Object> captchaMap = captchaUtil.getCaptcha();
//		//保存验证码
//		AdminCaptchaEntity captchaEntity = new AdminCaptchaEntity();
//		captchaEntity.setUuid(uuid);
//		captchaEntity.setCode(String.valueOf(captchaMap.get("code")));
//		adminCaptchaService.saveCaptcha(captchaEntity);
//
//		ServletOutputStream out = response.getOutputStream();
//		ImageIO.write((BufferedImage)captchaMap.get("image"), "jpg", out);
//		IoUtil.close(out);
//	}
	
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
	@LoginIgnore
	@Permit("community:property:send:code")
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
	* @Description: 登录获取小区列表  tips.7.20号改直接登录 不在选择小区列表
	 * @Param: [form]
	 * @Return: com.jsy.community.vo.CommonResult<?>
	 * @Author: chq459799974
	 * @Date: 2021/3/25
	**/
	@LoginIgnore
	@PostMapping("/sys/login")
	@Permit("community:property:sys:login")
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
		// 判断是不是验证码登陆,如果是,判断验证码正不正确
		if(!StringUtils.isEmpty(form.getCode())){
			checkVerifyCode(form.getAccount(),form.getCode());
		}
		log.info(form.getAccount() + "开始登录");
		//查询用户账号密码
		AdminUserAuthEntity user;
		user = adminUserService.queryLoginUserByMobile(form.getAccount());
		
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
		AdminUserEntity userData = adminUserService.queryUserByMobile(form.getAccount(), null);

		// 查询用户角色
		AdminUserRoleEntity adminUserRoleEntity = adminConfigService.queryRoleIdByUid(userData.getUid());

		//有权限的小区列表
		List<AdminCommunityEntity> adminCommunityList = adminConfigService.listAdminCommunity(userData.getUid());
		
		//用户菜单
		List<AdminMenuEntity> userMenu = new ArrayList<>();
		//返回VO
		AdminInfoVo adminInfoVo = new AdminInfoVo();

		//判断有无任一社区权限
		/*if(CollectionUtils.isEmpty(adminCommunityList)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"无社区管理权限，请联系管理员添加社区权限");
		}*/
		if (adminUserRoleEntity != null) {
			adminInfoVo.setRoleId(adminUserRoleEntity.getRoleId());
			userData.setRoleId(adminUserRoleEntity.getRoleId());
			/*//判断登录类型 (根据拥有权限的小区数量等于1是小区管理员账号 否则是物业公司账号)
			if(adminCommunityList.size() == 1){
				//小区管理员账号 直接登入小区菜单
				userData.setCommunityId(adminCommunityList.get(0).getCommunityId());
				userMenu = adminConfigService.queryMenuByUid(adminUserRoleEntity.getRoleId(), PropertyConsts.LOGIN_TYPE_COMMUNITY);
				//设置登录类型
				adminInfoVo.setLoginType(PropertyConsts.LOGIN_TYPE_COMMUNITY);
			}else{
				//物业公司账号 进入物业公司管理菜单
				userMenu = adminConfigService.queryMenuByUid(adminUserRoleEntity.getRoleId(),PropertyConsts.LOGIN_TYPE_PROPERTY);
				//设置登录类型
				adminInfoVo.setLoginType(PropertyConsts.LOGIN_TYPE_PROPERTY);
			}*/
			//物业公司账号 进入物业公司管理菜单
			userMenu = adminConfigService.queryMenuByUid(adminUserRoleEntity.getRoleId(),PropertyConsts.LOGIN_TYPE_PROPERTY);
			//设置登录类型
			adminInfoVo.setLoginType(PropertyConsts.LOGIN_TYPE_PROPERTY);
		}
		//设置菜单
		userData.setMenuList(userMenu);
		
		//设置物业公司名称
		adminInfoVo.setCompanyName(iPropertyCompanyService.getCompanyNameByCompanyId(userData.getCompanyId()));
		
		//清空该账号已之前的token(踢下线)
		String oldToken = redisTemplate.opsForValue().get("Admin:LoginAccount:" + form.getAccount());
		redisTemplate.delete("Admin:Login:" + oldToken);
		//创建token，保存redis
		List communityIdList = new ArrayList<>();
		for(AdminCommunityEntity entity : adminCommunityList){
			communityIdList.add(String.valueOf(entity.getCommunityId()));
		}
		userData.setCommunityIdList(communityIdList);
		String token = adminUserTokenService.createToken(userData);
		userData.setToken(token);
		
		//返回VO属性封装
		BeanUtils.copyProperties(userData,adminInfoVo);
		adminInfoVo.setUid(null);
		adminInfoVo.setStatus(null);
		return CommonResult.ok(adminInfoVo);
	}
	
	/**
	* @Description: 登入小区
	 * @Param: [account, communityId, communityKey]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/7/24
	**/
	@PostMapping("sys/enter")
	@Permit("community:property:sys:enter")
	public CommonResult enterCommunity(@RequestBody JSONObject jsonObject){
		Long communityId = jsonObject.getLong("communityId");
		List communityIds = UserUtils.getAdminCommunityIdList();
		UserUtils.validateCommunityId(communityId);
		//用户资料
		AdminUserEntity user = adminUserService.queryByUid(UserUtils.getUserId());
		//用户菜单
		List<AdminMenuEntity> userMenu = adminConfigService.queryMenuByUid(UserUtils.getAdminRoleId(), PropertyConsts.LOGIN_TYPE_COMMUNITY);
		//设置小区级菜单
		user.setMenuList(userMenu);
		user.setCompanyId(UserUtils.getAdminCompanyId());
		//设置小区ID
		user.setCommunityId(communityId);
		user.setCommunityIdList(communityIds);
		//创建token，保存redisIShopLeaseService
		String token = adminUserTokenService.createToken(user);
		user.setToken(token);
		AdminInfoVo adminInfoVo = new AdminInfoVo();
		BeanUtils.copyProperties(user,adminInfoVo);
		adminInfoVo.setUid(null);
		adminInfoVo.setStatus(null);
		return CommonResult.ok(adminInfoVo);
	}
	
	/**
	 * 检查手机验证码
	 */
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
	* @Description: 敏感操作短信验证(验证码和其他操作不同步,该接口返回的authToken即为中间桥梁)
	 * @Param: [account, code]
	 * @Return: com.jsy.community.vo.CommonResult<java.util.Map<java.lang.String,java.lang.Object>>
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@ApiOperation(value = "敏感操作短信验证", notes = "忘记密码等")
	@GetMapping("/check/code")
	@LoginIgnore
	@Permit("community:property:check:code")
	public CommonResult<Map<String,Object>> checkCode(@RequestParam String account, @RequestParam String code) {
		checkVerifyCode(account, code);
		String token = UserUtils.setRedisTokenWithTime("Admin:Auth", account,1, TimeUnit.HOURS);
		Map<String, Object> map = new HashMap<>();
		map.put("authToken",token);
		map.put("msg","验证通过，请在1小时内完成操作");
		return CommonResult.ok(map);
	}
	
	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	@Permit("community:property:sys:logout")
	public CommonResult<Boolean> logout() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader("token");
		if (StrUtil.isBlank(token)) {
			token = request.getParameter("token");
		}
		UserUtils.destroyToken("Admin:Login",token);
		return CommonResult.ok();
	}
	

	
}
