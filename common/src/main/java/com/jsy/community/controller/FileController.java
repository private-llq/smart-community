package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.utils.FileUploadUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.FileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 基础控制器
 *
 * @author ling
 * @since 2020-11-19 15:07
 */
@RestController
@Api(tags = "文件控制器")
@RequestMapping("/file")
@ApiJSYController
@ConditionalOnProperty(value = "jsy.web.enable", havingValue = "true")
public class FileController {
	
	@Resource
	private FileUploadUtils fileUploadUtils;
	
	@ApiOperation("上传文件")
	@PostMapping("")
	@ApiImplicitParam(name = "type", value = "上传文件类型，1头像", allowableValues = "1", dataType = "int", paramType = "query")
	public CommonResult<FileVo> upload(@RequestParam Integer type, @RequestParam("file") MultipartFile file) {
		return CommonResult.ok(fileUploadUtils.upload(type, file));
	}
	
	@ApiOperation("删除上传的文件")
	@DeleteMapping("file/{id}")
	public CommonResult<Boolean> delete(@PathVariable Long id) {
		return CommonResult.ok();
	}
}
