package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 社区 前端控制器
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-25
 */
@Api(tags = "社区控制器")
@RestController
@ApiJSYController
@RequestMapping("/community")
public class CommunityController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List<com.jsy.community.vo.BannerVO>>
	 * @Author lihao
	 * @Description 测试分布式事物
	 * @Date 2020/12/23 15:47
	 * @Param [bannerQO]
	 **/
	@ApiOperation("添加社区")
	@GetMapping("/addCommunityEntity")
	public CommonResult addCommunityEntity(){
		communityService.addCommunityEntity();
		return CommonResult.ok();
	}
}

