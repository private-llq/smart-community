package com.jsy.community.controller;

import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.AdminRoleService;
import com.jsy.community.api.IAdminConfigService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.admin.AdminRoleQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.basecommon.constant.BaseUserConstant;
import com.zhsj.baseweb.annotation.Permit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 角色管理
 * @since 2020-12-14 15:37
 **/
// @ApiJSYController
@RestController
@RequestMapping("role")
public class AdminRoleController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IAdminConfigService adminConfigService;
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private AdminRoleService adminRoleService;
	/**
	* @Description: 添加角色
	 * @Param: [sysRoleEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("")
	@businessLog(operation = "新增",content = "新增了【角色】")
	@Permit("community:property:role")
	public CommonResult addRole(@RequestBody AdminRoleEntity adminRoleEntity){
		ValidatorUtils.validateEntity(adminRoleEntity);
		adminRoleEntity.setId(Long.valueOf(UserUtils.getId()));
		adminRoleEntity.setCompanyId(UserUtils.getAdminCompanyId());
		adminConfigService.addRole(adminRoleEntity);
		return CommonResult.ok("添加成功");
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
	@Permit("community:property:role")
	public CommonResult delRole(@RequestParam("id") List<Long> roleIds){
		adminConfigService.delRole(roleIds, UserUtils.getAdminCompanyId());
		return CommonResult.ok("删除成功");
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
	@Permit("community:property:role")
	public CommonResult updateRole(@RequestBody AdminRoleQO adminRoleQO){
		if(adminRoleQO.getId() == null){
			return CommonResult.error("缺少ID");
		}
		String id = UserUtils.getId();
		adminRoleQO.setCompanyId(UserUtils.getAdminCompanyId());
		adminConfigService.updateRole(adminRoleQO, Long.valueOf(id));
		return  CommonResult.ok("操作成功");
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
		baseQO.getQuery().setCompanyId(UserUtils.getAdminCompanyId());
		return CommonResult.ok(adminConfigService.queryPage(baseQO),"查询成功");
	}

	@PostMapping("/pageAll")
	@Permit("community:property:role:pageAll")
	public CommonResult queryPageAll(@RequestBody BaseQO<AdminRoleEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new AdminRoleEntity());
		}
		baseQO.getQuery().setCompanyId(UserUtils.getAdminCompanyId());
		return CommonResult.ok(adminConfigService.queryPageAll(baseQO),"查询成功");
	}

	@PostMapping("/selectAllRole")
	@Permit("community:property:role:selectAllRole")
	public CommonResult selectAllRole(){
		Long adminCommunityId = UserUtils.getAdminCommunityId();
	  List<AdminRoleEntity> list=adminRoleService.selectAllRole(adminCommunityId);


		return CommonResult.ok(list,"查询成功");
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
		return CommonResult.ok(adminConfigService.queryRoleDetail(roleId, UserUtils.getAdminCompanyId()), "查询成功!");
	}
}

