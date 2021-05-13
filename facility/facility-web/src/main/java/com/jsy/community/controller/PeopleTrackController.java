package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPeopleTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PeopleTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PeopleTrackQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-04-26
 */
@Api(tags = "人员轨迹")
@RestController
@ApiJSYController
@RequestMapping("/peopleTrack")
@Login
public class PeopleTrackController {
	
	@DubboReference(version = Const.version, group = Const.group_facility, check = false)
	private IPeopleTrackService peopleTrackService;
	
	@ApiOperation("分页查询人员轨迹")
	@PostMapping("/listPeopleTrack")
	public CommonResult listPeopleTrack(@RequestBody BaseQO<PeopleTrackQO> peopleQo) {
		if (peopleQo.getQuery() == null) {
			peopleQo.setQuery(new PeopleTrackQO());
		}
		Long communityId = UserUtils.getAdminUserInfo().getCommunityId();
		peopleQo.getQuery().setCommunityId(communityId);
		PageInfo<PeopleTrackEntity> pageInfo = peopleTrackService.listPeopleTrack(peopleQo);
		return CommonResult.ok(pageInfo);
	}
}

