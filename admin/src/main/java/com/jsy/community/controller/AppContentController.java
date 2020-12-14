package com.jsy.community.controller;

import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.AppContentService;
import com.jsy.community.service.impl.AppContentServiceImpl;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chq459799974
 * @since 2020-11-19 13:33
 **/
@RequestMapping("content")
@Api(tags = "APP内容控制器")
@Login( allowAnonymous = true)
@Slf4j
@RestController
public class AppContentController {
	
	@Autowired
	private AppContentService appContentService;
	
	/**
	* @Description: 设置推荐城市
	 * @Param: [regionList]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@ApiOperation("【推荐城市】设置")
	@PostMapping("hotCity")
	@Transactional(rollbackFor = Exception.class)
	public CommonResult setHotCity(@RequestBody List<RegionEntity> regionList){
		boolean result = appContentService.setHotCity(regionList);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置失败");
	}
}
