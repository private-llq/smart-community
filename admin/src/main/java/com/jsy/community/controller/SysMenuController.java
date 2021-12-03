package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.sys.SysMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.sys.SysMenuQO;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统菜单
 * @since 2020-12-14 10:57
 **/
@RestController
@RequestMapping("menu")
@ApiJSYController
public class SysMenuController {
	
	@Resource
	private ISysConfigService sysConfigService;
	
	/**
	* @Description: 新增
	 * @Param: [sysMenuEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@LoginIgnore
	@PostMapping("add")
	@businessLog(operation = "新增",content = "新增了【系统菜单】")
	public CommonResult addMenu(@RequestBody SysMenuEntity sysMenuEntity){
		ValidatorUtils.validateEntity(sysMenuEntity);
		boolean b = sysConfigService.addMenu(sysMenuEntity);
		return b? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
	}
	
	/**
	* @Description: 删除
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@LoginIgnore
	@DeleteMapping("delete")
	@businessLog(operation = "删除",content = "删除了【系统菜单】")
	public CommonResult delMenu(@RequestParam("id") Long id){
		boolean b = sysConfigService.delMenu(id);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
	}
	
	/**
	* @Description: 修改
	 * @Param: [sysMenuQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@LoginIgnore
	@PutMapping("update")
	@businessLog(operation = "编辑",content = "更新了【系统菜单】")
	public CommonResult updateMenu(@RequestBody SysMenuQO sysMenuQO){
		boolean b = sysConfigService.updateMenu(sysMenuQO);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改失败");
	}
	
	/**
	* @Description: 查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.AppMenuEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("query")
	@Permit("community:admin:menu:query")
	public CommonResult<List<SysMenuEntity>> listOfMenu(){
		return CommonResult.ok(sysConfigService.listOfMenu());
	}
}
