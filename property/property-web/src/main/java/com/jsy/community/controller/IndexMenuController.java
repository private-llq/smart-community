package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAppMenuService;
import com.jsy.community.api.IIndexMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppMenuEntity;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class IndexMenuController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IIndexMenuService menuService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAppMenuService adminMenuService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listAdminMenu")
	public CommonResult<List<FrontParentMenu>> listAdminMenu(@RequestParam("communityId") Long communityId) {
		List<FrontParentMenu> parentMenus = menuService.listAdminMenu(communityId);
		return CommonResult.ok(parentMenus);
	}
	
	@ApiOperation("查询所有父菜单信息")
	@GetMapping("/listParentMenu")
	public CommonResult<List<AppMenuEntity>> listParentMenu() {
		List<AppMenuEntity> list = adminMenuService.listParentMenu();
		return CommonResult.ok(list);
	}
	
	@ApiOperation("根据父菜单id查询其子菜单信息")
	@GetMapping("/listChildMenuById")
	public CommonResult<List<AppMenuEntity>> listChildMenuById(@RequestParam("parentId") Long parentId) {
		List<AppMenuEntity> list = adminMenuService.listChildMenuById(parentId);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeMenu")
	public CommonResult removeMenu(@RequestParam("id") Long id) {
		try {
			redisTemplate.delete("indexMenuList"); // TODO  延时双删
			menuService.removeMenu(id);
			Thread.sleep(500);
			redisTemplate.delete("indexMenuList"); // TODO  延时双删
			return CommonResult.ok();
		} catch (Exception e) {
			return CommonResult.error("请先删除子菜单");
		}
	}
	
	@ApiOperation("新增父菜单信息")
	@PostMapping("/addParentMenu")
	public CommonResult addParentMenu(@RequestBody AppMenuEntity appMenuEntity) {
		ValidatorUtils.validateEntity(appMenuEntity, AppMenuEntity.addAdmin.class);
		Long parentId = menuService.addParentMenu(appMenuEntity);
		return CommonResult.ok(parentId);//返回新增后数据的id
	}
	
	@ApiOperation("新增子菜单信息")
	@PostMapping("/addChildMenu")
	public CommonResult addChildMenu(@RequestBody AppMenuEntity appMenuEntity) {
		// TODO 新增的时候 让用户选择是否展示在首页，首页的位置根据序号来
		// TODO 新增子菜单 没有验证 等与前端联调的时候再调整
		ValidatorUtils.validateEntity(appMenuEntity, AppMenuEntity.addAdmin.class);
		menuService.addChildMenu(appMenuEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改子菜单信息")
	@PutMapping("/updateChildMenu")
	public CommonResult updateChildMenu(@RequestBody AppMenuEntity appMenuEntity) {
		ValidatorUtils.validateEntity(appMenuEntity, AppMenuEntity.updateAdmin.class);
		menuService.updateChildMenu(appMenuEntity);
		return CommonResult.ok();
	}
	
}
