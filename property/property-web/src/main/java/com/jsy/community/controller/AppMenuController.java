package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "APP物业菜单控制器")
@Slf4j
@RestController
@ApiJSYController
@RequestMapping("/community/adminMenu")
public class AppMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAppMenuService appMenuService;

	@ApiOperation("查询APP所有父菜单信息")
	@GetMapping("/listParentMenu")
	public CommonResult<List<AppMenuEntity>> listParentMenu() {
		List<AppMenuEntity> list = appMenuService.listParentMenu();
		return CommonResult.ok(list);
	}
	
	@ApiOperation("根据父菜单id查询APP其子菜单信息")
	@GetMapping("/listChildMenuById")
	public CommonResult<List<AppMenuEntity>> listChildMenuById(@RequestParam("parentId") Long parentId) {
		List<AppMenuEntity> list = appMenuService.listChildMenuById(parentId);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listAdminMenu")
	public CommonResult<List<FrontParentMenu>> listAdminMenu(@RequestParam("communityId") Long communityId) {
		List<FrontParentMenu> parentMenus = appMenuService.listAdminMenu(communityId);
		return CommonResult.ok(parentMenus);
	}
	
	@ApiOperation("新增父菜单信息")
	@PostMapping("/addParentMenu")
	public CommonResult addParentMenu(@RequestBody AppMenuEntity appMenuEntity, @RequestParam("communityId") Long communityId) {
		ValidatorUtils.validateEntity(appMenuEntity, AppMenuEntity.addAdmin.class);
		appMenuService.addParentMenu(appMenuEntity, communityId);
		return CommonResult.ok();
	}
	
	@ApiOperation("新增子菜单信息")
	@PostMapping("/addChildMenu")
	public CommonResult addChildMenu(@RequestBody AppMenuEntity appMenuEntity, @RequestParam("communityId") Long communityId) {
		ValidatorUtils.validateEntity(appMenuEntity, AppMenuEntity.addAdmin.class);
		appMenuService.addChildMenu(appMenuEntity, communityId);
		return CommonResult.ok();
	}
	
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeMenu")
	public CommonResult removeMenu(@RequestParam("id") Long id, @RequestParam("communityId") Long communityId) {
		appMenuService.removeMenu(id,communityId);
		return CommonResult.ok();
	}
	
	
}

