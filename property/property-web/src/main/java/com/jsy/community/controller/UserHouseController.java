package com.jsy.community.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 业主房屋认证 前端控制器
 * </p>
 *
 * @author jsy
 * @since 2020-11-25
 */
@Api(tags = "业主房屋认证控制器")
@RestController
@RequestMapping("/userHouse")
//@Login
@ApiJSYController
public class UserHouseController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserHouseService userHouseService;
	
	/**
	 * @return com.jsy.community.vo.CommonResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.UserHouseEntity>>
	 * @Author lihao
	 * @Description 业主房屋认证审核列表
	 * @Date 2020/11/25 15:35
	 * @Param [baseQO]
	 **/
	@ApiOperation("业主房屋认证审核列表")
	@PostMapping("/selectUserHouse")
	public CommonResult<Page<UserHouseEntity>> selectUserHouse(@RequestBody BaseQO<UserHouseEntity> baseQO,
	                                                           @ApiParam(value = "社区id") Long communityId){
		Page<UserHouseEntity> userHouseList = userHouseService.selectUserHouse(baseQO,communityId);
		return CommonResult.ok(userHouseList);
	}
	
	
}

