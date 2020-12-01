package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.SysUserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.SysUserMapper;
import com.jsy.community.service.ISysUserService;
import com.jsy.community.utils.JwtUtils;
import com.jsy.community.utils.SimpleMailSender;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chq459799974
 * @description 系统用户
 * @since 2020-11-27 16:02
 **/
@RequestMapping("sys/user")
@Api(tags = "系统用户控制器")
//@Login
@Slf4j
@RestController
public class SysUserController {
	
	@Autowired
	private ISysUserService iSysUserService;

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
		Map<String, String> resultMap = iSysUserService.invitation(sysUserEntity);
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
			mv.setViewName("main/error.html");
			return mv;
		}
		Map<String, String> resultMap = iSysUserService.activation(sysUserEntity);
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
		iSysUserService.updateUser(sysUserEntity);
		return null;
	}
	
	//用户名ajax查重
	
	
	//邮箱注册后添加用户名
	@PutMapping("username")
	public CommonResult setUserName(@RequestParam String userName){
		//TODO TOKEN获取uid
		Long uid = 1L;
		Map<String, String> resultMap = iSysUserService.setUserName(uid, userName);
		return Boolean.parseBoolean(String.valueOf(resultMap.get("result"))) ? CommonResult.ok() : CommonResult.error(Integer.parseInt(resultMap.get("code")),resultMap.get("reason"));
	}
	
	//添加手机号(短信验证)
	@PutMapping("mobile")
	public CommonResult changeMobile(){
		return null;
	}

}
