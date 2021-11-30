package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IAppVersionService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.AppVersionEntity;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.ValidatorUtils;
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
// @ApiJSYController
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
	
	@ApiOperation("添加APP版本")
	@PostMapping("/version")
	public CommonResult addAppVersion(@RequestBody AppVersionEntity appVersionEntity){
		ValidatorUtils.validateEntity(appVersionEntity);
		appVersionService.addAppVersion(appVersionEntity);
		return CommonResult.ok("操作成功");
	}
	
	@ApiOperation("修改APP版本信息")
	@PutMapping("/version")
	public CommonResult updateAppVersion(@RequestBody AppVersionEntity appVersionEntity){
		if(appVersionEntity.getId() == null){
			return CommonResult.error("请传入id");
		}
		appVersionEntity.setCreateTime(null);
		appVersionEntity.setSysType(null);
		return appVersionService.updateById(appVersionEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	@ApiOperation("删除APP版本")
	@DeleteMapping("/version")
	public CommonResult delAppVersion(@RequestParam Long id){
		if(id == null){
			return CommonResult.error("请传入id");
		}
		return appVersionService.removeById(id) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	 * @Description: 查询APP版本详情
	 * @author: DKS
	 * @since: 2021/9/22 11:49
	 * @Param: [sysType]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("查询APP版本详情")
	@GetMapping("/v2/version")
	public CommonResult queryAppVersion(Integer sysType, String sysVersion){
		if(!BusinessConst.SYS_TYPE_ANDROID.equals(sysType) && !BusinessConst.SYS_TYPE_IOS.equals(sysType)){
			return CommonResult.error("请传入正确的系统类型");
		}
		AppVersionEntity appVersionEntity = appVersionService.queryAppVersion(sysType, sysVersion);
		return CommonResult.ok(appVersionEntity,"查询成功");
	}
	
}
