package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.vo.BannerVO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author chq459799974
 * @since 2020-11-18 16:19
 **/
@Api(tags = "轮播图控制器")
@RestController
@RequestMapping("banner")
@ApiJSYController
public class BannerController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IBannerService iBannerService;
	
	/**
	 * @Description: 轮播图列表查询
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【轮播图】列表查询")
	@PostMapping("list")
	public CommonResult<List<BannerVO>> list(@RequestBody BannerQO bannerQO){
		ValidatorUtils.validateEntity(bannerQO, BannerQO.queryBannerValidatedGroup.class);
		List<BannerVO> returnList = iBannerService.queryBannerList(bannerQO);
		return CommonResult.ok(returnList);
	}
	
}

