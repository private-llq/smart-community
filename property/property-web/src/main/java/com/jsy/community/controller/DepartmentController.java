package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "部门控制器")
@RestController
@RequestMapping("/department")
@ApiJSYController
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentService departmentService;

	@ApiOperation("查询所有部门")
	@GetMapping("/listDepartment")
	public CommonResult<List<DepartmentEntity>> listDepartment(@ApiParam("社区id")
	                                                           @RequestParam Long communityId) {
		List<DepartmentEntity> departmentEntities = departmentService.listDepartment(communityId);
		return CommonResult.ok(departmentEntities);
	}
	
	@ApiOperation("新增部门")
	@PostMapping("/addDepartment")
	public CommonResult addDepartment(@RequestBody DepartmentEntity departmentEntity) {
		departmentService.addDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改部门")
	@PutMapping("/updateDepartment")
	public CommonResult updateDepartment(@RequestBody DepartmentEntity departmentEntity) {
		departmentService.updateDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除部门")
	@DeleteMapping("/deleteDepartment")
	public CommonResult deleteDepartment(Long id) {
		departmentService.deleteDepartment(id);
		return CommonResult.ok();
	}
	
	
}

