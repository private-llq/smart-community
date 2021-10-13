package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.sys.SysUserEntity;
import com.jsy.community.entity.sys.SysUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.ResetPasswordQO;
import com.jsy.community.qo.sys.SysUserQO;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 系统用户
 * @since 2020-11-27 16:02
 **/
@RequestMapping("sys/user")
@Api(tags = "系统用户控制器")
@Slf4j
@RestController
public class SysUserController {
	
	@Autowired
	private ISysUserService sysUserService;
	
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private UserUtils userUtils;

	/**
	* @Description: 设置用户角色
	 * @Param: [sysUserRoleEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("roles")
	public CommonResult setUserRoles(@RequestBody SysUserRoleEntity sysUserRoleEntity){
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(sysUserRoleEntity.getRoleId());
		boolean b = sysUserService.setUserRoles(roleIds, sysUserRoleEntity.getUserId());
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置用户角色失败");
	}
	
	/**
	* @Description: 邮件注册邀请
	 * @Param: [sysUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	@PostMapping("invitation")
	public CommonResult invitation(@RequestBody SysUserEntity sysUserEntity) {
		ValidatorUtils.validateEntity(sysUserEntity,SysUserEntity.inviteUserValidatedGroup.class);
		Map<String, String> resultMap = sysUserService.invitation(sysUserEntity);
		return Boolean.parseBoolean(resultMap.get("result")) ? CommonResult.ok() : CommonResult.error(JSYError.REQUEST_PARAM.getCode(),resultMap.get("reason"));
	}
	
	/**
	* @Description: 邮件注册激活确认
	 * @Param: [sysUserEntity]
	 * @Return: org.springframework.web.servlet.ModelAndView
	 * @Author: chq459799974
	 * @Date: 2020/11/30
	**/
	@GetMapping("activation")
	public ModelAndView activation(SysUserEntity sysUserEntity){
		ModelAndView mv = new ModelAndView();
		// 链接参数有误
		if(StringUtils.isEmpty(sysUserEntity.getEmail()) || sysUserEntity.getCreateUserId() == null){
			mv.setViewName("mail/error.html");
			return mv;
		}
		Map<String, String> resultMap = sysUserService.activation(sysUserEntity);
		mv.addObject("reason",resultMap.get("reason"));
		mv.addObject("password",resultMap.get("password"));
		mv.setViewName(resultMap.get("templateName"));
		return mv;
	}
	
	/**
	* @Description: 禁用账户
	 * @Param: [uid]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/1
	**/
	@PutMapping("disable")
	public CommonResult disableUser(@RequestParam Long uid){
		SysUserEntity sysUserEntity = new SysUserEntity();
		sysUserEntity.setId(uid);
		sysUserEntity.setStatus(1);
		boolean b = sysUserService.updateUser(sysUserEntity);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"禁用失败");
	}
	
	//用户名ajax查重
	
	
	//邮箱注册后添加用户名
	@PutMapping("username")
	public CommonResult setUserName(@RequestParam String userName){
		//TODO TOKEN获取uid
		Long uid = 1L;
		Map<String, String> resultMap = sysUserService.setUserName(uid, userName);
		return Boolean.parseBoolean(String.valueOf(resultMap.get("result"))) ? CommonResult.ok() : CommonResult.error(Integer.parseInt(resultMap.get("code")),resultMap.get("reason"));
	}
	
	//添加手机号(短信验证)
	@PutMapping("mobile")
	public CommonResult changeMobile(){
		Long uid = 1L;
		return CommonResult.ok();
	}
	
	/**
	 * @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Login
	@PostMapping("query")
	public CommonResult queryOperator(@RequestBody BaseQO<SysUserQO> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new SysUserQO());
		}
		return CommonResult.ok(sysUserService.queryOperator(baseQO));
	}
	
	/**
	 * @Description: 添加操作员
	 * @Param: [sysUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Login
	@PostMapping("add")
	public CommonResult addOperator(@RequestBody SysUserEntity sysUserEntity){
		ValidatorUtils.validateEntity(sysUserEntity);
		sysUserService.addOperator(sysUserEntity);
		return CommonResult.ok("添加成功");
	}
	
	/**
	 * @Description: 编辑操作员
	 * @Param: [sysUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Login
	@PutMapping("update")
	public CommonResult updateOperator(@RequestBody SysUserEntity sysUserEntity){
		sysUserService.updateOperator(sysUserEntity);
		return CommonResult.ok("操作成功");
	}
	
	/**
	 * @Description: 删除操作员
	 * @Param: [sysUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/10/13
	 **/
	@Login
	@DeleteMapping("delete")
	public CommonResult deleteOperator(Long id){
		sysUserService.deleteOperator(id);
		return CommonResult.ok("操作成功");
	}
	//============== 操作员管理相关end ===============
	
	//============== 个人中心相关start ===============
	
	/**
	 * @Description: 修改/忘记密码
	 * @Param: [body]
	 * @Return: com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 * @Author: DKS
	 * @Date: 2021/10/13
	 *
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
		boolean b = sysUserService.updatePassword(qo,UserUtils.getUserId());
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
	
//	@ApiOperation("更换手机号")
//	@PutMapping("mobile")
//	@Login
//	public CommonResult changeMobile(@RequestBody Map<String,String> map){
//		//入参验证
//		String newMobile = map.get("newMobile");
//		if(StringUtils.isEmpty(newMobile)){
//			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请填写新手机号");
//		}
//		if (!RegexUtils.isMobile(newMobile)) {
//			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请检查手机号格式是否正确");
//		}
//		String oldCode = map.get("oldCode");
//		String newCode = map.get("newCode");
//		if (StrUtil.isEmpty(oldCode)) {
//			throw new JSYException("旧手机号验证码不能为空");
//		}
//		if (StrUtil.isEmpty(newCode)) {
//			throw new JSYException("新手机号验证码不能为空");
//		}
//
//
//		//从请求获取uid
//		String uid = UserUtils.getUserId();
//		//获取当前用户手机号
//		String oldMobile = sysUserService.queryMobileByUid(uid);
//		if(newMobile.equals(oldCode)){
//			throw new AdminException(JSYError.BAD_REQUEST.getCode(),"新手机号与原手机号相同");
//		}
//		//权限验证
//		Map<String, String> returnMap1 = checkVerifyCode(oldMobile, oldCode);//验证老手机
//		if(!"0".equals(returnMap1.get("result"))){
//			throw new AdminException(JSYError.BAD_REQUEST.getCode(),"旧手机"+returnMap1.get("msg"));
//		}
//		Map<String, String> returnMap2 = checkVerifyCode(newMobile, newCode);//验证新手机
//		if(!"0".equals(returnMap2.get("result"))){
//			throw new AdminException(JSYError.BAD_REQUEST.getCode(),"新手机"+returnMap2.get("msg"));
//		}
//
//		//用户是否已注册
//		boolean exists = sysUserService.checkUserExists(newMobile);
//		if(exists){
//			return CommonResult.error(JSYError.DUPLICATE_KEY.getCode(),"手机号已被注册，请直接登录或找回密码");
//		}
//		//更换手机号操作
//		boolean b = sysUserService.changeMobile(newMobile, oldMobile);
//		if(b){
//			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//			//销毁token
//			String token = request.getHeader("token");
//			if (StrUtil.isBlank(token)) {
//				token = request.getParameter("token");
//			}
//			userUtils.destroyToken("Sys:Login", token);
//		}
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"操作失败");
//	}
	//============== 个人中心相关end ===============
	
	//============== 通用start ==========================
	//检查手机验证码
	private Map<String,String> checkVerifyCode(String mobile, String code) {
		Object oldCode = redisTemplate.opsForValue().get("vCodeSys:" + mobile);
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("result","0");
		if (oldCode == null) {
			returnMap.put("result","1");
			returnMap.put("msg","验证码已失效");
		}else if (!oldCode.equals(code)) {
			returnMap.put("result","1");
			returnMap.put("msg","验证码错误");
		}
		return returnMap;
		// 验证通过后删除验证码
//        redisTemplate.delete(account);
	
	}
}
