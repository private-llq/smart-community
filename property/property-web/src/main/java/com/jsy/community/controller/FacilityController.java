package com.jsy.community.controller;


import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICommonConstService;
import com.jsy.community.api.IFacilityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.hk.FacilityEntity;
import com.jsy.community.entity.hk.FacilitySyncRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.hk.FacilityQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author chq459799974
 * @description 设备相关Controller
 * @since 2021-06-12 10:29
 **/
@Api(tags = "设备控制器")
@RestController
@RequestMapping("/facility")
// @ApiJSYController
public class FacilityController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IFacilityService facilityService;
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICommonConstService commonConstService;

	/**
	 * 在添加设备的时候，让用户选择该摄像机是做什么功能的。
	 **/
	@ApiOperation("获取设备作用")
	@GetMapping("/getFacilityTypeEffect")
	@Permit("community:property:facility:getFacilityTypeEffect")
	public CommonResult getFacilityTypeEffect() {
		List<CommonConst> constList = commonConstService.getFacilityTypeEffect();
		return CommonResult.ok(constList);
	}
	
	@ApiOperation("分页查询设备")
	@PostMapping("/listFacility")
	@Permit("community:property:facility:listFacility")
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
	@Permit("community:property:facility:getCount")
	// TODO: 2021/5/15 这个功能好像前端没有在强制刷新后调用这个接口  应该让他调的 后面让前端做下
	public CommonResult getCount(@ApiParam("设备分类Id") @RequestParam("typeId") Long typeId) {
		Map<String, Integer> map = facilityService.getCount(typeId,UserUtils.getAdminCommunityId());
		return CommonResult.ok(map);
	}
	
	@ApiOperation("添加设备")
	@PostMapping("/addFacility")
	@businessLog(operation = "新增",content = "新增了【设备】")
	@Permit("community:property:facility:addFacility")
	public CommonResult addFacility(@RequestBody FacilityEntity facilityEntity) {
		AdminInfoVo adminInfoVo = UserUtils.getAdminInfo();
		facilityEntity.setCommunityId(adminInfoVo.getCommunityId());
		facilityEntity.setPersonId(adminInfoVo.getUid());
		facilityEntity.setCreatePerson(adminInfoVo.getRealName());
		ValidatorUtils.validateEntity(facilityEntity, FacilityEntity.addFacilityValidate.class);
		facilityService.addFacility(facilityEntity);
		return CommonResult.ok("操作成功");
	}
	
	@ApiOperation("编辑设备")
	@PostMapping("/updateFacility")
	@businessLog(operation = "编辑",content = "更新了【设备】")
	@Permit("community:property:facility:updateFacility")
	public CommonResult updateFacility(@RequestBody FacilityEntity facilityEntity) {
		// By:LH: 编辑的时候不能更改该摄像头的作用哈  因为摄像头的作用需要更改摄像头后台  开启相应的功能 比如你要开启人脸比对，摄像机应该去后台选择人脸比对模式
		// By:LH: 一个摄像机不能同时做车牌抓拍与人脸比对功能2个事
		facilityEntity.setCommunityId(UserUtils.getAdminCommunityId());
		facilityEntity.setFacilityEffectId(null); //设备作用根据摄像头后台[配置-系统-系统设置-智能模式切换] ，不允许直接修改设备作用
		facilityService.updateFacility(facilityEntity);
		return CommonResult.ok("操作成功");
	}
	
	@ApiOperation("删除设备")
	@GetMapping("/deleteFacility")
	@businessLog(operation = "删除",content = "删除了【设备】")
	@Permit("community:property:facility:deleteFacility")
	public CommonResult deleteFacility(@RequestParam("id") Long id) {
		facilityService.deleteFacility(id,UserUtils.getAdminCommunityId());
		return CommonResult.ok("操作成功");
	}
	
	/**
	 * 刷新设备： 强制刷新 获取当前页设备最新在线状态
	 **/
	@ApiOperation("刷新设备")
	@GetMapping("/flushFacility")
	@Permit("community:property:facility:flushFacility")
	public CommonResult flushFacility(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("facilityTypeId") String facilityTypeId) {
		if(page == null || page == 0){
			page = 1;
		}
		if(page == null || page == 0){
			page = 10;
		}
		facilityService.flushFacility(page,size,facilityTypeId,UserUtils.getAdminCommunityId());
		return CommonResult.ok("操作成功");
	}
	
	/**
	 * 同步数据：指把数据库里面最新的数据情况下发到摄像机上面
	 * 情况1：新买了一个摄像机，它里面没有人脸，那么此时就需要（同步数据）批量导入数据进去。
	 * 情况2：数据库里面今天多了几个新业主信息，此时摄像机还没有添加这些业主的信息，那么此时就需要（同步数据）批量导入数据进去。
	 *      PS：情况2 其实每次来点这个同步按钮是没必要的，在业主认证时用rabbitMQ监听，只要添加了新业主，将其信息发到rabbitMQ，然后就及时异步消费实现将新增的业主添加到人脸库
	 **/
	@ApiOperation("根据设备id同步数据")
	@GetMapping("/connectData")
	@Permit("community:property:facility:connectData")
	public CommonResult connectData(@RequestParam("id") Long id) {
		facilityService.syncFaceData(id, UserUtils.getAdminCommunityId());
		return CommonResult.ok();
	}
	
	@ApiOperation("分页查询设备数据同步记录")
	@PostMapping("/connectData/record")
	@Permit("community:property:facility:connectData:record")
	public CommonResult queryDataRecord(@RequestBody BaseQO<FacilitySyncRecordEntity> baseQO) {
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new FacilitySyncRecordEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(facilityService.querySyncRecordPage(baseQO),"查询成功");
	}
	
	@ApiOperation("根据数据同步状态统计设备数")
	@GetMapping("/count")
	@Permit("community:property:facility:count")
	public CommonResult queryDataRecord(@RequestParam Integer isConnectData) {
		return CommonResult.ok(facilityService.countBySyncStatus(UserUtils.getAdminCommunityId(),isConnectData),"查询成功");
	}
}

