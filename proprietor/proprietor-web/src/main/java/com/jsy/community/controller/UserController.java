package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IUserAuthService;
import com.jsy.community.api.IUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.JwtUtils;
import com.jsy.community.utils.RegexUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 业主控制器
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
@RequestMapping("user")
@Api(tags = "用户控制器")
@RestController
@ApiJSYController
public class UserController {
	@DubboReference(version = Const.version, group = Const.group, check = false, timeout = 10000)
	private IUserAuthService userAuthService;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private IUserService userService;

	@DubboReference(version = Const.version, group = Const.group, check = false)
	private ICarService carService;
	
	@PostMapping("test")
	@ApiOperation("test")
	@Login
	public List<UserAuthEntity> test(@RequestBody BaseQO<UserEntity> qo) {
		ValidatorUtils.validateEntity(qo);
		
		return userAuthService.getList(true);
	}

	@PostMapping("proprietorRegister")
	@ApiOperation("业主登记")
//	@Login
	public CommonResult<?> proprietorRegister(@RequestBody UserEntity userEntity){
		//获取用户id信息
//		Long uid = JwtUtils.getUserId();
		Long uid = 12L;
		//新增业主信息时，必须要携带当前用户的uid
		userEntity.setId(uid);
		//验证业主信息登记必填项
		ValidatorUtils.validateEntity(userEntity, UserEntity.ProprietorRegister.class);
		//有填登记车辆信息的情况下
		if(userEntity.getHasCar()){
			if( null == userEntity.getCarEntity() ){
				return CommonResult.error("车辆信息未填写!");
			}
			//验证车辆信息
			ValidatorUtils.validateEntity(userEntity.getCarEntity(), CarEntity.proprietorCarValidated.class);
			if (carService.carIsExist(userEntity.getCarEntity().getCarPlate())) {
				return CommonResult.error(JSYError.NOT_IMPLEMENTED.getCode(), "车辆车牌已经登记存在!");
			}
			userEntity.getCarEntity().setUid(uid);
		}
		//登记业主信息
		return userService.proprietorRegister(userEntity) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
	}


}
