package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.UserAccountRecordQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author chq459799974
 * @description 用户金钱账户控制器
 * @since 2021-01-08 11:41
 **/
@Api(tags = "用户金钱账户控制器")
@RestController
@RequestMapping("user/account")
@ApiJSYController
@Login
public class UserAccountController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IUserAccountService userAccountService;
	
	/**
	* @Description: 查询余额
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@ApiOperation("【用户账户】查询余额")
	@GetMapping("balance")
	public CommonResult queryBalance(){
		return CommonResult.ok(userAccountService.queryBalance(UserUtils.getUserId()));
	}
	
	/**
	* @Description: 账户交易
	 * @Param: [userAccountRecordQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@ApiOperation("【用户账户】账户交易")
	@PostMapping("trade")
	public CommonResult trade(@RequestBody UserAccountRecordQO userAccountRecordQO){
		ValidatorUtils.validateEntity(userAccountRecordQO);
		userAccountRecordQO.setUid(UserUtils.getUserId());
		userAccountService.trade(userAccountRecordQO);
		return CommonResult.ok("交易完成");
	}
	
}
