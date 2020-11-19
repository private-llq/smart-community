package com.jsy.community.controller;

import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.FileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 基础控制器
 *
 * @author ling
 * @since 2020-11-19 15:07
 */
@RestController
@Api("文件控制器")
@RequestMapping("/file")
public class FileController {
	
	@ApiOperation("上传文件")
	@PostMapping("file")
	public CommonResult<FileVo> upload(@RequestParam MultipartFile file) {
		return CommonResult.ok(null);
	}
	
	@ApiOperation("删除上传的文件")
	@DeleteMapping("file/{id}")
	public CommonResult<Boolean> delete(@PathVariable Long id) {
		return CommonResult.ok();
	}
}
