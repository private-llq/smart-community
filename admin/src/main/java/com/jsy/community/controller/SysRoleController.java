package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.sys.SysRoleEntity;
import com.jsy.community.entity.sys.SysRoleMenuEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.sys.SysRoleQO;
import com.jsy.community.service.ISysConfigService;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chq459799974
 * @description 系统角色
 * @since 2020-12-14 15:37
 **/
@RestController
@RequestMapping("role")
// @ApiJSYController
public class SysRoleController {
	
	@Resource
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
	@businessLog(operation = "新增",content = "新增了【系统角色】")
	@Permit("community:admin:role:add")
	public CommonResult addRole(@RequestBody SysRoleEntity sysRoleEntity){
		ValidatorUtils.validateEntity(sysRoleEntity);
		sysRoleEntity.setId(Long.valueOf(UserUtils.getUserId()));
		sysConfigService.addRole(sysRoleEntity);
		return CommonResult.ok("添加成功");
	}
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("delete")
	@businessLog(operation = "删除",content = "删除了【系统角色】")
	@Permit("community:admin:role:delete")
	public CommonResult delMenu(@RequestParam("id") Long id){
		sysConfigService.delRole(id);
		return CommonResult.ok("删除成功");
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
	@businessLog(operation = "编辑",content = "更新了【系统角色】")
	@Permit("community:admin:role:update")
	public CommonResult updateMenu(@RequestBody SysRoleQO sysRoleQO){
		String id = UserUtils.getUserId();
		sysConfigService.updateRole(sysRoleQO, Long.valueOf(id));
		return CommonResult.ok("修改成功");
	}
	
	/**
	* @Description: 角色列表
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("list")
	@Permit("community:admin:role:list")
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
	@PostMapping("/page")
	@Permit("community:admin:role:page")
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
	@Permit("community:admin:role:menus")
	public CommonResult setUserRoles(@RequestBody SysRoleMenuEntity sysRoleMenuEntity){
		boolean b = sysConfigService.setRoleMenus(sysRoleMenuEntity.getMenuIds(), sysRoleMenuEntity.getRoleId());
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置角色菜单失败");
	}
	
	/**
	 * @author: DKS
	 * @description: 查询角色详情
	 * @param roleId: 角色ID
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/10/18 16:10
	 **/
	@GetMapping("/roleDetail")
	@Permit("community:admin:role:roleDetail")
	public CommonResult roleDetail(@RequestParam("roleId") Long roleId) {
		return CommonResult.ok(sysConfigService.queryRoleDetail(roleId));
	}
}
