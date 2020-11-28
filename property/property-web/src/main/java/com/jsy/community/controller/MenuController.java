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
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List < com.jsy.community.vo.menu.FrontParentMenu>>
	 * @Author lihao
	 * @Description 后台树形结构查询所有菜单
	 * @Date 2020/11/24 11:05
	 * @Param []
	 **/
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listMenu")
	public CommonResult<List<FrontParentMenu>> listMenu(@RequestParam("communityId") Long communityId) {
		List<FrontParentMenu> parentMenus = menuService.listMenu(communityId);
		return CommonResult.ok(parentMenus);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 查询所有父菜单
	 * @Date 2020/11/14 22:10
	 * @Param []
	 **/
	@ApiOperation("查询所有父菜单信息")
	@GetMapping("/listParentMenu")
	public CommonResult<List<AdminMenuEntity>> listParentMenu() {
		List<AdminMenuEntity> list = adminMenuService.listParentMenu();
		return CommonResult.ok(list);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List < com.jsy.community.entity.AdminMenuEntity>>
	 * @Author lihao
	 * @Description 查询所有子菜单
	 * @Date 2020/11/24 11:04
	 * @Param []
	 **/
	@ApiOperation("查询所有子菜单信息")
	@GetMapping("/listChildMenu")
	public CommonResult<List<AdminMenuEntity>> listChildMenu() {
		List<AdminMenuEntity> list = adminMenuService.listChildMenu();
		return CommonResult.ok(list);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List < com.jsy.community.entity.AdminMenuEntity>>
	 * @Author lihao
	 * @Description 根据父菜单id查询其子菜单信息
	 * @Date 2020/11/25 9:10
	 * @Param [parentId]
	 **/
	@ApiOperation("根据父菜单id查询其子菜单信息")
	@GetMapping("/listChildMenuById")
	public CommonResult<List<AdminMenuEntity>> listChildMenuById(@RequestParam("parentId") Long parentId) {
		List<AdminMenuEntity> list = adminMenuService.listChildMenuById(parentId);
		return CommonResult.ok(list);
	}

//	// TODO 表单形式添加菜单信息
//	@ApiOperation("添加菜单信息")
//	@PostMapping(value = "/saveMenu", produces = "application/json;charset=utf-8")
//	public CommonResult saveMenu(@RequestBody FrontMenuEntity menuEntity) {
//		menuService.saveMenu(menuEntity);
//		return CommonResult.ok();
//	}

//	@ApiOperation("根据id查询菜单信息")
//	@GetMapping("/getMenuById")
//	public CommonResult<FrontMenuVO> getMenuById(@RequestParam("id") Long id) {
//		// 回显
//		FrontMenuVO frontMenuVo = menuService.getMenuById(id);
//		return CommonResult.ok(frontMenuVo);
//	}

//	// TODO 表单形式修改
//	@ApiOperation("修改菜单信息")
//	@PostMapping(value = "/updateMenu", produces = "application/json;charset=utf-8")
//	public CommonResult updateMenu(@RequestParam("id") Long id,
//	                               @RequestBody FrontMenuVO frontMenuVo) {
//		menuService.updateMenu(id, frontMenuVo);
//		return CommonResult.ok();
//	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 删除菜单信息
	 * @Date 2020/11/25 9:13
	 * @Param [id]
	 **/
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

//	@ApiOperation("批量删除菜单")
//	@DeleteMapping("/removeListMenu")
//	public CommonResult removeListMenu(@ApiParam(value = "需要删除的id【数组】")
//		                                   Long[] ids) {
//		try {
//			menuService.removeListMenu(ids);
//			return CommonResult.ok();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return CommonResult.error("请先删除子菜单");
//		}
//	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 新增父菜单信息
	 * @Date 2020/11/25 9:13
	 * @Param [adminMenuEntity]
	 **/
	@ApiOperation("新增父菜单信息")
	@PostMapping("/addParentMenu")
	public CommonResult addParentMenu(@RequestBody AdminMenuEntity adminMenuEntity) {
		Long parentId = menuService.addParentMenu(adminMenuEntity);
		return CommonResult.ok(parentId);//返回新增后数据的id
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 新增子菜单信息
	 * @Date 2020/11/25 9:13
	 * @Param [adminMenuEntity]
	 **/
	@ApiOperation("新增子菜单信息")
	@PostMapping("/addChildMenu")
	public CommonResult addChildMenu(@RequestBody AdminMenuEntity adminMenuEntity) {
		menuService.addChildMenu(adminMenuEntity);
		return CommonResult.ok();
	}
	
}

