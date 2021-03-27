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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IDepartmentService departmentService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("查询所有部门信息")
	@GetMapping("/listDepartment")
	public CommonResult<List<Map>> listDepartment(@ApiParam(value = "社区id")
	                                              @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentEntity> departmentList = departmentService.listDepartment(id);
		List<Map> strings = new ArrayList<>();
		for (DepartmentEntity departmentEntity : departmentList) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", departmentEntity.getId());
			map.put("department", departmentEntity.getDepartment());
			map.put("nightImg", departmentEntity.getImgUrl());
			strings.add(map);
		}
		return CommonResult.ok(strings);
	}
	
	@ApiOperation("根据部门查询联系方式")
	@GetMapping("/listStaffPhone")
	public CommonResult<List<DepartmentStaffEntity>> listStaffPhone(@ApiParam(value = "部门id")
	                                                                @RequestParam(value = "id") Long id) {
		List<DepartmentStaffEntity> staffEntityList = departmentStaffService.listStaffPhone(id);
		return CommonResult.ok(staffEntityList);
	}
	
	@ApiOperation("通讯录")
	@GetMapping("/listDepartmentTel")
	public CommonResult<List<DepartmentEntity>> listDepartmentTel(@ApiParam(value = "社区id")
	                                              @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentEntity> departmentTels =departmentService.listDepartmentTel(id);
		return CommonResult.ok(departmentTels);
	}
	
}

