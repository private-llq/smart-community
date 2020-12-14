package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityQO;
import com.jsy.community.service.ICommunityService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RestController
public class CommunityController {
	
	@Autowired
	private ICommunityService iCommunityService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private static final String BUCKETNAME = "community-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	/**
	* @Description: 新增社区
	 * @Param: [communityEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PostMapping("")
	public CommonResult addCommunity(@RequestBody CommunityEntity communityEntity){
		boolean result = iCommunityService.addCommunity(communityEntity);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL);
	}
	
	/**
	* @Description: 删除社区
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@DeleteMapping("{id}")
	public CommonResult deleteCommunity(@PathVariable Long id){
		boolean result = iCommunityService.deleteCommunity(id);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL);
	}
	
	/**
	* @Description: 修改社区
	 * @Param: [communityQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@PutMapping("")
	public CommonResult updateCommunity(@RequestBody CommunityQO communityQO){
		Map<String, Object> resultMap = iCommunityService.updateCommunity(communityQO);
		return (boolean)resultMap.get("result") ? CommonResult.ok() : CommonResult.error(JSYError.REQUEST_PARAM.getCode(),String.valueOf(resultMap.get("msg")));
	}
	
	/**
	* @Description: 查询社区
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.CommunityEntity>>
	 * @Author: chq459799974
	 * @Date: 2020/12/14
	**/
	@GetMapping("")
	public CommonResult<Page<CommunityEntity>> queryCommunity(@RequestBody BaseQO<CommunityQO> baseQO){
		return CommonResult.ok(iCommunityService.queryCommunity(baseQO));
	}
	
	
	@PostMapping("/uploadIconImg")
	@ApiOperation("社区头图上传")
	public CommonResult uploadIconImg(@RequestParam("file") MultipartFile file){
		String filePath = MinioUtils.upload(file, BUCKETNAME);
		redisTemplate.opsForSet().add("community_img_part",filePath);
		return CommonResult.ok(filePath);
	}
	
}
