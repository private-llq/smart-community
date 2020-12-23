package com.jsy.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.mapper.TestMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chq459799974
 * @description TODO
 * @since 2020-12-23 12:42
 **/
@RestController
@RequestMapping("test")
public class TestController {
	@Resource
	private TestMapper testMapper;
	@GetMapping("test")
	public CommonResult test(){
		return CommonResult.ok(testMapper.selectList(new QueryWrapper<BannerEntity>().select("*")));
	}
}
