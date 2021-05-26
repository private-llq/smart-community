package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IFacilityTypeService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.hk.FacilityTypeEntity;
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
	
	@DubboReference(version = Const.version, group = Const.group_facility, check = false)
	private IFacilityTypeService facilityTypeService;

	@ApiOperation("添加设备分类树")
	@PostMapping("/addFacilityType")
	public CommonResult addFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		facilityTypeEntity.setId(SnowFlake.nextId());
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityTypeEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.addFacilityTypeValidate.class);
		facilityTypeService.addFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据id查询设备分类")
	@GetMapping("/getFacilityType")
	public CommonResult getFacilityType(@RequestParam Long id) {
		FacilityTypeEntity typeEntity = facilityTypeService.getFacilityType(id);
		return CommonResult.ok(typeEntity);
	}
	
	@ApiOperation("修改设备分类")
	@PostMapping("/updateFacilityType")
	public CommonResult updateFacilityType(@RequestBody FacilityTypeEntity facilityTypeEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityTypeEntity.setCommunityId(communityId);
		ValidatorUtils.validateEntity(facilityTypeEntity, FacilityTypeEntity.updateFacilityTypeValidate.class);
		facilityTypeService.updateFacilityType(facilityTypeEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除设备分类")
	@GetMapping("/deleteFacilityType")
	public CommonResult deleteFacilityType(@RequestParam Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityTypeService.deleteFacilityType(id,communityId);
		return CommonResult.ok();
	}
	
	@ApiOperation("树形结构查询设备分类")
	@GetMapping("/listFacilityType")
	public CommonResult<List<FacilityTypeVO>> listFacilityType() {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		List<FacilityTypeVO> facilityTypeVOS = facilityTypeService.listFacilityType(communityId);
		return CommonResult.ok(facilityTypeVOS);
	}
	
}

