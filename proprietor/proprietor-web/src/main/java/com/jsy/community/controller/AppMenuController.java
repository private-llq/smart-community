package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
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
 * 菜单 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "APP首页菜单控制器")
@Slf4j
@RestController
@RequestMapping("/menu")
@Login(allowAnonymous = true)
@ApiJSYController
public class AppMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IAppMenuService appMenuService;
	
	// TODO: 2021/2/3 这里应该设计成  打开app，如果没有选择社区  就不展示菜单  而不是默认走社区1的菜单
	// TODO: 2021/2/3 还有这里首页展示菜单的问题  后面再说吧  到时候再到中间表添加个字段排序 主要还是需求不合理的问题
	@ApiOperation("查询首页展示的菜单选项")
	@GetMapping("/listIndexMenu")
	public CommonResult listIndexMenu(@ApiParam(value = "社区id")
	                                  @RequestParam(value = "communityId", defaultValue = "1", required = false) Long communityId) {
		List<AppMenuEntity> list = appMenuService.listIndexMenu(communityId);
		return CommonResult.ok(list);
		
	}
	
	@ApiOperation("更多菜单")
	@GetMapping("/moreListMenu")
	public CommonResult moreListMenu(@ApiParam(value = "社区id")
	                                 @RequestParam(value = "communityId", defaultValue = "1", required = false) Long communityId) {
		List<FrontParentMenu> list = appMenuService.moreIndexMenu(communityId);
		return CommonResult.ok(list);
	}
	
	
}

