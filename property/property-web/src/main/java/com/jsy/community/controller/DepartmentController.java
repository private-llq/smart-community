package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.DepartmentQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.DepartmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 部门控制器
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
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentService departmentService;
	
	@ApiOperation("树形查询所有部门")
	@GetMapping("/listDepartment")
	public CommonResult<List<DepartmentVO>> listDepartment(@ApiParam("社区id") @RequestParam Long communityId) {
		List<DepartmentVO> departmentVOS = departmentService.listDepartment(communityId);
		return CommonResult.ok(departmentVOS);
	}
	
	@ApiOperation("新增部门")
	@PostMapping("/addDepartment")
	public CommonResult addDepartment(@RequestBody DepartmentQO departmentEntity) {
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.addDepartmentValidate.class);
		if (departmentEntity.getSort() > 99 || departmentEntity.getSort() < 0) {
			throw new PropertyException("你输入的排序序号不符要求,请重新输入!");
		}
		departmentService.addDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改部门")
	@PostMapping("/updateDepartment")
	public CommonResult updateDepartment(@RequestBody DepartmentQO departmentEntity) {
		ValidatorUtils.validateEntity(departmentEntity, DepartmentQO.updateDepartmentValidate.class);
		if (departmentEntity.getSort() > 99 || departmentEntity.getSort() < 0) {
			throw new PropertyException("你输入的排序序号不符要求,请重新输入!");
		}
		departmentService.updateDepartment(departmentEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除部门")
	@GetMapping("/deleteDepartment")
	public CommonResult deleteDepartment(Long departmentId, Long communityId) {
		departmentService.deleteDepartment(departmentId, communityId);
		return CommonResult.ok();
	}
	
}

