package com.jsy.community.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ling
 * @since 2020-11-18 14:48
 */
@RequestMapping("test")
@Api(tags = "测试")
@RestController
public class TestController {
	
	@GetMapping("/echo")
	@ApiOperation("测试")
	public void test() {
		System.out.println("测试");
	}
}
