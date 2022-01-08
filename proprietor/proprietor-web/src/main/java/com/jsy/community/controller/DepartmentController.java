package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
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
// @ApiJSYController
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IDepartmentService departmentService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("通讯录")
	@GetMapping("/listDepartment")
	// @Permit("community:proprietor:department:listDepartment")
	public CommonResult<List<Map>> listDepartment(@ApiParam(value = "社区id") @RequestParam Long id) {
		List<DepartmentEntity> departmentList = departmentService.listDepartment(id);
		List<Map> strings = new ArrayList<>();
		for (DepartmentEntity departmentEntity : departmentList) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", departmentEntity.getId());
			map.put("department", departmentEntity.getDepartment());
			// 为什么要将其命名为nightImg呢  最开始需求说的是这个地方有两种部门图片样式，当初留了一手白色主题的。为了便于前端区分所以有nightImg与dayImg  结果现在又说没有2中图片样式了 所以导致这里命名是nightImg，前端又写的nightImg所以没有改 不然后端改了 前端也要改
			map.put("nightImg", departmentEntity.getImgUrl());
			// 部门可以最多有3个电话, 一对多关系, 没有建立相应的电话表   将电话直接写在部门表t_department的，以, 隔开
			String phone = departmentEntity.getPhone();
			if (!StringUtils.isEmpty(phone)) {
				String[] phoneArray = phone.split(",");
				List<String> phones = new ArrayList<>();
				for (String s : phoneArray) {
					String trim = s.trim();
					phones.add(trim);
				}
				map.put("phones", phones);
				strings.add(map);
			}
		}
		return CommonResult.ok(strings);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Deprecated // 过期不再使用
	@ApiOperation("根据部门查询联系方式")
	@GetMapping("/listStaffPhone")
	public CommonResult<List<DepartmentStaffEntity>> listStaffPhone(@ApiParam(value = "部门id")
	                                                                @RequestParam(value = "id") Long id) {
		List<DepartmentStaffEntity> staffEntityList = departmentStaffService.listStaffPhone(id);
		return CommonResult.ok(staffEntityList);
	}
	
	@Deprecated // 过期不再使用
	@ApiOperation("通讯录xx")
	@GetMapping("/listDepartmentTel")
	public CommonResult<List<DepartmentEntity>> listDepartmentTel(@ApiParam(value = "社区id")
	                                                              @RequestParam(required = false, defaultValue = "1", value = "id") Long id) {
		List<DepartmentEntity> departmentTels = departmentService.listDepartmentTel(id);
		return CommonResult.ok(departmentTels);
	}
	
}

