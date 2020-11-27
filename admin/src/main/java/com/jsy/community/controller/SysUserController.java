package com.jsy.community.controller;

import com.jsy.community.entity.SysUserEntity;
import com.jsy.community.utils.SimpleMailSender;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
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
	
	@Value("${emailLinkExpiretime}")
	public long emailLinkExpiretime;
	
	@Resource
	private SimpleMailSender simpleMailSender;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@PostMapping("invitation")
	public CommonResult invitation(@RequestBody SysUserEntity sysUserEntity) {
		ValidatorUtils.validateEntity(sysUserEntity,SysUserEntity.inviteUserValidatedGroup.class);
		//redis暂存邮件邀请
		redisTemplate.opsForValue().set("AdminInvite:" + sysUserEntity.getEmail(),sysUserEntity.getRealName(),emailLinkExpiretime, TimeUnit.HOURS);
		//TODO token获取uid，查询邀请者姓名invitor
		String invitor = "张先森";
		simpleMailSender.sendTemplateMail(sysUserEntity,invitor);
		return CommonResult.ok();
	}
	
	@GetMapping("activation")
	public ModelAndView activation(){
		//TODO 数据库添加用户
		//TODO 发邮件通知
		ModelAndView mv = new ModelAndView("mail/activation.html");
		mv.addObject("password", UUID.randomUUID().toString().substring(0,6));
		return mv;
	}

}
