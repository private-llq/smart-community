package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.POIUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "部门联系人控制器")
@RestController
@RequestMapping("/staff")
@ApiJSYController
public class DepartmentStaffController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("查询所有员工信息")
	@PostMapping("/listDepartmentStaff")
	public CommonResult<PageInfo<DepartmentStaffEntity>> listDepartmentStaff(@ApiParam("departmentId") @RequestParam Long departmentId,
	                                                                         @RequestBody BaseQO<DepartmentStaffEntity> staffEntity) {
		PageInfo<DepartmentStaffEntity> page = departmentStaffService.listDepartmentStaff(departmentId, staffEntity);
		return CommonResult.ok(page);
	}
	
	@ApiOperation("添加员工")
	@PostMapping("/addDepartmentStaff")
	public CommonResult addDepartmentStaff(@RequestBody DepartmentStaffEntity staffEntity) {
		ValidatorUtils.validateEntity(staffEntity, DepartmentStaffEntity.addStaffValidate.class);
		departmentStaffService.addDepartmentStaff(staffEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改员工信息")
	@PutMapping("/updateDepartmentStaff")
	public CommonResult updateDepartmentStaff(@RequestBody DepartmentStaffEntity departmentStaffEntity) {
		ValidatorUtils.validateEntity(departmentStaffEntity, DepartmentStaffEntity.updateStaffValidate.class);
		return CommonResult.ok();
	}
	
	@ApiOperation("批量删除员工")
	@PostMapping("/deleteStaffByIds")
	public CommonResult deleteStaffByIds(@RequestBody Integer[] ids) {
		departmentStaffService.deleteStaffByIds(ids);
		return CommonResult.ok();
	}
	
	@ApiOperation("测试poi")
	@PostMapping("/poi")
	public CommonResult poi(@RequestParam("file") MultipartFile file) {
		try {
			List<String[]> strings = POIUtils.readExcel(file);
			for (String[] string : strings) {
				for (String s : string) {
					System.out.println(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return CommonResult.error("111");
		}
		return null;
	}
	
	
}

