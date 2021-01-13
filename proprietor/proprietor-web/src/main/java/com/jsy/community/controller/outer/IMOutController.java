package com.jsy.community.controller.outer;

import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.utils.UserUtils;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
* @Description: 提供给IM组调用
 * @Author: chq459799974
 * @Date: 2021/1/13
**/
@Api(tags = "提供给IM组调用")
@ApiOutController
@RestController
@RequestMapping("im")
public class IMOutController {
	
	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserService userService;
	
	@GetMapping("community/user")
	public Map<String,Object> checkUserAndGetUid(){
		String uid = UserUtils.getUserId();
		return userService.checkUserAndGetUid(uid);
	}
	
}
