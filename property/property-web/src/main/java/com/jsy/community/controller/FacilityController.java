package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-03-13
 */
@Api(tags = "设备控制器")
@RestController
@RequestMapping("/facility")
@ApiJSYController
public class FacilityController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IFacilityService facilityService;
	
	@ApiOperation("添加设备")
	@PostMapping("/addFacility")
	public CommonResult addFacility(@RequestBody FacilityEntity facilityEntity) {
		ValidatorUtils.validateEntity(facilityEntity, FacilityEntity.addFacilityValidate.class);
		facilityService.addFacility(facilityEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除设备")
	@GetMapping("/deleteFacility")
	public CommonResult deleteFacility(@RequestParam("id") Long id){
		facilityService.deleteFacility(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("分页查询设备")
	@PostMapping("/listFacility")
	public CommonResult listFacility(@RequestBody FacilityQO facilityQO) {
		List<FacilityEntity> list = facilityService.listFacility(facilityQO);
		return CommonResult.ok(list);
	}
}

