package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAdminMenuService;
import com.jsy.community.api.IMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @return
 * @Author lihao
 * @Description 物业端前台菜单控制器
 * @Date 2020/11/14 22:10
 * @Param
 **/
@Api(tags = "物业端控制本小区菜单控制器")
@RestController
@RequestMapping("/menu")
@Login(allowAnonymous = true)
@ApiJSYController
public class MenuController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IMenuService menuService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IAdminMenuService adminMenuService;
	
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listMenu")
	public CommonResult<List<FrontParentMenu>> listMenu(@RequestParam("communityId") Long communityId) {
		List<FrontParentMenu> parentMenus = menuService.listMenu(communityId);
		return CommonResult.ok(parentMenus);
	}
	
	@ApiOperation("查询所有父菜单信息")
	@GetMapping("/listParentMenu")
	public CommonResult<List<AdminMenuEntity>> listParentMenu() {
		List<AdminMenuEntity> list = adminMenuService.listParentMenu();
		return CommonResult.ok(list);
	}
	
	@ApiOperation("根据父菜单id查询其子菜单信息")
	@GetMapping("/listChildMenuById")
	public CommonResult<List<AdminMenuEntity>> listChildMenuById(@RequestParam("parentId") Long parentId) {
		List<AdminMenuEntity> list = adminMenuService.listChildMenuById(parentId);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeMenu")
	public CommonResult removeMenu(@RequestParam("id") Long id) {
		try {
			menuService.removeMenu(id);
			return CommonResult.ok();
		} catch (Exception e) {
			return CommonResult.error("请先删除子菜单");
		}
	}
	
	@ApiOperation("新增父菜单信息")
	@PostMapping("/addParentMenu")
	public CommonResult addParentMenu(@RequestBody AdminMenuEntity adminMenuEntity) {
		Long parentId = menuService.addParentMenu(adminMenuEntity);
		return CommonResult.ok(parentId);//返回新增后数据的id
	}
	
	@ApiOperation("新增子菜单信息")
	@PostMapping("/addChildMenu")
	public CommonResult addChildMenu(@RequestBody AdminMenuEntity adminMenuEntity) {
		// TODO 新增的时候 让用户选择是否展示在首页，首页的位置根据序号来
		menuService.addChildMenu(adminMenuEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改子菜单信息")
	@PutMapping("/updateChildMenu")
	public CommonResult updateChildMenu(@RequestBody AdminMenuEntity adminMenuEntity) {
		menuService.updateChildMenu(adminMenuEntity);
		return CommonResult.ok();
	}
	
}

