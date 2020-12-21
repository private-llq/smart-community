package com.jsy.community.controller;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.api.IAdminUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.entity.admin.AdminUserRoleEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
public class AdminUserController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminUserService adminUserService;
	
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
	public CommonResult setUserRoles(@RequestBody AdminUserRoleEntity adminUserRoleEntity){
		boolean b = adminUserService.setUserRoles(adminUserRoleEntity.getRoleIds(), adminUserRoleEntity.getUserId());
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
	public CommonResult invitation(@RequestBody AdminUserEntity sysUserEntity) {
		ValidatorUtils.validateEntity(sysUserEntity,AdminUserEntity.inviteUserValidatedGroup.class);
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String token = request.getHeader("token");
		if (StrUtil.isBlank(token)) {
			token = request.getParameter("token");
		}
		AdminInfoVo adminInfo = userUtils.getAdminInfo(token);
		sysUserEntity.setCreateUserName(adminInfo.getRealName());//邀请者姓名
		sysUserEntity.setCreateUserId(UserUtils.getUserId());//邀请者uid
		Map<String, String> resultMap = adminUserService.invitation(sysUserEntity);
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
	public ModelAndView activation(AdminUserEntity adminUserEntity){
		ModelAndView mv = new ModelAndView();
		// 链接参数有误
		if(StringUtils.isEmpty(adminUserEntity.getEmail()) || adminUserEntity.getCreateUserId() == null){
			mv.setViewName("mail/error.html");
			return mv;
		}
		Map<String, String> resultMap = adminUserService.activation(adminUserEntity);
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
		AdminUserEntity adminUserEntity = new AdminUserEntity();
		adminUserEntity.setId(uid);
		adminUserEntity.setStatus(1);
		boolean b = adminUserService.updateUser(adminUserEntity);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"禁用失败");
	}
	
	//用户名ajax查重
	
	
	//邮箱注册后添加用户名
	@PutMapping("username")
	public CommonResult setUserName(@RequestParam String userName){
		//TODO TOKEN获取uid
		Long uid = 1L;
		Map<String, String> resultMap = adminUserService.setUserName(uid, userName);
		return Boolean.parseBoolean(String.valueOf(resultMap.get("result"))) ? CommonResult.ok() : CommonResult.error(Integer.parseInt(resultMap.get("code")),resultMap.get("reason"));
	}
	
	//添加手机号(短信验证)
	@PutMapping("mobile")
	public CommonResult changeMobile(){
		Long uid = 1L;
		return CommonResult.ok();
	}

}
