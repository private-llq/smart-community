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
			map.put("nightImg", departmentEntity.getImgUrl());  // 为什么要将其命名为nightImg呢  最开始需求说的是这个地方有两种部门图片样式，当初留了一手白色主题的。为了便于前端区分所以有nightImg与dayImg  结果现在又说没有2中图片样式了 所以导致这里命名是nightImg，前端又写的nightImg所以没有改 不然后端改了 前端也要改
			String phone = departmentEntity.getPhone();
			String[] phoneArray = phone.split(",");
			List<String> phones = new ArrayList<>();
			for (String s : phoneArray) {
				String trim = s.trim();
				phones.add(trim);
			}
			map.put("phones",phones);
			strings.add(map);
		}
		return CommonResult.ok(strings);
	}
	
	@Deprecated
	@ApiOperation("根据部门查询联系方式")
	@GetMapping("/listStaffPhone")
	public CommonResult<List<DepartmentStaffEntity>> listStaffPhone(@ApiParam(value = "部门id")
	                                                                @RequestParam(value = "id") Long id) {
		List<DepartmentStaffEntity> staffEntityList = departmentStaffService.listStaffPhone(id);
		return CommonResult.ok(staffEntityList);
	}
	
	@Deprecated
	@ApiOperation("通讯录")
	@GetMapping("/listDepartmentTel")
	public CommonResult<List<DepartmentEntity>> listDepartmentTel(@ApiParam(value = "社区id")
	                                              @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentEntity> departmentTels =departmentService.listDepartmentTel(id);
		return CommonResult.ok(departmentTels);
	}
	
}

