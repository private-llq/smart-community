package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IRepairTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairTypeEntity;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 报修类别 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-12-25
 */
@Api(tags = "报修类别控制器")
@Slf4j
// @ApiJSYController
@RestController
@RequestMapping("/repairType")
@Deprecated
public class RepairTypeController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IRepairTypeService repairTypeService;
	
	@ApiOperation("报修类别查询")
	@GetMapping("/getType")
	public CommonResult getType() {
		List<RepairTypeEntity> list = repairTypeService.getType();
		return CommonResult.ok(list);
	}
}

