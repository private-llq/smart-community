package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyCommunityListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
@Login
public class CommunityController {
	
	// TODO: 2021/4/16 这里的group没有改成  property是因为目前  group这种写法不知道其他人调ICommunityService时  人家是不是没有改成  property  所以我这里也先不动
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICommunityService communityService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private UserUtils userUtils;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.util.List < com.jsy.community.vo.BannerVO>>
	 * @Author lihao
	 * @Description 测试分布式事物  ==========先别删，有点用  要删的时候  我来================
	 * @Date 2020/12/23 15:47
	 * @Param [bannerQO]
	 **/
	@ApiOperation("添加社区")
	@GetMapping("/addCommunityEntity")
	public CommonResult addCommunityEntity() {
		communityService.addCommunityEntity();
		return CommonResult.ok();
	}

	@ApiOperation("获取社区电子地图")
	@GetMapping
	public CommonResult getElectronicMap(){
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		Map<String, Object> map = communityService.getElectronicMap(communityId);
		return CommonResult.ok(map);
	}

	/**
	 * @author: Pipi
	 * @description: 物业端添加社区
	 * @param communityEntity:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/7/22 9:37
	 **/
	@Login
	@PostMapping("/addCommunity")
	public CommonResult addCommunity(@RequestBody CommunityEntity communityEntity, HttpServletRequest request) {
		ValidatorUtils.validateEntity(communityEntity, CommunityEntity.ProperyuAddValidatedGroup.class);
		// 设置默认的社区房屋层级模式
		communityEntity.setHouseLevelMode(1);
		// 新增数据
		Long communityId = communityService.addCommunity(communityEntity, UserUtils.getUserId());
		// 获取登录用户数据
		AdminInfoVo adminUserInfo = UserUtils.getAdminUserInfo();
		adminUserInfo.getCommunityIdList().add(communityId);
		String token = request.getHeader("token");
		// 根据token,更新Redis数据
		userUtils.updateRedisByToken("Admin:Login", JSON.toJSONString(adminUserInfo), token);
		return CommonResult.ok("添加成功!");
	}

	/**
	 * @author: Pipi
	 * @description: 物业端分页查询小区列表
	 * @param baseQO:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/7/22 11:41
	 **/
	@Login
	@PostMapping("/queryCommunityList")
	public CommonResult communityList(@RequestBody BaseQO<CommunityEntity> baseQO) {
		if (baseQO.getSize() == null || baseQO.getSize() <= 0) {
			baseQO.setSize(10L);
		}
		if (baseQO.getPage() == null || baseQO.getPage() <= 0) {
			baseQO.setPage(1L);
		}
		if (baseQO.getQuery() == null) {
			baseQO.setQuery(new CommunityEntity());
		}
		PageInfo<PropertyCommunityListVO> communityListVOPageInfo = communityService.queryPropertyCommunityList(baseQO, UserUtils.getAdminUserInfo().getCommunityIdList());
		return CommonResult.ok(communityListVOPageInfo);
	}
	
}

