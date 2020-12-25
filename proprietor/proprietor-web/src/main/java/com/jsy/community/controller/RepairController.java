package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRepairService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.repair.RepairVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@Login(allowAnonymous = false)
@ApiJSYController
@RequestMapping("/repair")
public class RepairController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IRepairService repairService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private static final String BUCKETNAME = "repair-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	@ApiOperation("房屋报修查询")
	@GetMapping("/getRepair")
	public CommonResult getRepair() {
		String uid = UserUtils.getUserId();
		List<RepairEntity> list = repairService.getRepair(uid);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("房屋取消报修")
	@GetMapping("/cancelRepair")
	public CommonResult cancelRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id) {
		String uid = UserUtils.getUserId();
		repairService.cancelRepair(id, uid);
		return CommonResult.ok();
	}
	
	@ApiOperation("报修详情")
	@GetMapping("/repairDetails")
	public CommonResult repairDetails(@ApiParam(value = "房屋报修id") @RequestParam Long id) {
		String uid = UserUtils.getUserId();
		RepairVO repairVO = repairService.repairDetails(id, uid);
		return CommonResult.ok(repairVO);
	}
	
	@ApiOperation("报修内容图片上传")
	@PostMapping("/uploadRepairImg")
	public CommonResult uploadRepairImg(@RequestParam("file") MultipartFile[] files) {
		String[] filePaths = MinioUtils.uploadForBatch(files, BUCKETNAME);
		StringBuilder filePath = new StringBuilder();
		for (String s : filePaths) {
			redisTemplate.opsForSet().add("repair_img_part", s); // TODO 前端要注意调整 repairImg
			filePath.append(s);
			filePath.append(";");
		}
		return CommonResult.ok(filePath);
	}
	
	@ApiOperation("评价报修")
	@GetMapping("/appraiseRepair")
	public CommonResult appraiseRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id,
	                                   @ApiParam(value = "用户评价,100字以内") @RequestParam String appraise,
	                                   @ApiParam(value = "评价类型") @RequestParam Integer status) {
		String uid = UserUtils.getUserId();
		repairService.appraiseRepair(id, appraise, uid, status);
		return CommonResult.ok();
	}
	
	@ApiOperation("发起房屋报修")
	@PostMapping("/addRepair")
	public CommonResult addRepair(@RequestBody RepairEntity repairEntity) {
		String uid = UserUtils.getUserId();
		repairEntity.setUserId(uid);
		ValidatorUtils.validateEntity(repairEntity, RepairEntity.addRepairValidate.class);
		repairService.addRepair(repairEntity);
		return CommonResult.ok();
	}


//	@ApiOperation("完成报修")
//	@GetMapping("/completeRepair")
//	public CommonResult completeRepair(@ApiParam(value = "房屋报修id") @RequestParam Long id,
//	                                   @ApiParam(value = "业主id") @RequestParam Long userId) {
//		repairService.completeRepair(id, userId);
//		return CommonResult.ok();
//	}


//	@ApiOperation("删除评价")
//	@GetMapping("/deleteAppraise")
//	public CommonResult deleteAppraise(@ApiParam(value = "房屋报修id") @RequestParam Long id) {
//		repairService.deleteAppraise(id);
//		return CommonResult.ok();
//	}
	
	
}

