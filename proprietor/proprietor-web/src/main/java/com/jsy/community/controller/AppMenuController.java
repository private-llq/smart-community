package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * APP首页菜单控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "APP首页菜单控制器")
@Slf4j
@RestController
@RequestMapping("/menu")
@ApiJSYController
public class AppMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IAppMenuService appMenuService;
	
	@ApiOperation("查询首页展示的菜单选项")
	@GetMapping("/listIndexMenu")
	@Permit("community:proprietor:menu:listIndexMenu")
	public CommonResult listIndexMenu(@ApiParam(value = "社区id") @RequestParam Long communityId) {
		List<AppMenuEntity> list = appMenuService.listIndexMenu(communityId);
		return CommonResult.ok(list);
		
	}
	
	@ApiOperation("更多菜单")
	@GetMapping("/moreListMenu")
	@Permit("community:proprietor:menu:moreListMenu")
	public CommonResult moreListMenu(@ApiParam(value = "社区id") @RequestParam Long communityId) {
		List<AppMenuEntity> list = appMenuService.moreIndexMenu(communityId);
		return CommonResult.ok(list);
	}

	@ApiOperation("查询首页展示的菜单选项")
	@GetMapping("/listIndexMenu/v2")
	@Permit("community:proprietor:menu:listIndexMenu:v2")
	public CommonResult listIndexMenu2(@ApiParam(value = "社区id") @RequestParam Long communityId) {
		List<AppMenuEntity> list = appMenuService.listAppMenu(communityId);
		return CommonResult.ok(list);

	}
	@ApiOperation("更多菜单")
	@GetMapping("/moreListMenu/v2")
	@Permit("community:proprietor:menu:moreListMenu:v2")
	public CommonResult moreListMenuV2(@ApiParam(value = "社区id") @RequestParam Long communityId) {
		List<AppMenuEntity> list = appMenuService.listAppMenuAll(communityId);
		return CommonResult.ok(list);

	}

}

