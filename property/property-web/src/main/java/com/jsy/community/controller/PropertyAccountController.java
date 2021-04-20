package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPropertyAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chq459799974
 * @description 社区对公账户Controller
 * @since 2021-04-20 17:51
 **/
@Api(tags = "社区对公账户")
@RestController
@RequestMapping("/account")
@ApiJSYController
@Login
public class PropertyAccountController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPropertyAccountService propertyAccountService;
	
	/**
	* @Description: 查询本社区对公账户
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/20
	**/
	@ApiOperation("查询本社区对公账户")
	@GetMapping("")
	public CommonResult queryByCommunityId(){
		return CommonResult.ok(propertyAccountService.queryBankAccount(UserUtils.getAdminCommunityId()),"查询成功");
	}
	
}
