package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @return
 * @Author lihao
 * @Description 社区通讯录
 * @Date 2020/11/27 6:42
 * @Param
 **/
@Api(tags = "社区通讯录控制器")
@RestController
@RequestMapping("/department")
@ApiJSYController
@Login(allowAnonymous = true)
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentService departmentService;
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("查询所有部门信息")
	@GetMapping("/listDepartment")
	public CommonResult<List<DepartmentEntity>> listDepartment(@ApiParam(value = "社区id")
	                                                           @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentEntity> departmentList = departmentService.listDepartment(id);
		return CommonResult.ok(departmentList);
	}
	
	@ApiOperation("根据部门查询联系方式")
	@GetMapping("/listStaffPhone")
	public CommonResult<List<DepartmentStaffEntity>> listStaffPhone(@ApiParam(value = "部门id")
	                                                                @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentStaffEntity> staffEntityList = departmentStaffService.listStaffPhone(id);
		return CommonResult.ok(staffEntityList);
	}
	
	
}

