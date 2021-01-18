package com.jsy.community.controller.outer;

import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRedbagService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IRedbagService redbagService;
	
	@ApiOperation("【用户】检查真实身份")
	@GetMapping("community/user")
	public Map<String,Object> checkUserAndGetUid(){
		String uid = UserUtils.getUserId();
		return userService.checkUserAndGetUid(uid);
	}
	
	@ApiOperation("【单红包】领取")
	@PostMapping("redbag/receive/single")
	public boolean receiveSingleRedbag(@RequestBody RedbagQO redbagQO){
		ValidatorUtils.validateEntity(redbagQO,RedbagQO.receiveSingleValidated.class);
		redbagQO.setRedbagType(1);
		redbagService.receiveRedbag(redbagQO);
		return true;
	}
	
	@ApiOperation("【群红包】领取")
	@PostMapping("redbag/receive/group")
	public Map<String,Object> receiveGroupRedbag(){
		return null;
	}
	
	@ApiOperation("【红包】退款")
	@PostMapping("redbag/refund")
	public boolean redbagRefund(){
		return false;
	}
	
}
