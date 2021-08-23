package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IProprietorLogService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.log.ProprietorLog;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 业主操作日志 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-01-22
 */
@Api(tags = "业主操作日志控制器")
@RestController
@RequestMapping("/community/proprietorLog")
@ApiJSYController
@Slf4j
public class ProprietorLogController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IProprietorLogService proprietorLogService;
	
	@ApiOperation("保存业主操作记录")
	@PostMapping("/saveProprietorLog")
	@businessLog(operation = "新增",content = "新增了【业主操作记录】")
	public CommonResult saveProprietorLog(@RequestBody ProprietorLog proprietorLog){
		proprietorLog.setId(SnowFlake.nextId());
		proprietorLogService.saveProprietorLog(proprietorLog);
		return CommonResult.ok();
	}
}

