package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.DepartmentEntity;
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
 * @author jsy
 * @since 2020-11-24
 */
@Api(tags = "通讯部门控制器")
@RestController
@RequestMapping("/department")
@ApiJSYController
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentService departmentService;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.entity.DepartmentEntity>>
	 * @Author lihao
	 * @Description 测试环境
	 * @Date 2020/11/24 16:13
	 * @Param []
	 **/
	@ApiOperation("测试")
	@GetMapping("/listDepartment")
	public CommonResult<List<DepartmentEntity>> listDepartment(){
		List<DepartmentEntity> departments = departmentService.listDepartment();
		return CommonResult.ok(departments);
	}
}

