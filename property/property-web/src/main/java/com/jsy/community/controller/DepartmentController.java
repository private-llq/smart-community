package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.TreeCommunityVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 社区通讯录(部门控制器)
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "社区通讯录(部门控制器)")
@RestController
@RequestMapping("/department")
@ApiJSYController
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentService departmentService;
	
	@ApiOperation("树形查询所有部门")
	@GetMapping("/listDepartment")
	@Permit("community:property:department:listDepartment")
	public CommonResult listDepartment() {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		TreeCommunityVO treeCommunityVO = departmentService.listDepartment(communityId);
		return CommonResult.ok(treeCommunityVO);
	}
	
	@ApiOperation("新增部门")
	@PostMapping("/addDepartment")
	@businessLog(operation = "新增",content = "新增了【社区部门】")
	@Permit("community:property:department:addDepartment")
	public CommonResult addDepartment(@RequestBody DepartmentQO departmentEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentEntity.setCommunityId(communityId);
		departmentEntity.setId(SnowFlake.nextId());
		
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.addDepartmentValidate.class);
		departmentService.addDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询部门")
	@GetMapping("/getDepartmentById")
	@Permit("community:property:department:getDepartmentById")
	public CommonResult getDepartmentById(@RequestParam Long departmentId) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		DepartmentEntity departmentEntity = departmentService.getDepartmentById(departmentId, communityId);
		return CommonResult.ok(departmentEntity);
	}
	
	@ApiOperation("修改部门")
	@PostMapping("/updateDepartment")
	@businessLog(operation = "编辑",content = "更新了【社区部门】")
	@Permit("community:property:department:updateDepartment")
	public CommonResult updateDepartment(@RequestBody DepartmentQO departmentEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.updateDepartmentValidate.class);
		departmentService.updateDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除部门")
	@GetMapping("/deleteDepartment")
	@businessLog(operation = "删除",content = "删除了【社区部门】")
	@Permit("community:property:department:deleteDepartment")
	public CommonResult deleteDepartment(@RequestParam Long departmentId) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		departmentService.deleteDepartment(departmentId, communityId);
		return CommonResult.ok();
	}
}

