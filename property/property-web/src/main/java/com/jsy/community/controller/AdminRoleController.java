package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author chq459799974
 * @description 角色管理
 * @since 2020-12-14 15:37
 **/
@ApiJSYController
@RestController
@Login
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
	@businessLog(operation = "新增",content = "新增了【角色】")
	public CommonResult addRole(@RequestBody AdminRoleEntity adminRoleEntity){
		ValidatorUtils.validateEntity(adminRoleEntity);
		adminRoleEntity.setCompanyId(UserUtils.getAdminCompanyId());
		return adminConfigService.addRole(adminRoleEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 删除角色
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("")
	@businessLog(operation = "删除",content = "删除了【角色】")
	public CommonResult delRole(@RequestParam("id") Long id){
		return adminConfigService.delRole(id,UserUtils.getAdminCompanyId()) ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
	}
	
	/**
	* @Description: 修改角色
	 * @Param: [sysRoleQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PutMapping("")
	@businessLog(operation = "编辑",content = "更新了【角色】")
	public CommonResult updateRole(@RequestBody AdminRoleQO adminRoleQO){
		if(adminRoleQO.getId() == null){
			return CommonResult.error("缺少ID");
		}
		adminRoleQO.setCompanyId(UserUtils.getAdminCompanyId());
		return adminConfigService.updateRole(adminRoleQO) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	* @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("/page")
	public CommonResult queryPage(@RequestBody BaseQO<AdminRoleEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new AdminRoleEntity());
		}
		baseQO.getQuery().setCompanyId(UserUtils.getAdminCompanyId());
		return CommonResult.ok(adminConfigService.queryPage(baseQO),"查询成功");
	}

	/**
	 * @author: Pipi
	 * @description: 查询角色详情
	 * @param roleId: 角色ID
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/9 10:23
	 **/
	@Login
	@GetMapping("/roleDetail")
	public CommonResult roleDetail(@RequestParam("roleId") Long roleId) {
		return CommonResult.ok(adminConfigService.queryRoleDetail(roleId, UserUtils.getAdminCompanyId()), "查询成功!");
	}
}

