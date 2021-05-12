package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommonConstService;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
@Login
public class FacilityController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IFacilityService facilityService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICommonConstService commonConstService;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@ApiOperation("获取设备作用")
	@GetMapping("/getFacilityTypeEffect")
	public CommonResult getFacilityTypeEffect() {
		List<CommonConst> constList = commonConstService.getFacilityTypeEffect();
		return CommonResult.ok(constList);
	}

	@ApiOperation("获取设备作用")
	@GetMapping("/get")
	public CommonResult get() {
		rabbitTemplate.convertAndSend("exchange_camera_face","queue.camera.face","曹尼玛");
		return CommonResult.ok();
	}
	
	@ApiOperation("添加设备")
	@PostMapping("/addFacility")
	public CommonResult addFacility(@RequestBody FacilityEntity facilityEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityEntity.setCommunityId(communityId);
		facilityEntity.setId(SnowFlake.nextId());
		
		String userId = UserUtils.getAdminUserInfo().getUid();
		String realName = UserUtils.getAdminUserInfo().getRealName();
		facilityEntity.setPersonId(userId);
		facilityEntity.setCreatePerson(realName);
		
		ValidatorUtils.validateEntity(facilityEntity, FacilityEntity.addFacilityValidate.class);
		facilityService.addFacility(facilityEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("分页查询设备")
	@PostMapping("/listFacility")
	public CommonResult listFacility(@RequestBody BaseQO<FacilityQO> facilityQO) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		
		if (facilityQO.getQuery() == null) {
			facilityQO.setQuery(new FacilityQO());
		}
		
		facilityQO.getQuery().setCommunityId(communityId);
		PageInfo<FacilityEntity> pageInfo = facilityService.listFacility(facilityQO);
		return CommonResult.ok(pageInfo);
	}
	
	@ApiOperation("获取设备在线离线数")
	@GetMapping("/getCount")
	public CommonResult getCount(@ApiParam("设备分类Id") @RequestParam("typeId") Long typeId) {
		Map<String, Integer> map = facilityService.getCount(typeId);
		return CommonResult.ok(map);
	}
	
	@ApiOperation("编辑设备")
	@PostMapping("/updateFacility")
	// TODO: 2021/4/23 注意，更新设备状态后  如果定时任务执行了会又根据定时任务当时获取的状态来
	// TODO: 2021/4/23 编辑的时候先不考虑让用户可以更改该摄像头的作用
	public CommonResult updateFacility(@RequestBody FacilityEntity facilityEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		
		facilityEntity.setCommunityId(communityId);
		facilityService.updateFacility(facilityEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除设备")
	@GetMapping("/deleteFacility")
	public CommonResult deleteFacility(@RequestParam("id") Long id) {
		facilityService.deleteFacility(id);
		return CommonResult.ok();
	}
	
	@ApiOperation("刷新设备")
	@GetMapping("/flushFacility")
	public CommonResult flushFacility(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("facilityTypeId") String facilityTypeId) {
		facilityService.flushFacility(page, size, facilityTypeId);
		return CommonResult.ok();
	}
	
	@ApiOperation("根据设备id同步数据")
	@GetMapping("/connectData")
	public CommonResult connectData(@RequestParam("id") Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityService.connectData(id, communityId);
		return CommonResult.ok();
	}
}

