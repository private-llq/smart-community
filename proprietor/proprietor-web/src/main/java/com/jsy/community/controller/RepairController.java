package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRepairService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.repair.RepairVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @return
 * @Author lihao
 * @Description 房屋报修控制器
 * @Date 2020/12/8 11:31
 * @Param
 **/
@Api(tags = "房屋报修控制器")
@Slf4j
@RestController
@Login(allowAnonymous = true)
@ApiJSYController
@RequestMapping("/repair")
public class RepairController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IRepairService repairService;
	
	@ApiOperation("发起房屋报修")
	@PostMapping("/addRepair")
	public CommonResult addRepair(@RequestBody RepairEntity repairEntity) {
		repairService.addRepair(repairEntity);
		return CommonResult.ok();
	}
	
	@ApiOperation("房屋报修查询")
	@GetMapping("/getRepair")
	public CommonResult getRepair(@ApiParam(value = "业主id") @RequestParam Long uid) {
		List<RepairEntity> list = repairService.getRepair(uid);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("房屋取消报修")
	@GetMapping("/cancelRepair")
	public CommonResult cancelRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id,
	                                 @ApiParam(value = "业主id") @RequestParam Long userId) {
		repairService.cancelRepair(id, userId);
		return CommonResult.ok();
	}

	
	
	
	
	
	
	
	
	
	
	
//	@ApiOperation("完成报修")
//	@GetMapping("/completeRepair")
//	public CommonResult completeRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id,
//	                                   @ApiParam(value = "业主id") @RequestParam Long userId) {
//		repairService.completeRepair(id, userId);
//		return CommonResult.ok();
//	}






	
//	@ApiOperation("评价报修")
//	@GetMapping("/appraiseRepair")
//	public CommonResult appraiseRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id,
//	                                   @ApiParam(value = "用户评价") @RequestParam String appraise) {
//		repairService.appraiseRepair(id, appraise);
//		return CommonResult.ok();
//	}







//	@ApiOperation("删除评价")
//	@GetMapping("/deleteAppraise")
//	public CommonResult deleteAppraise(@ApiParam(value = "房屋报修id") @RequestParam Long id) {
//		repairService.deleteAppraise(id);
//		return CommonResult.ok();
//	}
	
	
	
	
	@ApiOperation("报修详情")
	@GetMapping("/repairDetails")
	public CommonResult repairDetails(@ApiParam(value = "房屋报修id") @RequestParam Long id,
	                                  @ApiParam(value = "业主id") @RequestParam Long userId) {
		RepairVO repairVO = repairService.repairDetails(id, userId);
		return CommonResult.ok(repairVO);
	}


//	@ApiOperation("报修内容图片上传")
//	@PostMapping("/uploadRepairImg")
//	public CommonResult uploadRepairImg(@RequestParam("file") MultipartFile file) {
//
//
//	}
}

