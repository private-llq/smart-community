package com.jsy.community.controller;


import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.web.ApiProprietor;
import com.jsy.community.api.IFrontMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.menu.FrontMenuVo;
import com.jsy.community.vo.menu1.FrontParentMenu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author jsy
 * @since 2020-11-14
 */
@Api(tags = "前台菜单控制器")
@RestController
@RequestMapping("/front/menu")
@Login(allowAnonymous = true)
@ApiProprietor
public class FrontMenuController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IFrontMenuService frontMenuService;
	
	// TODO 待定  分页  或  树形结构
	@ApiOperation("后台分页查询所有菜单")
	@PostMapping(value = "/listFrontMenu", produces = "application/json;charset=utf-8")
	public CommonResult listFrontMenu(@RequestBody BaseQO<FrontMenuEntity> baseQO) {
		List<FrontMenuVo> list = frontMenuService.listFrontMenu(baseQO);
		return CommonResult.ok(list);
	}
	
	// TODO 树形结构
	@ApiOperation("后台树形结构查询所有菜单")
	@GetMapping("/listMenu")
	public CommonResult listMenu() {
		List<FrontParentMenu> parentMenus = frontMenuService.listMenu();
		return CommonResult.ok(parentMenus);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 查询所有父菜单  用于表单添加时候  选择所属父菜单
	 * @Date 2020/11/14 22:10
	 * @Param []
	 **/
	@ApiOperation("查询所有父菜单信息")
	@GetMapping("/listParentMenu")
	public CommonResult listParentMenu() {
		List<FrontMenuEntity> list = frontMenuService.listParentMenu();
		return CommonResult.ok(list);
	}
	
	// TODO 表单形式添加菜单信息
	@ApiOperation("添加菜单信息")
	@PostMapping(value = "/saveMenu", produces = "application/json;charset=utf-8")
	public CommonResult saveMenu(@RequestBody FrontMenuEntity menuEntity) {
		frontMenuService.saveMenu(menuEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询菜单信息")
	@GetMapping("/getMenuById/{id}")
	public CommonResult getMenuById(@PathVariable Long id) {
		// 回显
		FrontMenuVo frontMenuVo = frontMenuService.getMenuById(id);
		return CommonResult.ok(frontMenuVo);
	}
	
	// TODO 表单形式修改
	@ApiOperation("修改菜单信息")
	@PostMapping(value = "/updateMenu/{id}", produces = "application/json;charset=utf-8")
	public CommonResult updateMenu(@PathVariable Long id,
	                               @RequestBody FrontMenuVo frontMenuVo) {
		frontMenuService.updateMenu(id, frontMenuVo);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除菜单信息")
	@DeleteMapping("/removeMenu/{id}")
	public CommonResult removeMenu(@PathVariable Long id) {
		try {
			frontMenuService.removeMenu(id);
			return CommonResult.ok();
		} catch (Exception e) {
			return CommonResult.error("请先删除子菜单");
		}
	}
	
	
	@ApiOperation("查询首页展示的菜单选项")
	@GetMapping("/listIndexMenu")
	public CommonResult listIndexMenu() {
		List<FrontMenuEntity> list = frontMenuService.listIndexMenu();
		return CommonResult.ok(list);
	}
	
	@ApiOperation("批量删除菜单")
	@DeleteMapping("/removeListMenu/{ids}")
	public CommonResult removeListMenu(@ApiParam(value = "需要删除的id【数组】")
	                                   @PathVariable Long[] ids) {
		try {
			frontMenuService.removeListMenu(ids);
			return CommonResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return CommonResult.error("请先删除子菜单");
		}
	}
	
	@ApiOperation("更多菜单")
	@GetMapping("/moreListMenu")
	public CommonResult moreListMenu() {
		List<FrontMenuVo> list = frontMenuService.moreListMenu();
		return CommonResult.ok(list);
	}
	
}

