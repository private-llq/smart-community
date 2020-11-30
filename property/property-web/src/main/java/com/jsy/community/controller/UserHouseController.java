package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.constant.Const;
import com.jsy.community.vo.UserHouseVO;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 业主房屋认证 前端控制器
 * </p>
 *
 * @author lihao
 * @since 2020-11-25
 */
@Api(tags = "业主房屋认证控制器")
@RestController
@RequestMapping("/userHouse")
@Login(allowAnonymous = true)
@ApiJSYController
public class UserHouseController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserHouseService userHouseService;
	
	
	/**
	 * @return com.jsy.community.vo.CommonResult<com.baomidou.mybatisplus.extension.plugins.pagination.Page < com.jsy.community.entity.UserHouseEntity>>
	 * @Author lihao
	 * @Description 业主房屋认证审核列表
	 * @Date 2020/11/25 15:35
	 * @Param [baseQO]
	 **/
	@ApiOperation("业主房屋认证审核列表")
	@PostMapping("/selectUserHouse")
	public CommonResult<PageInfo<UserHouseVO>> selectUserHouse(@RequestBody BaseQO<UserHouseEntity> baseQO,
	                                                           @ApiParam(value = "社区id")
	                                                           @RequestParam Long communityId) {
		PageInfo<UserHouseVO> page = userHouseService.selectUserHouse(baseQO, communityId);
		return CommonResult.ok(page);
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 * @Author lihao
	 * @Description 通过审核
	 * @Date 2020/11/26 15:58
	 * @Param [id]
	 **/
	@ApiOperation("通过审核")
	@GetMapping("/pass")
	public CommonResult<Boolean> pass(@ApiParam(value = "待审核房屋id") Long id) {
		Boolean b = userHouseService.pass(id);
		return b ? CommonResult.ok() : CommonResult.error("审核失败");
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult<java.lang.Boolean>
	 * @Author lihao
	 * @Description 通过审核
	 * @Date 2020/11/26 15:58
	 * @Param [id]
	 **/
	@ApiOperation("不通过审核")
	@GetMapping("/notPass")
	public CommonResult<Boolean> notPass(@ApiParam(value = "待审核房屋id") Long id) {
		Boolean b = userHouseService.notPass(id);
		return b ? CommonResult.ok() : CommonResult.error("审核失败");
	}
	
	
}

