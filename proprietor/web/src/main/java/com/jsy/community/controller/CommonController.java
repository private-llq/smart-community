package com.jsy.community.controller;

import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("common")
@Api(tags = "公共控制器")
@RestController
public class CommonController {
	
	@ApiOperation("小区门牌级联查询")
	@GetMapping("community/door")
	public CommonResult door(@RequestParam Integer id, @RequestParam Integer level) {
		return null;
	}
}
