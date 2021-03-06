package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.*;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.admin.AdminCommunityEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.util.MyCaptchaUtil;
import com.jsy.community.utils.RSAUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitMenu;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.*;
import com.zhsj.base.api.vo.LoginVo;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 */
@Slf4j
@RestController
// @ApiJSYController
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
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseAuthRpcService baseAuthRpcService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuRpcService baseMenuRpcService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseSmsRpcService baseSmsRpcService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseUserInfoRpcService userInfoRpcService;

	
	@Resource
	private MyCaptchaUtil captchaUtil;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Value("${propertyLoginExpireHour}")
	private long loginExpireHour = 12;
	
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
	public CommonResult<Boolean> sendCode(@RequestParam String account, @RequestParam Integer type) {
		if (RegexUtils.isMobile(account)) {
			baseSmsRpcService.sendVerificationCode(account);
		} else {
			throw new PropertyException(JSYError.REQUEST_PARAM);
		}
		return CommonResult.ok();
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
		LoginVo loginVo;
		// 判断是不是验证码登陆,如果是,判断验证码正不正确并获取用户信息
		if(!StringUtils.isEmpty(form.getCode())){
//			checkVerifyCode(form.getAccount(),form.getCode());
			loginVo = baseAuthRpcService.propertyLogin(form.getAccount(), form.getCode(), "PHONE_CODE");
		} else {
			loginVo = baseAuthRpcService.propertyLogin(form.getAccount(), RSAUtil.privateDecrypt(form.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), "PHONE_PWD");
		}
		log.info(form.getAccount() + "开始登录");
		// 判断物业公司有效期
		Long companyIdByUid = iPropertyCompanyService.getPropertyCompanyIdByUid(loginVo.getUserInfo().getAccount());
		PropertyCompanyEntity companyEntity = iPropertyCompanyService.selectCompany(companyIdByUid);
		if (companyEntity.getOverTime() != null) {
			if (LocalDate.now().isAfter(companyEntity.getOverTime())) {
				throw new PropertyException(JSYError.COMPANY_TIME_EXPIRE.getCode(), "该物业已到期，请联系管理员！");
			}
		}
		
		// 获取用户菜单
		List<PermitMenu> userMenu = baseMenuRpcService.all(loginVo.getUserInfo().getId(), BusinessConst.PROPERTY_ADMIN);
		// list排序
		userMenu.sort(Comparator.comparing(PermitMenu::getSort));
		
		//用户资料
//		AdminUserEntity userData = adminUserService.queryByUid(String.valueOf(loginVo.getUserInfo().getUserId()));
		
		// 查询用户角色
//		AdminUserRoleEntity adminUserRoleEntity = adminConfigService.queryRoleIdByUid(userData.getUid());

		//有权限的小区列表
		List<AdminCommunityEntity> adminCommunityList = adminConfigService.listAdminCommunity(String.valueOf(loginVo.getUserInfo().getAccount()));
		
		//用户菜单
//		List<AdminMenuEntity> userMenu = new ArrayList<>();
		//返回VO
		AdminInfoVo adminInfoVo = new AdminInfoVo();
		adminInfoVo.setUid(String.valueOf(loginVo.getUserInfo().getAccount()));
		adminInfoVo.setMobile(loginVo.getUserInfo().getPhone());
		adminInfoVo.setRealName(loginVo.getUserInfo().getNickName());
		
		//判断有无任一社区权限
		/*if(CollectionUtils.isEmpty(adminCommunityList)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"无社区管理权限，请联系管理员添加社区权限");
		}*/
		// 获取用户角色
		List<PermitRole> userRoles = baseRoleRpcService.listAllRolePermission(loginVo.getUserInfo().getId(), BusinessConst.PROPERTY_ADMIN);
		if (CollectionUtils.isEmpty(userRoles)) {
			throw new JSYException("请联系管理员赋予角色权限");
		}
		adminInfoVo.setRoleId(userRoles.get(0).getId());
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
//		userMenu = adminConfigService.queryMenuByUid(adminUserRoleEntity.getRoleId(),PropertyConsts.LOGIN_TYPE_PROPERTY);
		//设置登录类型
		adminInfoVo.setLoginType(PropertyConsts.LOGIN_TYPE_PROPERTY);
		//设置菜单
		adminInfoVo.setMenuList(userMenu);
		
		//设置物业公司名称
		Long companyId = iPropertyCompanyService.getPropertyCompanyIdByUid(String.valueOf(loginVo.getUserInfo().getAccount()));
		adminInfoVo.setCompanyId(companyId);
		adminInfoVo.setCompanyName(iPropertyCompanyService.getCompanyNameByCompanyId(companyId));
		
		//清空该账号已之前的token(踢下线)
//		String oldToken = redisTemplate.opsForValue().get("Admin:LoginAccount:" + form.getAccount());
//		redisTemplate.delete("Admin:Login:" + oldToken);
		// 获取token
		List<String> communityIdList = new ArrayList<>();
		for(AdminCommunityEntity entity : adminCommunityList){
			communityIdList.add(entity.getCommunityId());
		}
		adminInfoVo.setCommunityIdList(communityIdList);
//		String token = adminUserTokenService.createToken(userData);
		adminInfoVo.setToken(loginVo.getToken().getToken());
		
		redisTemplate.opsForValue().set("Admin:Login:" + loginVo.getToken().getToken(), JSON.toJSONString(adminInfoVo), loginExpireHour, TimeUnit.HOURS);//登录token
		redisTemplate.opsForValue().set("Admin:LoginAccount:" + loginVo.getUserInfo().getPhone(), loginVo.getToken().getToken(), loginExpireHour, TimeUnit.HOURS);//登录账户key的value设为token
		
		//返回VO属性封装
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
//		UserInfoVo userInfo = UserUtils.getUserInfo();
//		Long adminId = userInfo.getUserId();
//		List<String> communityIds = UserUtils.getAdminCommunityIdList();
		UserUtils.validateCommunityId(communityId);
		//用户资料
//		AdminUserEntity user = adminUserService.queryByUid(UserUtils.getUserId());
		AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
		UserDetail userDetail = userInfoRpcService.getUserDetail(adminInfoVo.getUid());
		//用户菜单
//		List<AdminMenuEntity> userMenu = adminConfigService.queryMenuByUid(UserUtils.getAdminRoleId(), PropertyConsts.LOGIN_TYPE_COMMUNITY);
		List<PermitMenu> userMenu = baseMenuRpcService.all(userDetail.getId(), BusinessConst.COMMUNITY_ADMIN);
		// list排序
		userMenu.sort(Comparator.comparing(PermitMenu::getSort));
		//设置小区级菜单
		adminInfoVo.setMenuList(userMenu);
		//设置小区ID
		adminInfoVo.setCommunityId(communityId);
		CommunityEntity communityNameById = communityService.getCommunityNameById(communityId);
		if (communityNameById != null) {
			adminInfoVo.setCommunityName(communityNameById.getName());
		}
//		adminInfoVo.setCommunityIdList(communityIds);
		
		//创建token，保存redisIShopLeaseService
//		String token = adminUserTokenService.createToken(user);
//		user.setToken(token);
//		BeanUtils.copyProperties(user,adminInfoVo);
		redisTemplate.opsForValue().set("Admin:Login:" + adminInfoVo.getToken(), JSON.toJSONString(adminInfoVo), loginExpireHour, TimeUnit.HOURS);//登录token
		redisTemplate.opsForValue().set("Admin:LoginAccount:" + adminInfoVo.getMobile(), adminInfoVo.getToken(), loginExpireHour, TimeUnit.HOURS);//登录账户key的value设为token
		
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
