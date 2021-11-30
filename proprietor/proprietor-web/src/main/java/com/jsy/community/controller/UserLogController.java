package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IUserLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLoginLogEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chq459799974
 * @description 用户日志
 * @since 2020-11-28 16:49
 **/
@Api(tags = "用户日志控制器")
@RestController
@RequestMapping("user/log")
// @ApiJSYController
public class UserLogController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserLogService iUserLogService;
	
	@PostMapping("")
	@Permit("community:proprietor:user:log")
	public CommonResult addUserLoginLog(@RequestBody UserLoginLogEntity userLoginLogEntity){
		userLoginLogEntity.setUid(UserUtils.getUserId());
		boolean result = iUserLogService.addUserLoginLog(userLoginLogEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增用户登录日志失败");
	}
	
}
