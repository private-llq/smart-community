package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminUserQO;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SmsUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
	
	/**
	* @Description: 操作员条件查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@PostMapping("query")
	public CommonResult queryOperator(@RequestBody BaseQO<AdminUserQO> baseQO){
		if(baseQO.getQuery() == null || baseQO.getQuery().getCommunityId() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少社区ID");
		}
		//TODO 一个手机号可以注册多个社区，根据token取UID查询拥有社区ID数组(或社区ID数组直接存到redis)，验证前端传过来的id是否in 社区ID数组(是否具有社区权限)
		return CommonResult.ok(adminUserService.queryOperator(baseQO),"查询成功");
	}
	
	/**
	* @Description: 添加操作员
	 * @Param: [adminUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@PostMapping("")
	public CommonResult addOperator(@RequestBody AdminUserEntity adminUserEntity){
		//TODO 获取操作员UID验证操作权限(1.是否是超级管理员 2.是否有操作员管理权限 3.是否具有社区权限) ， 操作带上社区id
		//TODO 数据正则验证 手机号 身份证等
		ValidatorUtils.validateEntity(adminUserEntity,AdminUserEntity.addOperatorValidatedGroup.class);
		return adminUserService.addOperator(adminUserEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 编辑操作员
	 * @Param: [adminUserEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/3/17
	**/
	@PutMapping("")
	public CommonResult updateOperator(@RequestBody AdminUserEntity adminUserEntity){
		//TODO 获取操作员UID验证操作权限(1.是否是超级管理员 2.是否有操作员管理权限 3.是否具有社区权限) ， 操作带上社区id
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
	@PutMapping("pass/reset")
	public CommonResult resetPass(@RequestParam Long id){
		//TODO 获取操作员UID验证操作权限(1.是否是超级管理员 2.是否有操作员管理权限 3.是否具有社区权限) ， 操作带上社区id
		String uid = "1a7a182d711e441fbb24659090daf5cb";
		return adminUserService.resetPassword(id,uid) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	//==================================== 物业端（新）end ====================================
}
