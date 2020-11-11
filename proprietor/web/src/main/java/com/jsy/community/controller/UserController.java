package com.jsy.community.controller;

import com.jsy.community.api.IUserAuthService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@Api(tags = "用户控制器")
@RestController
public class UserController {
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserAuthService userAuthService;
	
	@GetMapping("test")
	@ApiOperation("test")
	public void test() {
		userAuthService.getList(true);
	}
}
