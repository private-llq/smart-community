package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IFacilityTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityTypeEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.hk.FacilityTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Login
public class FacilityTypeController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IFacilityTypeService facilityTypeService;

	@ApiOperation("添加设备分类树")
	@PostMapping("/addFacilityType")
	@businessLog(operation = "新增",content = "新增了【设备分类】")
	public CommonResult addFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		facilityTypeEntity.setId(SnowFlake.nextId());
		facilityTypeEntity.setCommunityId(UserUtils.getAdminCommunityId());
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.addFacilityTypeValidate.class);
		facilityTypeService.addFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询设备分类")
	@GetMapping("/getFacilityType")
	public CommonResult getFacilityType(@RequestParam Long id) {
		FacilityTypeEntity typeEntity = facilityTypeService.getFacilityType(id,UserUtils.getAdminCommunityId());
		return CommonResult.ok(typeEntity);
	}
	
	@ApiOperation("修改设备分类")
	@PostMapping("/updateFacilityType")
	@businessLog(operation = "编辑",content = "更新了【设备分类】")
	public CommonResult updateFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		if (facilityTypeEntity.getId() == null) {
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"请选择要修改的设备分类");
		}
		facilityTypeEntity.setCommunityId(UserUtils.getAdminCommunityId());
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.updateFacilityTypeValidate.class);
		facilityTypeService.updateFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除设备分类")
	@GetMapping("/deleteFacilityType")
	@businessLog(operation = "删除",content = "删除了【设备分类】")
	public CommonResult deleteFacilityType(@RequestParam Long id) {
		facilityTypeService.deleteFacilityType(id,UserUtils.getAdminCommunityId());
		return CommonResult.ok();
	}
	
	@ApiOperation("树形结构查询设备分类")
	@GetMapping("/listFacilityType")
	public CommonResult<List<FacilityTypeVO>> listFacilityType() {
		List<FacilityTypeVO> facilityTypeVOS = facilityTypeService.listFacilityType(UserUtils.getAdminCommunityId());
		return CommonResult.ok(facilityTypeVOS);
	}
	
}

