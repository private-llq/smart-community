package com.jsy.community.controller.outer;

import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
* @Description: 外部调用公共接口
 * @Author: chq459799974
 * @Date: 2021/1/20
**/
@Api(tags = "外部调用公共接口")
@ApiOutController
@RestController
@RequestMapping("common")
public class OutCommonController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserService userService;
	
	@Login
	@ApiOperation("【用户】获取uid和手机号")
	@GetMapping("user/detail")
	public CommonResult queryUserDetail(){
		String uid = UserUtils.getUserId();
		return CommonResult.ok(userService.queryUserDetailByUid(uid));
	}
	
}
