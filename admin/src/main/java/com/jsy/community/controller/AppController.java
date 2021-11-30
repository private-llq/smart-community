package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.entity.AppVersionEntity;
import com.jsy.community.service.IAppVersionService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author DKS
 * @ClassName AppController
 * @Date 2021/11/11  15:15
 * @Description APP上传
 * @Version 1.0
 **/
@Api(tags = "APP相关")
@Slf4j
@RestController
@RequestMapping("/app")
@Login(allowAnonymous = true)
// @ApiJSYController
public class AppController {
	
	@Resource
	private IAppVersionService appVersionService;
	
	/**
	 * @Description: ANDROID-APP上传到服务器
	 * @author: DKS
	 * @since: 2021/11/13 13:57
	 * @Param: [file]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("ANDROID-APP上传到服务器")
	@PostMapping("/uploadApp")
	public CommonResult uploadApp(@RequestParam("file") MultipartFile file) {
		String app = MinioUtils.upload(file, "sys-android");
		return CommonResult.ok(app);
	}
	
	/**
	 * @Description: IOS-APP上传到服务器
	 * @author: DKS
	 * @since: 2021/11/13 13:58
	 * @Param: [file]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("IOS-APP上传到服务器")
	@PostMapping("/uploadIos")
	public CommonResult uploadIos(@RequestParam("file") MultipartFile file) {
		String app = MinioUtils.upload(file, "sys-ios");
		return CommonResult.ok(app);
	}
	
	/**
	 * @Description: 查询APP版本列表
	 * @author: DKS
	 * @since: 2021/11/13 13:58
	 * @Param: [sysType, sysVersion]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("查询APP版本列表")
	@GetMapping("/list/version")
	public CommonResult queryAppVersionList(Integer sysType, String sysVersion){
		if(!BusinessConst.SYS_TYPE_ANDROID.equals(sysType) && !BusinessConst.SYS_TYPE_IOS.equals(sysType)){
			sysType = null;
		}
		return CommonResult.ok(appVersionService.queryAppVersionList(sysType,sysVersion),"查询成功");
	}
	
	/**
	 * @Description: 添加APP版本
	 * @author: DKS
	 * @since: 2021/11/13 13:58
	 * @Param: [appVersionEntity]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("添加APP版本")
	@PostMapping("/version/insert")
	public CommonResult addAppVersion(@RequestBody AppVersionEntity appVersionEntity){
		ValidatorUtils.validateEntity(appVersionEntity);
		appVersionService.addAppVersion(appVersionEntity);
		return CommonResult.ok("操作成功");
	}
	
	/**
	 * @Description: 修改APP版本信息
	 * @author: DKS
	 * @since: 2021/11/13 13:58
	 * @Param: [appVersionEntity]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("修改APP版本信息")
	@PutMapping("/version/update")
	public CommonResult updateAppVersion(@RequestBody AppVersionEntity appVersionEntity){
		if(appVersionEntity.getId() == null){
			return CommonResult.error("请传入id");
		}
		appVersionEntity.setCreateTime(null);
		appVersionEntity.setSysType(null);
		return appVersionService.updateById(appVersionEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	 * @Description: 删除APP版本
	 * @author: DKS
	 * @since: 2021/11/13 13:58
	 * @Param: [id]
	 * @return: com.jsy.community.vo.CommonResult
	 */
	@ApiOperation("删除APP版本")
	@DeleteMapping("/version/delete")
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
	@GetMapping("/version/detail")
	public CommonResult queryAppVersion(Integer sysType, String sysVersion){
		if(!BusinessConst.SYS_TYPE_ANDROID.equals(sysType) && !BusinessConst.SYS_TYPE_IOS.equals(sysType)){
			return CommonResult.error("请传入正确的系统类型");
		}
		AppVersionEntity appVersionEntity = appVersionService.queryAppVersion(sysType, sysVersion);
		return CommonResult.ok(appVersionEntity,"查询成功");
	}
	
}
