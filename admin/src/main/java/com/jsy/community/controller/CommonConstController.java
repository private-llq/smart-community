package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.ICommonConstService;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 公共常量表 前端控制器
 * </p>
 *
 * @author jsy
 * @since 2020-12-25
 */
@Api(tags = "公共常量控制器")
@Slf4j
@RestController
@RequestMapping("/const")
@ApiJSYController
public class CommonConstController {
	
	@Resource
	private ICommonConstService commonConstService;
	
	@LoginIgnore
	@ApiOperation("根据常量所属编号查询其所有常量")
	@PostMapping("/getConst")
	public CommonResult getConst(@ApiParam("常量所属编号") @RequestParam Integer constId,
	                             @RequestBody BaseQO<CommonConst> baseQO) {
		PageInfo<CommonConst> pageInfo = commonConstService.getConst(constId, baseQO);
		return CommonResult.ok(pageInfo);
	}
	
	@LoginIgnore
	@ApiOperation("添加常量")
	@PostMapping("/addConst")
	@businessLog(operation = "新增",content = "新增了【常量】")
	public CommonResult addConst(@RequestBody CommonConst commonConst) {
		long id = SnowFlake.nextId();
		commonConst.setId(id);
		commonConstService.addConst(commonConst);
		return CommonResult.ok();
	}
}

