package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.admin.AdminLoginQO;
import com.jsy.community.service.*;
import com.jsy.community.utils.RSAUtil;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.sys.SysInfoVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PermitMenu;
import com.zhsj.base.api.domain.PermitRole;
import com.zhsj.base.api.rpc.IBaseAuthRpcService;
import com.zhsj.base.api.rpc.IBaseMenuRpcService;
import com.zhsj.base.api.rpc.IBaseRoleRpcService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 */
@Slf4j
@RestController
// @ApiJSYController
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
	
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseAuthRpcService baseAuthRpcService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseMenuRpcService baseMenuRpcService;
	@DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
	private IBaseRoleRpcService baseRoleRpcService;
	
	@Value("${propertyLoginExpireHour}")
	private long loginExpireHour = 12;
	
//	/**
//	 * 验证码
//	 */
//	@GetMapping("captcha.jpg")
//	public void captcha(HttpServletResponse response, String uuid) throws IOException {
//		response.setHeader("Cache-Control", "no-store, no-cache");
//		response.setContentType("image/jpeg");
//
//		//获取图片验证码
//		BufferedImage image = sysCaptchaService.getCaptcha(uuid);
//
//		ServletOutputStream out = response.getOutputStream();
//		ImageIO.write(image, "jpg", out);
//		IoUtil.close(out);
//	}
//
//	/**
//	 * 登录
//	 */
//	@PostMapping("/sys/login")
//	public CommonResult<?> login(@RequestBody SysLoginQO form) {
//		boolean captcha = sysCaptchaService.validate(form.getUsername(), form.getCaptcha());
//		if (!captcha) {
//			return CommonResult.error("验证码无效");
//		}
//
//		//用户信息
//		SysUserEntity user;
//		if(form.getUsername().contains("@")){
//			user = sysUserService.queryByEmail(form.getUsername());
//		}else{
//			user = sysUserService.queryByUserName(form.getUsername());
//		}
//
//		//账号不存在、密码错误
//		if (user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
//			return CommonResult.error("账号或密码不正确");
//		}
//
//		//账号锁定
//		if (user.getStatus() == 1) {
//			return CommonResult.error("账号已被锁定,请联系管理员");
//		}
//
//		//生成token，并保存到redis
//		String token = sysUserTokenService.createToken(user);
//		user.setToken(token);
//		//查询用户菜单
//		List<SysMenuEntity> menuList = sysConfigService.queryUserMenu(user.getId());
//		user.setMenuList(menuList);
//		return CommonResult.ok(user);
//	}
	
	/**
	 * 发送手机验证码
	 */
	@LoginIgnore
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
	@LoginIgnore
	@PostMapping("/sys/login")
	public CommonResult<?> login(@RequestBody AdminLoginQO form) {
		ValidatorUtils.validateEntity(form);
		if (StrUtil.isEmpty(form.getCode()) && StrUtil.isEmpty(form.getPassword())) {
			throw new AdminException("验证码和密码不能同时为空");
		}
		LoginVo loginVo;
		// 判断是不是验证码登陆,如果是,判断验证码正不正确
		if(!StringUtils.isEmpty(form.getCode())){
			checkVerifyCode(form.getAccount(),form.getCode());
			loginVo = baseAuthRpcService.login(BusinessConst.ULTIMATE_ADMIN, form.getAccount(), form.getCode(), "PHONE_CODE");
		} else {
			loginVo = baseAuthRpcService.login(BusinessConst.ULTIMATE_ADMIN, form.getAccount(), RSAUtil.privateDecrypt(form.getPassword(), RSAUtil.getPrivateKey(RSAUtil.COMMON_PRIVATE_KEY)), "PHONE_PWD");
		}
		log.info(form.getAccount() + "开始登录");
		// 获取用户菜单
		List<PermitMenu> userMenu = baseMenuRpcService.all(loginVo.getUserInfo().getId(), BusinessConst.ULTIMATE_ADMIN);
		
		//返回VO
		SysInfoVo sysInfoVo = new SysInfoVo();
		sysInfoVo.setId(String.valueOf(loginVo.getUserInfo().getId()));
		sysInfoVo.setMobile(loginVo.getUserInfo().getPhone());
		sysInfoVo.setRealName(loginVo.getUserInfo().getNickName());
		
		// 获取用户角色
		List<PermitRole> userRoles = baseRoleRpcService.listAllRolePermission(loginVo.getUserInfo().getId(), BusinessConst.ULTIMATE_ADMIN);
		if (userRoles != null && userRoles.size() > 0) {
			sysInfoVo.setRoleId(userRoles.get(0).getId());
		}
		
		//设置菜单
		if (userMenu != null && userMenu.size() > 0) {
			sysInfoVo.setMenuList(userMenu);
		}
		
		//清空该账号已之前的token(踢下线)
//		String oldToken = redisTemplate.opsForValue().get("Sys:LoginAccount:" + form.getAccount());
//		redisTemplate.delete("Sys:Login:" + oldToken);
		//获取token
		sysInfoVo.setToken(loginVo.getToken().getToken());
		
		redisTemplate.opsForValue().set("Sys:Login:" + loginVo.getToken().getToken(), JSON.toJSONString(sysInfoVo), loginExpireHour, TimeUnit.HOURS);//登录token
		redisTemplate.opsForValue().set("Sys:LoginAccount:" + loginVo.getUserInfo().getPhone(), loginVo.getToken().getToken(), loginExpireHour, TimeUnit.HOURS);//登录账户key的value设为token
		
		sysInfoVo.setId(null);
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
	@LoginIgnore
	@ApiOperation(value = "敏感操作短信验证", notes = "忘记密码等")
	@GetMapping("/check/code")
	public CommonResult<Map<String,Object>> checkCode(@RequestParam String account, @RequestParam String code) {
		checkVerifyCode(account, code);
		String token = UserUtils.setRedisTokenWithTime("Sys:Auth", account,1, TimeUnit.HOURS);
		Map<String, Object> map = new HashMap<>();
		map.put("authToken",token);
		map.put("msg","验证通过，请在1小时内完成操作");
		return CommonResult.ok(map);
	}
	
	/**
	 * 退出
	 */
	@PostMapping("/sys/logout")
	@Permit("community:admin:sys:logout")
	public CommonResult<Boolean> logout() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader("token");
		if (StrUtil.isBlank(token)) {
			token = request.getParameter("token");
		}
		UserUtils.destroyToken("Sys:Login",token);
		return CommonResult.ok();
	}
	
}
