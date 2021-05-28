package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.UploadImg;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRepairService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.UploadBucketConst;
import com.jsy.community.constant.UploadRedisConst;
import com.jsy.community.entity.CommonConst;
import com.jsy.community.entity.RepairEntity;
import com.jsy.community.qo.proprietor.RepairCommentQO;
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
import org.springframework.util.StringUtils;
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
@Login
@ApiJSYController
@RequestMapping("/repair")
public class RepairController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IRepairService repairService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	/**
	 * null 全部   0 待处理    1 处理中   2 已处理    3 已驳回
	 **/
	@ApiOperation("房屋报修查询")
	@GetMapping("/getRepair")
	public CommonResult getRepair(@ApiParam(value = "订单状态") @RequestParam(required = false) Integer status) {
		String uid = UserUtils.getUserId();
		List<RepairEntity> list = repairService.getRepair(uid, status);
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
	
	@ApiOperation("报修事项查询")
	@GetMapping("/getRepairType")
	@Login(allowAnonymous = true)
	public CommonResult getRepairType(@ApiParam(value = "报修类别") @RequestParam int repairType) {
		List<CommonConst> list = repairService.getRepairType(repairType);
		return CommonResult.ok(list);
	}
	
	@ApiOperation("报修内容图片上传")
	@PostMapping("/uploadRepairImg")
	@UploadImg(bucketName = UploadBucketConst.REPAIR_BUCKET, redisKeyName = UploadRedisConst.REPAIR_IMG_PART)
	public CommonResult uploadRepairImg(@RequestParam("file") MultipartFile[] files, CommonResult commonResult) {
		if (files.length > 3) {
			throw new ProprietorException("只能上传3张图片");
		}
		String[] filePaths = (String[]) commonResult.getData();
		StringBuilder filePath = new StringBuilder();
		for (String s : filePaths) {
			if (!StringUtils.isEmpty(s)) {
				redisTemplate.opsForSet().add("repair_img_part", s);
				filePath.append(s);
				filePath.append(";");
			}
		}
		return CommonResult.ok(filePath);
	}
	
	@ApiOperation("评价报修")
	@PostMapping("/appraiseRepair")
	public CommonResult appraiseRepair(@RequestBody RepairCommentQO repairCommentQO) {
		String uid = UserUtils.getUserId();
		repairCommentQO.setUid(uid);
		repairService.appraiseRepair(repairCommentQO);
		
		String filePath = repairCommentQO.getFilePath();
		String[] split = filePath.split(";");
		for (String path : split) {
			redisTemplate.opsForSet().add(UploadRedisConst.REPAIR_COMMENT_IMG_ALL, path); // 存入redis
		}
		return CommonResult.ok();
	}
	
	@ApiOperation("评价图片上传")
	@PostMapping("/uploadCommentImg")
	@UploadImg(bucketName = UploadBucketConst.REPAIR_BUCKET, redisKeyName = UploadRedisConst.REPAIR_COMMENT_IMG_PART)
	public CommonResult uploadCommentImg(@RequestParam("file") MultipartFile[] files, CommonResult commonResult) {
		if (files.length > 3) {
			throw new ProprietorException("只能上传3张图片");
		}
		String[] filePaths = (String[]) commonResult.getData();
		StringBuilder filePath = new StringBuilder();
		for (String s : filePaths) {
			if (!StringUtils.isEmpty(s)) {
				redisTemplate.opsForSet().add(UploadRedisConst.REPAIR_COMMENT_IMG_PART, s);
				filePath.append(s);
				filePath.append(";");
			}
		}
		return CommonResult.ok(filePath);
	}
	
	@ApiOperation("发起房屋报修")
	@PostMapping("/addRepair")
	public CommonResult addRepair(@RequestBody RepairEntity repairEntity) {
		String uid = UserUtils.getUserId();
		repairEntity.setUserId(uid);
		ValidatorUtils.validateEntity(repairEntity, RepairEntity.addRepairValidate.class);
		repairService.addRepair(repairEntity);
		
		String repairImg = repairEntity.getRepairImg();
		String[] split = repairImg.split(";");
		for (String filePath : split) {
			redisTemplate.opsForSet().add(UploadRedisConst.REPAIR_IMG_ALL, filePath); // 存入redis
		}
		return CommonResult.ok();
	}
	
	@Login(allowAnonymous = true)
	@ApiOperation("查看驳回原因")
	@GetMapping("/getRejectReason")
	public CommonResult getRejectReason(@RequestParam() Long id) {
		String reason = repairService.getRejectReason(id);
		return CommonResult.ok(reason,"");
	}
}

