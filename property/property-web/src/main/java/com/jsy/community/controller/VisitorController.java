package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	* @Description: 访客记录 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/12
	**/
	@PostMapping("page")
	public CommonResult queryVisitorPage(@RequestBody BaseQO<VisitorEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new VisitorEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(visitorService.queryVisitorPage(baseQO),"查询成功");
	}
	
}
