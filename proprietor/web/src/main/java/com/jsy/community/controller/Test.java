package com.jsy.community.controller;

import com.jsy.community.api.Demo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("test")
@RestController
@Api(tags = "测试接口")
public class Test {
	
	@DubboReference(version = "1.0", group = "test")
	private Demo demo;
	
	@ApiOperation("测试方法")
	@GetMapping("/demo")
	public CommonResult<Boolean> tests(@RequestParam(required = false) String name) {
		System.out.println(demo.sayHello());
		return CommonResult.error(100);
	}
}
