package com.jsy.community.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.HouseQO;
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
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IHouseService iHouseService;
	
	// 查询子级，后期若要封装层级，HouseQO加查询类型
	@ApiOperation("【楼栋】查询子级楼栋")
	@PostMapping("page/sub")
	public CommonResult<Page<HouseEntity>> queryHousePage(@RequestBody BaseQO<HouseQO> baseQO){
		Page<HouseEntity> subHousePage = iHouseService.queryHousePage(baseQO);
		return CommonResult.ok(subHousePage);
	}
	
	@ApiOperation("【楼栋】新增楼栋信息")
	@PostMapping("")
	public CommonResult addHouse(@RequestBody HouseEntity houseEntity){
		ValidatorUtils.validateEntity(houseEntity, HouseEntity.addHouseValidatedGroup.class);
		boolean result = iHouseService.addHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"新增楼栋信息失败");
	}
	
	@ApiOperation("【楼栋】修改楼栋信息")
	@PutMapping("")
	public CommonResult updateHouse(@RequestBody HouseEntity houseEntity){
		ValidatorUtils.validateEntity(houseEntity, HouseEntity.updateHouseValidatedGroup.class);
		boolean result = iHouseService.updateHouse(houseEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改楼栋信息失败");
	}
	
	@ApiOperation("【楼栋】删除楼栋信息")
	@DeleteMapping("")
	public CommonResult deleteHouse(@RequestParam("id") Long id){
		boolean result = iHouseService.deleteHouse(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"删除楼栋信息失败");
	}
	
}

