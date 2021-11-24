package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IPeopleTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PeopleTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.PeopleTrackQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
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
public class PeopleTrackController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPeopleTrackService peopleTrackService;
	
	@ApiOperation("分页查询人员轨迹")
	@PostMapping("/listPeopleTrack")
	@Permit("community:property:peopleTrack:listPeopleTrack")
	public CommonResult listPeopleTrack(@RequestBody BaseQO<PeopleTrackQO> peopleQo) {
		if (peopleQo.getQuery() == null) {
			peopleQo.setQuery(new PeopleTrackQO());
		}
		peopleQo.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		PageInfo<PeopleTrackEntity> pageInfo = peopleTrackService.listPeopleTrack(peopleQo);
		return CommonResult.ok(pageInfo);
	}
}

