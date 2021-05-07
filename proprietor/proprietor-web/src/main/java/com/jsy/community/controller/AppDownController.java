package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lihao
 * @ClassName AppDownController
 * @Date 2021/2/5  15:38
 * @Description TODO
 * @Version 1.0
 **/
@Api(tags = "APP上传")
@Slf4j
@RestController
@RequestMapping("/app")
@Login(allowAnonymous = true)
@ApiJSYController
public class AppDownController {
	
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
}
