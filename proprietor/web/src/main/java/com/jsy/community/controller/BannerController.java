package com.jsy.community.controller;


import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.BannerVO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * banner轮播图 前端控制器
 * </p>
 *
 * @author jsy
 * @since 2020-11-16
 */
@Api(tags = "轮播图控制器")
@RestController
@RequestMapping("/banner")
public class BannerController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IBannerService iBannerService;
	
	/**
	* @Description: 轮播图 上传
	 * @Param: [file, bannerEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("轮播图 上传")
	@PostMapping("upload")
	public CommonResult upload(MultipartFile[] file, BannerEntity bannerEntity){
		if(file == null || file.length == 0){
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"文件为空");
		}
		ValidatorUtils.validateEntity(bannerEntity, BannerEntity.addBannerValidatedGroup.class);
		//TODO 调CommonService方法，文件上传到fastdfs。url暂时写死
		bannerEntity.setUrl("http://3gimg.qq.com/map_openplat/lbs_web/custom_map_templates/custom_map_template_2.png");
		//写库
		boolean b = iBannerService.addBanner(bannerEntity);
		if(b){
			return CommonResult.ok();
		}
		return CommonResult.error(JSYError.INTERNAL);
	}
	
	/**
	* @Description: 轮播图列表查询
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("轮播图 列表查询")
	@PostMapping("list")
	public CommonResult list(@ApiParam(value = "需要删除的id【数组】") @RequestBody BannerQO bannerQO){
		ValidatorUtils.validateEntity(bannerQO, BannerQO.queryBannerValidatedGroup.class);
		List<BannerVO> returnList = iBannerService.queryBannerList(bannerQO);
		return CommonResult.ok(returnList);
	}
	
	/**
	* @Description: 轮播图 批量删除
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("轮播图 批量删除")
	@DeleteMapping("")
	public CommonResult deleteBanner(@ApiParam(value = "需要删除的ids集合")@RequestBody Long[] ids){
		boolean result = iBannerService.deleteBannerBatch(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),JSYError.INTERNAL.getMessage());
	}
}

