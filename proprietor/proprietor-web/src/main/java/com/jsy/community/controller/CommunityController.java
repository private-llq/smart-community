package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author chq459799974
 * @description 社区控制器
 * @since 2020-11-19 16:59
 **/
@RequestMapping("community")
@Api(tags = "社区控制器")
@Login( allowAnonymous = true)
@Slf4j
@ApiJSYController
@RestController
public class CommunityController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService iCommunityService;
	
	//TODO 和公共接口是否重复？
	@GetMapping("")
	public CommonResult<Page<CommunityEntity>> queryCommunity(@RequestBody BaseQO<CommunityQO> baseQO){
		return CommonResult.ok(iCommunityService.queryCommunity(baseQO));
	}
	
}
