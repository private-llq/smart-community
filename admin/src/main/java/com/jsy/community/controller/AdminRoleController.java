package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.service.AdminRoleService;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DKS
 * @description 大后台管理中台角色
 * @since 2021-12-17 11:34
 **/
@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

	@Resource
	private AdminRoleService adminRoleService;

	/**
	* @Description: 添加角色
	 * @Param: [adminRoleQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: DKS
	 * @Date: 2021/12/17
	**/
	@PostMapping("insert")
	@businessLog(operation = "新增",content = "新增了【中台角色】")
	@Permit("community:admin:admin:role:insert")
	public CommonResult addRole(@RequestBody AdminRoleQO adminRoleQO){
		adminRoleQO.setId(Long.valueOf(UserUtils.getId()));
		adminRoleService.addRole(adminRoleQO);
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
	@businessLog(operation = "删除",content = "删除了【中台角色】")
	@Permit("community:admin:admin:role:delete")
	public CommonResult delRole(@RequestParam("id") List<Long> roleIds){
		adminRoleService.delRole(roleIds);
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
	@businessLog(operation = "编辑",content = "更新了【中台角色】")
	@Permit("community:admin:admin:role:update")
	public CommonResult updateRole(@RequestBody AdminRoleQO adminRoleQO){
		if(adminRoleQO.getId() == null){
			return CommonResult.error("缺少ID");
		}
		String id = UserUtils.getId();
		adminRoleService.updateRole(adminRoleQO, Long.valueOf(id));
		return CommonResult.ok("修改成功");
	}

	/**
	* @Description: 角色列表 分页查询
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.sys.SysRoleEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("/page")
	@Permit("community:property:role:page")
	public CommonResult queryPage(@RequestBody BaseQO<AdminRoleEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new AdminRoleEntity());
		}
//		baseQO.getQuery().setCompanyId(UserUtils.getAdminCompanyId());
		return CommonResult.ok(adminRoleService.queryPage(baseQO),"查询成功");
	}

	/**
	 * @author: Pipi
	 * @description: 查询角色详情
	 * @param roleId: 角色ID
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/9 10:23
	 **/
	@GetMapping("/roleDetail")
	@Permit("community:property:role:roleDetail")
	public CommonResult roleDetail(@RequestParam("roleId") Long roleId) {
		return CommonResult.ok(adminRoleService.queryRoleDetail(roleId), "查询成功!");
	}
}

