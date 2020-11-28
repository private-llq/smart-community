package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IDepartmentService;
import com.jsy.community.constant.Const;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-24
 */
@Api(tags = "通讯录控制器")
@RestController
@RequestMapping("/department")
@ApiJSYController
public class DepartmentController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IDepartmentService departmentService;
	
	
	
}

