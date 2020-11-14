package com.jsy.community.controller;


import com.jsy.community.api.IFrontMenuService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FrontMenuEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
public class FrontMenuController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IFrontMenuService frontMenuService;
	
	@ApiOperation("分页查询所有菜单")
	@PostMapping(value = "/listFrontMenu", produces = "application/json;charset=utf-8")
	public CommonResult listFrontMenu(@RequestBody BaseQO<FrontMenuEntity> baseQO){
		List<FrontMenuEntity> list = frontMenuService.listFrontMenu(baseQO);
		return CommonResult.ok(list);
	}
	
	
	 @ApiOperation("添加菜单信息")
	@PostMapping(value = "/saveMenu", produces = "application/json;charset=utf-8")
	public CommonResult saveMenu(@RequestBody FrontMenuEntity menuEntity){
		frontMenuService.saveMenu(menuEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改菜单信息")
	@PutMapping(value = "/updateMenu", produces = "application/json;charset=utf-8")
	public CommonResult updateMenu(@RequestBody FrontMenuEntity menuEntity){
		frontMenuService.updateMenu(menuEntity);
		return CommonResult.ok();
	}
	
	
	
	
	
	
}

