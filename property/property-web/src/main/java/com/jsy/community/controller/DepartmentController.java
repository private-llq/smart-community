package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.TreeCommunityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部门控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "社区通讯录(部门控制器)")
@RestController
@RequestMapping("/department")
@ApiJSYController
@Login
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentService departmentService;
	
	@ApiOperation("树形查询所有部门")
	@GetMapping("/listDepartment")
	public CommonResult listDepartment() {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		TreeCommunityVO treeCommunityVO = departmentService.listDepartment(communityId);
		return CommonResult.ok(treeCommunityVO);
	}
	
	@ApiOperation("新增部门")
	@PostMapping("/addDepartment")
	public CommonResult addDepartment(@RequestBody DepartmentQO departmentEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.addDepartmentValidate.class);
		departmentService.addDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询部门")
	@GetMapping("/getDepartmentById")
	public CommonResult getDepartmentById(@RequestParam Long departmentId) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		DepartmentEntity departmentEntity = departmentService.getDepartmentById(departmentId, communityId);
		return CommonResult.ok(departmentEntity);
	}
	
	@ApiOperation("修改部门")
	@PostMapping("/updateDepartment")
	public CommonResult updateDepartment(@RequestBody DepartmentQO departmentEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.updateDepartmentValidate.class);
		departmentService.updateDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除部门")
	@GetMapping("/deleteDepartment")
	public CommonResult deleteDepartment(@RequestParam Long departmentId) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentService.deleteDepartment(departmentId, communityId);
		return CommonResult.ok();
	}
}

