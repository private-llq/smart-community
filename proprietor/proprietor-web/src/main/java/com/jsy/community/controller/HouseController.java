package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chq459799974
 * @description 楼宇房屋控制器
 * @since 2021-01-09 11:28
 **/
@RequestMapping("house")
@Api(tags = "楼宇房屋控制器")
@ApiJSYController
@RestController
public class HouseController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IHouseService houseService;
	
	/** 
	* @Description: 批量查询房屋信息
	 * @Param: [ids]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/9
	**/
	@ApiOperation("批量查询房屋信息")
	@PostMapping("getByIds")
	public CommonResult queryHouseByIdBatch(@RequestBody List<Long> ids){
		return CommonResult.ok(houseService.queryHouseByIdBatch(ids));
	}
	
}
