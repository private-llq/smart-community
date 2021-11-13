package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.entity.sys.SysPlatformSettingEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.service.ISysPlatformSettingService;
import com.jsy.community.vo.CommonResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author DKS
 * @description 平台设置
 * @since 2021-11-13 14:45
 **/
@RestController
@RequestMapping("/platform")
@ApiJSYController
public class SysPlatformSettingController {
	
	@Resource
	private ISysPlatformSettingService sysPlatformSettingService;
	
	/**
	 * @Description: 新增平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:24
	 * @Param: [sysPlatformSettingEntity]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@PostMapping("/edit")
	@Login
	@businessLog(operation = "新增",content = "新增了【平台设置】")
	public CommonResult editPlatform(@RequestBody SysPlatformSettingEntity sysPlatformSettingEntity){
		boolean b = sysPlatformSettingService.editPlatform(sysPlatformSettingEntity);
		return b ? CommonResult.ok("添加成功") : CommonResult.error(JSYError.INTERNAL.getCode(),"添加失败");
	}
	
	/**
	 * @Description: 查询平台设置
	 * @author: DKS
	 * @since: 2021/11/13 15:33
	 * @Param: []
	 * @return: com.jsy.community.vo.CommonResult<com.jsy.community.entity.sys.SysPlatformSettingEntity>
	 */
	@Login
	@GetMapping("/query")
	public CommonResult<SysPlatformSettingEntity> selectPlatform(){
		return CommonResult.ok(sysPlatformSettingService.selectPlatform());
	}
}
