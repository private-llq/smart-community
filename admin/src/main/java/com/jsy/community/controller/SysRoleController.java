package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysRoleMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:37
 **/
@RestController
@RequestMapping("role")
@ApiJSYController
public class SysRoleController {
	
	@Autowired
	private ISysConfigService sysConfigService;
	
	/**
	* @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("add")
	@Transactional(rollbackFor = Exception.class)
	public CommonResult addRole(@RequestBody SysRoleEntity sysRoleEntity){
		ValidatorUtils.validateEntity(sysRoleEntity);
		boolean b = sysConfigService.addRole(sysRoleEntity);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
	}
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("delete")
	public CommonResult delMenu(@RequestParam("id") Long id){
		boolean b = sysConfigService.delRole(id);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
	}
	
	/**
	* @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PutMapping("update")
	@Transactional(rollbackFor = Exception.class)
	public CommonResult updateMenu(@RequestBody SysRoleQO sysRoleQO){
		boolean b = sysConfigService.updateRole(sysRoleQO);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改失败");
	}
	
	/**
	* @Description: 角色列表
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("list")
	public CommonResult<List<SysRoleEntity>> listOfMenu(){
		return CommonResult.ok(sysConfigService.listOfRole());
	}
	
	/**
	 * @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	 **/
	@Login
	@PostMapping("/page")
	public CommonResult queryPage(@RequestBody BaseQO<SysRoleEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new SysRoleEntity());
		}
		return CommonResult.ok(sysConfigService.queryPage(baseQO),"查询成功");
	}
	
	/** 
	* @Description: 为角色设置菜单
	 * @Param: [sysRoleMenuEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/15
	**/
	@Transactional(rollbackFor = Exception.class)
	@PostMapping("menus")
	public CommonResult setUserRoles(@RequestBody SysRoleMenuEntity sysRoleMenuEntity){
		boolean b = sysConfigService.setRoleMenus(sysRoleMenuEntity.getMenuIds(), sysRoleMenuEntity.getRoleId());
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置角色菜单失败");
	}
}
