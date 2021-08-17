package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarTrackService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarTrackEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CarTrackQO;
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
 * 车辆轨迹 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2021-04-24
 */
@Api(tags = "车辆轨迹")
@RestController
@RequestMapping("/carTrack")
@ApiJSYController
@Login
public class CarTrackController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private ICarTrackService carTrackService;
	
	@ApiOperation("分页查询车辆轨迹")
	@PostMapping("/listCarTrack")
	public CommonResult listCarTrack(@RequestBody BaseQO<CarTrackQO> carQo) {
		if (carQo.getQuery() == null) {
			carQo.setQuery(new CarTrackQO());
		}
		carQo.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		PageInfo<CarTrackEntity> pageInfo = carTrackService.listCarTrack(carQo);
		return CommonResult.ok(pageInfo);

	}
}

