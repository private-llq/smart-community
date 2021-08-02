package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorHistoryEntity;
import com.jsy.community.entity.VisitorStrangerEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author chq459799974
 * @description 物业端访客Controller
 * @since 2021-04-12 13:45
 **/
@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
@Login
@ApiJSYController
public class VisitorController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IVisitorService visitorService;
	
	/**
	* @Description: 访客记录 分页查询(现在主表数据是t_visitor,以后会改为t_visitor_history)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/12
	**/
	@PostMapping("page")
	public CommonResult queryVisitorPage(@RequestBody BaseQO<VisitorHistoryEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new VisitorHistoryEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(visitorService.queryVisitorPage(baseQO),"查询成功");
	}
	
	/**
	* @Description: 查询单次访客邀请的随行人员列表
	 * @Param: [visitorId]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@GetMapping("follow")
	public CommonResult queryFollowPersonListByVisitorId(@RequestParam Long visitorId){
		return CommonResult.ok(visitorService.queryFollowPersonListByVisitorId(visitorId),"查询成功");
	}
	
	/**
	* @Description: 陌生人记录 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	@PostMapping("stranger/page")
	public CommonResult queryStrangerPage(@RequestBody BaseQO<VisitorStrangerEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new VisitorStrangerEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(visitorService.queryStrangerPage(baseQO));
	}
	
}
