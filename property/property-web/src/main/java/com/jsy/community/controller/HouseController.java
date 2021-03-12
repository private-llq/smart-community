package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 社区楼栋 前端控制器
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-20
 */
@Api(tags = "楼栋控制器")
@RestController
@RequestMapping("house")
@ApiJSYController
public class HouseController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IHouseService houseService;
	
	// 查询子级，后期若要封装层级，HouseQO加查询类型
//	@ApiOperation("【楼栋】查询子级楼栋")
//	@PostMapping("page/sub")
//	public CommonResult<PageInfo<HouseEntity>> queryHousePage(@RequestBody BaseQO<HouseQO> baseQO){
//		return CommonResult.ok(iHouseService.queryHousePage(baseQO));
//	}

//	@ApiOperation("【楼栋】新增楼栋信息")
//	@PostMapping("")
//	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
//		ValidatorUtils.validateEntity(houseEntity, HouseEntity.addHouseValidatedGroup.class);
//		boolean result = iHouseService.addHouse(houseEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼栋信息失败");
//	}
	
	//TODO 修改入参QO 关联修改下级
//	@ApiOperation("【楼栋】修改楼栋信息")
//	@PutMapping("")
//	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
//		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
//		boolean result = iHouseService.updateHouse(houseEntity);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼栋信息失败");
//	}
	
//	@ApiOperation("【楼栋】删除楼栋信息")
//	@DeleteMapping("")
//	public CommonResult deleteHouse(@RequestParam("id") Long id){
//		boolean result = iHouseService.deleteHouse(id);
//		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除楼栋信息失败");
//	}
	
	// ============================================ 物业端产品原型确定后新加的 开始  ===========================================================
	
	@ApiOperation("【楼宇房屋】新增楼栋、单元、房屋")
	@PostMapping("")
	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
		if(houseEntity.getType() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少类型参数");
		}
		if(BusinessConst.BUILDING_TYPE_DOOR == houseEntity.getType()){
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addRoomValidatedGroup.class);
		}else{
			ValidatorUtils.validateEntity(houseEntity,HouseEntity.addHouseValidatedGroup.class);
		}
		boolean result = houseService.addHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼栋信息失败");
	}
	
	@ApiOperation("【楼宇房屋】修改")
	@PutMapping("")
	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
		boolean result = houseService.updateHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼栋信息失败");
	}
	
	@ApiOperation("【楼宇房屋】条件查询")
	@PostMapping("query")
	public CommonResult<PageInfo<HouseEntity>> queryHouse(@RequestBody BaseQO<HouseQO> baseQO){
		HouseQO query = baseQO.getQuery();
		if(query == null || query.getType() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少查询类型");
		}
		if(query.getCommunityId() == null){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"缺少社区ID");
		}
		if(BusinessConst.BUILDING_TYPE_BUILDING == query.getType()){
			query.setBuilding(query.getName());
		}else if(BusinessConst.BUILDING_TYPE_UNIT == query.getType()){
			query.setUnit(query.getName());
		}else if(BusinessConst.BUILDING_TYPE_DOOR == query.getType()){
			query.setDoor(query.getName());
		}else{
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"非法查询类型");
		}
		return CommonResult.ok(houseService.queryHouse(baseQO));
	}
	// ============================================ 物业端产品原型确定后新加的 结束  ===========================================================
	
}

