package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.consts.PropertyConsts;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author chq459799974
 * @description 系统用户
 * @since 2020-11-27 16:02
 **/
@RequestMapping("sys/user")
@Api(tags = "系统用户控制器")
@ApiJSYController
@Slf4j
//@Login
@RestController
public class AdminUserController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private SmsUtil smsUtil;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;

//	/**
//	* @Description: 设置用户角色
//	 * @Param: [sysUserRoleEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/14
//	**/
//	@Transactional(rollbackFor = Exception.class)
//	@PostMapping("roles")
//	public CommonResult setUserRoles(@RequestBody AdminUserRoleEntity adminUserRoleEntity){
//		boolean b = adminUserService.setUserRoles(adminUserRoleEntity.getRoleIds(), adminUserRoleEntity.getUserId());
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置用户角色失败");
//	}

//	/**
//	 * @Description: 禁用账户
//	 * @Param: [uid]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/1
//	 **/
//	@PutMapping("disable")
//	public CommonResult disableUser(@RequestParam Long uid){
//		AdminUserEntity adminUserEntity = new AdminUserEntity();
//		adminUserEntity.setId(uid);
//		adminUserEntity.setStatus(1);
//		boolean b = adminUserService.updateUser(adminUserEntity);
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"禁用失败");
//	}
	
//	/**
//	* @Description: 邮件注册邀请
//	 * @Param: [sysUserEntity]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/11/30
//	**/
//	@Login
//	@PostMapping("invitation/email")
//	public CommonResult invitationOfEmail(@RequestBody AdminUserEntity sysUserEntity) {
//		ValidatorUtils.validateEntity(sysUserEntity,AdminUserEntity.inviteUserValidatedGroup.class);
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//		String token = request.getHeader("token");
//		if (StrUtil.isBlank(token)) {
//			token = request.getParameter("token");
//		}
//		AdminInfoVo adminInfo = userUtils.getAdminInfo(token);
//		sysUserEntity.setCreateUserName(adminInfo.getRealName());//邀请者姓名
//		sysUserEntity.setCreateUserId(UserUtils.getUserId());//邀请者uid
//		Map<String, String> resultMap = adminUserService.invitation(sysUserEntity);
//		return Boolean.parseBoolean(resultMap.get("result")) ? CommonResult.ok() : CommonResult.error(JSYError.REQUEST_PARAM.getCode(),resultMap.get("reason"));
//	}
	
//	/**
//	* @Description: 邮件注册激活确认
//	 * @Param: [sysUserEntity]
//	 * @Return: org.springframework.web.servlet.ModelAndView
//	 * @Author: chq459799974
//	 * @Date: 2020/11/30
//	**/
//	@GetMapping("activation/email")
//	public ModelAndView activationOfEmail(AdminUserEntity adminUserEntity){
//		ModelAndView mv = new ModelAndView();
//		// 链接参数有误
//		if(StringUtils.isEmpty(adminUserEntity.getEmail()) || adminUserEntity.getCreateUserId() == null){
//			mv.setViewName("mail/error.html");
//			return mv;
//		}
//		Map<String, String> resultMap = adminUserService.activation(adminUserEntity);
//		mv.addObject("reason",resultMap.get("reason"));
//		mv.addObject("password",resultMap.get("password"));
//		mv.setViewName(resultMap.get("templateName"));
//		return mv;
//	}
	
//	/**
//	* @Description: 用户名ajax查重
//	 * @Param: [username]
//	 * @Return: com.jsy.community.vo.CommonResult
//	 * @Author: chq459799974
//	 * @Date: 2020/12/25
//	**/
//	@GetMapping("exists")
//	public CommonResult checkUsernameExists(@RequestParam("username") String username){
//		boolean b = adminUserService.checkUsernameExists(username);
//		return b ? CommonResult.error(JSYError.DUPLICATE_KEY.getCode(),"用户名被占用") : CommonResult.ok("用户名可以使用");
//	}
	
	//邮箱注册后添加用户名
//	@PutMapping("username")
//	public CommonResult setUserName(@RequestParam String userName){
//		//TODO TOKEN获取uid
//		Long uid = 1L;
//		Map<String, String> resultMap = adminUserService.setUserName(uid, userName);
//		return Boolean.parseBoolean(String.valueOf(resultMap.get("result"))) ? CommonResult.ok() : CommonResult.error(Integer.parseInt(resultMap.get("code")),resultMap.get("reason"));
//	}
	
	//添加手机号(短信验证)
//	@PutMapping("mobile")
//	public CommonResult changeMobile(){
//		Long uid = 1L;
//		return CommonResult.ok();
//	}
	
	//手机号邀请用户
//	@GetMapping("invitation/mobile")
//	@Login
//	public CommonResult invitationOfMobile(@RequestParam String mobile){
//		AdminUserEntity adminUserEntity = adminUserService.queryByMobile(mobile);
//		if(adminUserEntity != null){
//			return CommonResult.error("用户已注册，请不要重复邀请");
//		}
//		//发短信
////		smsUtil.sendSms(mobile,"");
//		//redis存验证码
//		String code = MyMathUtils.randomCode(6);
//		code = "1111";
//		stringRedisTemplate.opsForValue().set("Admin:Invit:" + mobile,code);
//		return CommonResult.ok();
//	}
	
	//手机号用户注册
//	@GetMapping("activation/mobile")
//	public CommonResult activationOfMobile(@RequestParam String mobile, @RequestParam String code){
//		//redis验证短信
//		String savedCode = stringRedisTemplate.opsForValue().get("Admin:Invit:" + mobile);
//		//保存用户
//		AdminUserEntity user = new AdminUserEntity();
//		if(code.equals(savedCode)){
//			stringRedisTemplate.delete("Admin:Invit:" + mobile);
//			user.setMobile(mobile);
//			//生成随机初始密码
//			String password = UUID.randomUUID().toString().substring(0, 6);
//			user.setPassword(password);
//			adminUserService.saveUser(user);
//			return CommonResult.ok(user,"验证成功，用户已注册");
//		}
//		return CommonResult.error("验证失败");
//	}
	
	//==================================== 物业端（新）begin ====================================
	//============== 操作员管理相关start ===============
	/**
	* @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Login
	@PostMapping("query")
	public CommonResult queryOperator(@RequestBody BaseQO<AdminUserQO> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new AdminUserQO());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(adminUserService.queryOperator(baseQO));
	}
	
	/**
	* @Description: 添加操作员
	 * @Param: [adminUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Login
	@PostMapping("")
	public CommonResult addOperator(@RequestBody AdminUserEntity adminUserEntity){
		ValidatorUtils.validateEntity(adminUserEntity,AdminUserEntity.addOperatorValidatedGroup.class);
		if(CollectionUtils.isEmpty(adminUserEntity.getMenuIdList())){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少功能授权");
		}
		AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
		adminUserEntity.setCommunityId(loginUser.getCommunityId());
		adminUserEntity.setCreateBy(loginUser.getUid());
		return adminUserService.addOperator(adminUserEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 编辑操作员
	 * @Param: [adminUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@Login
	@PutMapping("")
	public CommonResult updateOperator(@RequestBody AdminUserEntity adminUserEntity){
		ValidatorUtils.validateEntity(adminUserEntity,AdminUserEntity.updateOperatorValidatedGroup.class);
		AdminInfoVo loginUser = UserUtils.getAdminUserInfo();
		adminUserEntity.setCommunityId(loginUser.getCommunityId());
		adminUserEntity.setUpdateBy(loginUser.getUid());
		boolean b = adminUserService.updateOperator(adminUserEntity);
		return b ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/** 
	* @Description: 重置密码(随机)
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/18
	**/
	@Login
	@PutMapping("pass/reset")
	public CommonResult resetPass(@RequestBody Map<String,Long> map){
		if(map.get("id") == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少ID参数");
		}
		String uid = UserUtils.getUserId();
		return adminUserService.resetPassword(map.get("id"),uid) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	//============== 操作员管理相关end ===============
	
	//============== 个人中心相关start ===============
	/**
	* @Description: 用户头像更新
	 * @Param: [file]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Login
	@PutMapping("avatar")
	public CommonResult uploadAvatar(MultipartFile file){
		if(!PicUtil.checkSizeAndType(file,5*1024)){
			throw new JSYException(JSYError.BAD_REQUEST.getCode(),"图片格式错误");
		}
		String url = MinioUtils.upload(file, PropertyConsts.BUCKET_NAME_AVATAR);
		boolean result = adminUserService.updateAvatar(url,UserUtils.getUserId());
		return result ? CommonResult.ok(url,"操作成功") : CommonResult.error(JSYError.INTERNAL.getCode(),"操作失败");
	}
	
	/**
	* @Description: 个人资料查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/16
	**/
	@Login
	@GetMapping("info")
	public CommonResult queryPersonalData(){
		return CommonResult.ok(adminUserService.queryPersonalData(UserUtils.getUserId()),"查询成功");
	}
	
	/**
	* @Description: 修改/忘记密码
	 * @Param: [body]
	 * @Return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 * @Author: chq459799974
	 * @Date: 2021/4/19
	**/
	@ApiOperation("修改/忘记密码")
	@PutMapping("password")
	@Auth
	@Login(allowAnonymous = true)
	public CommonResult<Boolean> updatePassword(@RequestAttribute(value = "body") String body) {
		ResetPasswordQO qo = JSONObject.parseObject(body, ResetPasswordQO.class);
		String uid = UserUtils.getUserId();
		if(uid == null){  //忘记密码
			ValidatorUtils.validateEntity(qo,ResetPasswordQO.forgetPassVGroup.class);
		}else{  //在线修改密码
			ValidatorUtils.validateEntity(qo,ResetPasswordQO.updatePassVGroup.class);
		}
		if (!qo.getPassword().equals(qo.getConfirmPassword())) {
			throw new JSYException("两次密码不一致");
		}
		boolean b = adminUserService.updatePassword(qo,UserUtils.getUserId());
		if(b){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String authToken = request.getHeader("authToken");
			if (StrUtil.isBlank(authToken)) {
				authToken = request.getParameter("authToken");
			}
			//销毁Auth token
			userUtils.destroyToken("Auth",authToken);
		}
		return b ? CommonResult.ok() : CommonResult.error("操作失败");
	}
	
	@ApiOperation("更换手机号")
	@PutMapping("mobile")
	@Login
	public CommonResult changeMobile(@RequestBody Map<String,String> map){
		//入参验证
		String newMobile = map.get("newMobile");
		if(StringUtils.isEmpty(newMobile)){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请填写新手机号");
		}
		if (!RegexUtils.isMobile(newMobile)) {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请检查手机号格式是否正确");
		}
		String oldCode = map.get("oldCode");
		String newCode = map.get("newCode");
		if (StrUtil.isEmpty(oldCode)) {
			throw new JSYException("旧手机号验证码不能为空");
		}
		if (StrUtil.isEmpty(newCode)) {
			throw new JSYException("新手机号验证码不能为空");
		}
		//从请求获取uid
		String uid = UserUtils.getUserId();
		//获取当前用户手机号
		String oldMobile = adminUserService.queryMobileByUid(uid);
		if(newMobile.equals(oldCode)){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"新手机号与原手机号相同");
		}
		//权限验证
		Map<String, String> returnMap1 = checkVerifyCode(oldMobile, oldCode);//验证老手机
		if(!"0".equals(returnMap1.get("result"))){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"旧手机"+returnMap1.get("msg"));
		}
		Map<String, String> returnMap2 = checkVerifyCode(newMobile, newCode);//验证新手机
		if(!"0".equals(returnMap2.get("result"))){
			throw new PropertyException(JSYError.BAD_REQUEST.getCode(),"新手机"+returnMap2.get("msg"));
		}
		//用户是否已注册
		boolean exists = adminUserService.checkUserExists(newMobile);
		if(exists){
			return CommonResult.error(JSYError.DUPLICATE_KEY.getCode(),"手机号已被注册，请直接登录或找回密码");
		}
		//更换手机号操作
		boolean b = adminUserService.changeMobile(newMobile, oldMobile);
		if(b){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			//销毁token
			String token = request.getHeader("token");
			if (StrUtil.isBlank(token)) {
				token = request.getParameter("token");
			}
			userUtils.destroyToken("Admin:Login", token);
		}
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"操作失败");
	}
	//============== 个人中心相关end ===============
	
	//============== 通用start ==========================
	//检查手机验证码
	private Map<String,String> checkVerifyCode(String mobile, String code) {
		Object oldCode = redisTemplate.opsForValue().get("vCodeAdmin:" + mobile);
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("result","0");
		if (oldCode == null) {
			returnMap.put("result","1");
			returnMap.put("msg","验证码已失效");
//			throw new PropertyException("验证码已失效");
		}else if (!oldCode.equals(code)) {
			returnMap.put("result","1");
			returnMap.put("msg","验证码错误");
//			throw new PropertyException("验证码错误");
		}
		return returnMap;
		// 验证通过后删除验证码
//        redisTemplate.delete(account);
	}
	//============== 通用end ==========================
	//==================================== 物业端（新）end ====================================
}
