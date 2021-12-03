package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.RegionEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.service.AppContentService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PicUtil;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author chq459799974
 * @since 2020-11-19 13:33
 **/
@RequestMapping("content")
@Api(tags = "APP内容控制器")
@Slf4j
@RestController
// @ApiJSYController
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
	@Permit("community:admin:content:hotCity")
	public CommonResult setHotCity(@RequestBody List<RegionEntity> regionList){
		boolean result = appContentService.setHotCity(regionList);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"设置失败");
	}
	
	/**
	* @Description: 用户默认头像上传
	 * @Param: [avatar]
	 * @Return: com.jsy.community.vo.CommonResult<java.lang.String>
	 * @Author: chq459799974
	 * @Date: 2021/2/24
	**/
	@ApiOperation("用户默认头像上传")
	@PostMapping("defaultAvatar/upload")
	@Permit("community:admin:content:defaultAvatar:upload")
	public CommonResult uploadDefaultAvatar(MultipartFile avatar) {
		PicUtil.imageQualified(avatar);
		String url = MinioUtils.upload(avatar, BusinessConst.APP_SYS_DEFAULT_AVATAR_BUCKET_NAME);
		if(!StringUtils.isEmpty(url)){
			//TODO 修改静态配置文件sys_default_content.json(绝对路径)中的avatar属性
			return CommonResult.ok(url);
		}
		return CommonResult.error("上传失败");
	}
	
	@ApiOperation("天气图标上传")
	@PostMapping("weatherIcon/upload")
	@Permit("community:admin:content:weatherIcon:upload")
	public CommonResult uploadWeatherIcon(MultipartFile file){
		PicUtil.imageQualified(file);
		String url = MinioUtils.upload(file, "weather-icon");
		if(!StringUtils.isEmpty(url)){
			return CommonResult.ok(url);
		}
		return CommonResult.error("上传失败");
	}
	
	@ApiOperation("天气图标批量上传并写库(文件名需要为数字需要，且与天气接口对应，详见天气接口文档)")
	@PostMapping("weatherIcon/upload/batch")
	@Permit("community:admin:content:weatherIcon:upload:batch")
	public CommonResult uploadWeatherIconAndSave(@RequestParam String dirPath){
		int result = appContentService.addWeatherIconFromFileDirectory(dirPath);
		return CommonResult.ok("已添加" + result + "个天气图标");
	}
	
}
