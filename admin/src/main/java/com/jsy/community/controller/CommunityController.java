package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.service.impl.CommunityServiceImpl;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author chq459799974
 * @description TODO
 * @since 2020-11-19 16:59
 **/
@RequestMapping("community")
@Api(tags = "社区控制器")
@Login( allowAnonymous = true)
@Slf4j
@RestController
public class CommunityController {
	
	@Autowired
	private CommunityServiceImpl communityServiceImpl;
	
	@PostMapping("")
	public CommonResult addCommunity(@RequestBody CommunityEntity communityEntity){
		boolean result = communityServiceImpl.addCommunity(communityEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL);
	}
	
	@DeleteMapping("{id}")
	public CommonResult deleteCommunity(@PathVariable Long id){
		boolean result = communityServiceImpl.deleteCommunity(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL);
	}
	
	@PutMapping("")
	public CommonResult updateCommunity(@RequestBody CommunityEntity communityEntity){
		Map<String, Object> resultMap = communityServiceImpl.updateCommunity(communityEntity);
		return (boolean)resultMap.get("result") ? CommonResult.ok() : CommonResult.error(JSYError.REQUEST_PARAM.getCode(),String.valueOf(resultMap.get("msg")));
	}
	
	@GetMapping("")
	public CommonResult<Page<CommunityEntity>> queryCommunity(@RequestBody BaseQO<CommunityQO> baseQO){
		return CommonResult.ok(communityServiceImpl.queryCommunity(baseQO));
	}
	
}
