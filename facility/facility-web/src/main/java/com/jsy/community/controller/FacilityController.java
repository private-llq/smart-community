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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备控制器
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
	
	@DubboReference(version = Const.version, group = Const.group_facility, check = false)
	private IFacilityService facilityService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICommonConstService commonConstService;

	/**
	 * 在添加设备的时候，让用户选择该摄像机是做什么功能的。
	 **/
	@ApiOperation("获取设备作用")
	@GetMapping("/getFacilityTypeEffect")
	public CommonResult getFacilityTypeEffect() {
		List<CommonConst> constList = commonConstService.getFacilityTypeEffect();
		return CommonResult.ok(constList);
	}
	
	/**
	 * 添加设备功能：  1. 保存设备基本信息   2. 根据基本信息(账号密码...)开启设备相应功能   3. 保存设备状态信息
	 **/
	@ApiOperation("添加设备")
	@PostMapping("/addFacility")
	public CommonResult addFacility(@RequestBody FacilityEntity facilityEntity) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityEntity.setCommunityId(communityId);
		
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
	public CommonResult listFacility(@RequestBody BaseQO<FacilityQO> baseQO) {
		if (baseQO.getQuery() == null) {
			baseQO.setQuery(new FacilityQO());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		PageInfo<FacilityEntity> pageInfo = facilityService.listFacility(baseQO);
		return CommonResult.ok(pageInfo);
	}
	
	@ApiOperation("获取设备在线离线数")
	@GetMapping("/getCount")
	// TODO: 2021/5/15 这个功能好像前端没有在强制刷新后调用这个接口  应该让他调的 后面让前端做下
	public CommonResult getCount(@ApiParam("设备分类Id") @RequestParam("typeId") Long typeId) {
		Map<String, Integer> map = facilityService.getCount(typeId,UserUtils.getAdminCommunityId());
		return CommonResult.ok(map);
	}
	
	@ApiOperation("编辑设备")
	@PostMapping("/updateFacility")
	// TODO: 2021/4/23 编辑的时候不能更改该摄像头的作用哈  因为摄像头的作用需要更改摄像头后台  开启相应的功能 比如你要开启人脸比对，摄像机应该去后台选择人脸比对模式
	// TODO: 2021/4/23 一个摄像机不能同时做车牌抓拍与人脸比对功能2个事
	public CommonResult updateFacility(@RequestBody FacilityEntity facilityEntity) {
		facilityEntity.setCommunityId(UserUtils.getAdminCommunityId());
		facilityEntity.setFacilityEffectId(null);
		facilityService.updateFacility(facilityEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("删除设备")
	@GetMapping("/deleteFacility")
	public CommonResult deleteFacility(@RequestParam("id") Long id) {
		facilityService.deleteFacility(id,UserUtils.getAdminCommunityId());
		return CommonResult.ok();
	}
	
	/**
	 * 刷新设备： 强制刷新 获取当前页设备最新在线状态
	 **/
	@ApiOperation("刷新设备")
	@GetMapping("/flushFacility")
	public CommonResult flushFacility(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("facilityTypeId") String facilityTypeId) {
		facilityService.flushFacility(page, size, facilityTypeId);
		return CommonResult.ok();
	}
	
	/**
	 * 同步数据：指把数据库里面最新的数据情况下发到摄像机上面
	 * 情况1：新买了一个摄像机，它里面没有人脸，那么此时就需要（同步数据）批量导入数据进去。
	 * 情况2：数据库里面今天多了几个新业主信息，此时摄像机还没有添加这些业主的信息，那么此时就需要（同步数据）批量导入数据进去。
	 *      PS：情况2 其实每次来点这个同步按钮是没必要的，在业主认证时用rabbitMQ监听，只要添加了新业主，将其信息发到rabbitMQ，然后就及时异步消费实现将新增的业主添加到人脸库
	 **/
	@ApiOperation("根据设备id同步数据")
	@GetMapping("/connectData")
	public CommonResult connectData(@RequestParam("id") Long id) {
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		facilityService.connectData(id, communityId);
		return CommonResult.ok();
	}
}

