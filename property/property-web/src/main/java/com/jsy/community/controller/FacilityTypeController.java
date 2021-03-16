package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IFacilityTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.hk.FacilityTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-03-12
 */
@Api(tags = "设备分类控制器")
@RestController
@RequestMapping("/facilityType")
@ApiJSYController
public class FacilityTypeController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IFacilityTypeService facilityTypeService;
	
	//完成
	@ApiOperation("添加设备分类")
	@PostMapping("/addFacilityType")
	public CommonResult addFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.addFacilityValidate.class);
		facilityTypeService.addFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	//完成
	@ApiOperation("修改设备分类")
	@PostMapping("/updateFacilityType")
	public CommonResult updateFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.updateFacilityValidate.class);
		facilityTypeService.updateFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	//完成(没处理设备分类下有设备的)
	@ApiOperation("删除设备分类")
	@GetMapping("/deleteFacilityType")
	public CommonResult deleteFacilityType(@RequestParam Long id,@RequestParam Long communityId) {
		facilityTypeService.deleteFacilityType(id,communityId);
		return CommonResult.ok();
	}
	
	//完成(关于设备数没有处理)
	@ApiOperation("树形结构查询设备分类")
	@GetMapping("/listFacilityType")
	public CommonResult<List<FacilityTypeVO>> listFacilityType(@ApiParam("社区id") @RequestParam Long communityId) {
		List<FacilityTypeVO> facilityTypeVOS = facilityTypeService.listFacilityType(communityId);
		return CommonResult.ok(facilityTypeVOS);
	}
	
	
}

