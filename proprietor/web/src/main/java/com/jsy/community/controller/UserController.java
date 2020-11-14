package com.jsy.community.controller;

import com.jsy.community.api.IUserAuthService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("user")
@Api(tags = "用户控制器")
@RestController
public class UserController {
	@DubboReference(version = Const.version, group = Const.group, check = false, timeout = 10000)
	private IUserAuthService userAuthService;
	
	@PostMapping("test")
	@ApiOperation("test")
	public List<UserAuthEntity> test(@RequestBody BaseQO<UserEntity> qo) {
		ValidatorUtils.validateEntity(qo);
		
		return userAuthService.getList(true);
	}
}
