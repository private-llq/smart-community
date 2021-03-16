package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.qo.DepartmentStaffQO;
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
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "部门员工控制器")
@RestController
@ApiJSYController
@RequestMapping("/staff")
public class DepartmentStaffController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	@ApiOperation("查询所有员工信息")
	@GetMapping("/listDepartmentStaff")
	public CommonResult<PageInfo<DepartmentStaffEntity>> listDepartmentStaff(@ApiParam("部门id") @RequestParam Long departmentId,
	                                                                         @RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "10") Long size) {
		PageInfo<DepartmentStaffEntity> pageInfo = departmentStaffService.listDepartmentStaff(departmentId, page, size);
		return CommonResult.ok(pageInfo);
	}
	
	@ApiOperation("根据id查询员工信息")
	@GetMapping("/getDepartmentStaffById")
	public CommonResult getDepartmentStaffById(@ApiParam("员工id") Long id) {
		DepartmentStaffEntity staffEntity = departmentStaffService.getDepartmentStaffById(id);
		return CommonResult.ok(staffEntity);
	}
	
	@ApiOperation("添加员工")
	@PostMapping("/addDepartmentStaff")
	public CommonResult addDepartmentStaff(@RequestBody DepartmentStaffQO staffEntity) {
		ValidatorUtils.validateEntity(staffEntity, DepartmentStaffQO.addStaffValidate.class);
		departmentStaffService.addDepartmentStaff(staffEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("修改员工信息")
	@PostMapping("/updateDepartmentStaff")
	public CommonResult updateDepartmentStaff(@RequestBody DepartmentStaffQO departmentStaffEntity) {
		ValidatorUtils.validateEntity(departmentStaffEntity, DepartmentStaffQO.updateStaffValidate.class);
		departmentStaffService.updateDepartmentStaff(departmentStaffEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除员工")
	@GetMapping("/deleteDepartmentStaff")
	public CommonResult deleteStaffByIds(@ApiParam("员工id") Long id, @ApiParam("社区id") Long communityId) {
		departmentStaffService.deleteStaffByIds(id, communityId);
		return CommonResult.ok();
	}
	
	@ApiOperation("通过Excel添加通讯录")
	@PostMapping("/addLinkByExcel")
	public CommonResult addLinkByExcel(@RequestParam("file") MultipartFile file) {
		try {
			// 获取Excel中的数据，每一行数据封装成一个String[]，将一个工作簿里面的每行数据封装成一个List<String[]>
			List<String[]> strings = POIUtils.readExcel(file);
			Map<String, Object> map = departmentStaffService.addLinkByExcel(strings);
			return CommonResult.ok(map);
		} catch (IOException e) {
			e.printStackTrace();
			return CommonResult.error("添加失败,请联系管理员");
		}
	}
	
	
}

