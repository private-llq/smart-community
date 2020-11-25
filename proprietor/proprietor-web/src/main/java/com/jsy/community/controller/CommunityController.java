package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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

	/**
	 * 通过社区名称和城市id查询相关的社区数据
	 * @author YuLF
	 * @since  2020/11/23 11:21
	 * @Param  communityEntity 	必要参数实体
	 * @return 返回通过社区名称和城市id查询结果
	 */
	@ApiOperation("社区模糊搜索接口")
	@PostMapping()
	public CommonResult<List<CommunityEntity>> getCommunityByName(@RequestBody CommunityEntity communityEntity){
		//验证请求参数
		ValidatorUtils.validateEntity(communityEntity, CommunityEntity.GetCommunityByName.class);
		return CommonResult.ok(iCommunityService.getCommunityByName(communityEntity));
	}
	
	/**
	* @Description: 小区定位
	 * @Param: [uid, location]
	 * @Return: com.jsy.community.vo.CommonResult<com.jsy.community.entity.CommunityEntity>
	 * @Author: chq459799974
	 * @Date: 2020/11/25
	**/
	@ApiOperation("社区定位")
	@PostMapping("locate")
	public CommonResult<CommunityEntity> locate(Long uid, @RequestBody Map<String,Double> location){
		return CommonResult.ok(iCommunityService.locateCommunity(uid,location));
	}
	
}
