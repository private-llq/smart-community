package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.BannerVO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author chq459799974
 * @since 2020-11-18 16:19
 **/
@Api(tags = "轮播图控制器")
@RestController
@RequestMapping("/banner")
@ApiJSYController
public class BannerController {

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IBannerService iBannerService;
	
	private static final String BUCKETNAME = "bannner-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	
	/**
	 * @Description: 轮播图列表查询
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【轮播图】列表查询")
	@PostMapping("/list")
	public CommonResult<List<BannerVO>> list(@RequestBody BannerQO bannerQO){
		List<BannerVO> returnList = iBannerService.queryBannerList(bannerQO);
		return CommonResult.ok(returnList);
	}

	/**
	* @Description: 轮播图 上传
	 * @Param: [file, bannerEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("【轮播图基本信息】上传")
//	@Login
	@PostMapping("upload")
	public CommonResult upload(@RequestBody BannerEntity bannerEntity){
		ValidatorUtils.validateEntity(bannerEntity, BannerEntity.addBannerValidatedGroup.class);
		//TODO 调CommonService方法，文件上传到fastdfs。url暂时写死
		//写库
		boolean b = iBannerService.addBanner(bannerEntity);
		if(b){
			return CommonResult.ok();
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 轮播图图片上传
	 * @Date 2020/12/4 18:04
	 * @Param [file]
	 **/
	@ApiOperation("【轮播图图片】上传")
	@PostMapping("uploadImg")
	public CommonResult uploadImg(@RequestParam("file") MultipartFile file){
		String filePath = MinioUtils.upload(file, BUCKETNAME);
		return CommonResult.ok(filePath);
	}

	/**
	* @Description: 轮播图 批量删除
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("【轮播图】批量删除")
//	@Login
	@DeleteMapping("")
	public CommonResult deleteBanner(@RequestBody Long[] ids){
		if(ids.length == 0){
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),JSYError.REQUEST_PARAM.getMessage());
		}
		boolean result = iBannerService.deleteBannerBatch(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),JSYError.INTERNAL.getMessage());
	}
}

