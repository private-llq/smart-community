package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentStaffService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentStaffEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
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
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentStaffService departmentStaffService;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.DepartmentStaffEntity>>
	 * @Author lihao
	 * @Description 测试环境
	 * @Date 2020/11/24 16:35
	 * @Param []
	 **/
	@ApiOperation("测试")
	@GetMapping("/listStaff")
	public CommonResult<List<DepartmentStaffEntity>> listStaff(){
		List<DepartmentStaffEntity> staffEntities = departmentStaffService.listStaff();
		return CommonResult.ok(staffEntities);
	}
}

