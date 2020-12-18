package com.jsy.community.controller;

import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.entity.admin.AdminRoleMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
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
public class AdminRoleController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	
	/**
	* @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("")
	public CommonResult addRole(@RequestBody AdminRoleEntity adminRoleEntity){
		ValidatorUtils.validateEntity(adminRoleEntity);
		boolean b = adminConfigService.addRole(adminRoleEntity);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
	}
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("")
	public CommonResult delMenu(@RequestParam("id") Long id){
		boolean b = adminConfigService.delRole(id);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
	}
	
	/**
	* @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PutMapping("")
	public CommonResult updateMenu(@RequestBody AdminRoleQO adminRoleQO){
		boolean b = adminConfigService.updateRole(adminRoleQO);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改失败");
	}
	
	/**
	* @Description: 角色列表
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("")
	public CommonResult<List<AdminRoleEntity>> listOfMenu(){
		return CommonResult.ok(adminConfigService.listOfRole());
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
	public CommonResult setUserRoles(@RequestBody AdminRoleMenuEntity sysRoleMenuEntity){
		boolean b = adminConfigService.setRoleMenus(sysRoleMenuEntity.getMenuIds(), sysRoleMenuEntity.getRoleId());
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置角色菜单失败");
	}
}
