package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAppVersionService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppVersionEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author lihao
 * @ClassName AppController
 * @Date 2021/2/5  15:38
 * @Description TODO
 * @Version 1.0
 **/
@Api(tags = "APP相关")
@Slf4j
@RestController
@RequestMapping("/app")
@Login(allowAnonymous = true)
@ApiJSYController
public class AppController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IAppVersionService appVersionService;
	
	@ApiOperation("APP上传到服务器")
	@PostMapping("/uploadApp")
	public CommonResult uploadApp(@RequestParam("file") MultipartFile file) {
		String app = MinioUtils.upload(file, "app");
		return CommonResult.ok(app);
	}

	@ApiOperation("APP上传到服务器")
	@PostMapping("/uploadIos")
	public CommonResult uploadIos(@RequestParam("file") MultipartFile file) {
		String app = MinioUtils.uploadName(file, "ios");
		return CommonResult.ok(app);
	}
	
//	@ApiOperation("APP上传到服务器")
//	@PostMapping("/uploadApp")
//	public CommonResult uploadApp(@RequestParam("file") MultipartFile file) {
////		String app = MinioUtils.upload(file, "app");
//		String app = MinioUtils.uploadPic(file, "user-face-avatar");
//		return CommonResult.ok(app);
//	}
	
	@ApiOperation("查询APP版本列表")
	@GetMapping("/list/version")
	public CommonResult queryAppVersionList(Integer sysType, String sysVersion){
		if(!BusinessConst.SYS_TYPE_ANDROID.equals(sysType) && !BusinessConst.SYS_TYPE_IOS.equals(sysType)){
			sysType = null;
		}
		List<AppVersionEntity> list = appVersionService.queryAppVersionList(sysType,sysVersion);
		if(list != null && list.size() == 1){
			return CommonResult.ok(appVersionService.queryAppVersionList(sysType,sysVersion).get(0),"查询成功");
		}
		return CommonResult.ok(list,"查询成功");
	}
	
	
	public static void main(String[] args) {
		System.out.println(SnowFlake.nextId());
	}
}
