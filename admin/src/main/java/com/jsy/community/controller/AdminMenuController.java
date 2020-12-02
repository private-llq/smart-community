package com.jsy.community.controller;


import com.jsy.community.entity.AdminMenuEntity;
import com.jsy.community.service.IAdminMenuService;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "APP菜单控制器")
//@Login
@Slf4j
@RestController
@RequestMapping("/community/adminMenu")
public class AdminMenuController {
	
	@Autowired
	private IAdminMenuService adminMenuService;
	
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listAdminMenu")
	public CommonResult<List<FrontParentMenu>> listAdminMenu() {
		List<FrontParentMenu> parentMenus = adminMenuService.listAdminMenu();
		return CommonResult.ok(parentMenus);
	}
	
	@ApiOperation("新增父菜单")
	@PostMapping("/insertAdminMenu")
	public CommonResult insertAdminMenu(@RequestBody AdminMenuEntity adminMenu) {
		adminMenuService.insertAdminMenu(adminMenu);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeAdminMenu")
	public CommonResult removeAdminMenu(@RequestParam("id") Long id) {
		adminMenuService.removeAdminMenu(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("新增子菜单")
	@PostMapping("/insertChildMenu")
	public CommonResult insertChildMenu(@RequestBody AdminMenuEntity adminMenu){
		adminMenuService.insertChildMenu(adminMenu);
		return CommonResult.ok();
	}
	
	
	
	
}

