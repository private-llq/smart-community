package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IRedbagService;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.PaymentEnum;
import com.jsy.community.qo.RedbagQO;
import com.jsy.community.qo.proprietor.UserAccountTradeQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author chq459799974
 * @description 用户金钱账户控制器
 * @since 2021-01-08 11:41
 **/
@Api(tags = "用户金钱账户控制器")
@RestController
@RequestMapping("user/account")
@ApiJSYController
@Login
public class UserAccountController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IUserAccountService userAccountService;
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private IRedbagService redbagService;
	
	/**
	* @Description: 查询余额
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@ApiOperation("【用户账户】查询余额")
	@GetMapping("balance")
	public CommonResult queryBalance(){
		return CommonResult.ok(userAccountService.queryBalance(UserUtils.getUserId()));
	}
	
	/**
	* @Description: 账户交易
	 * @Param: [userAccountTradeQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/8
	**/
	@ApiOperation("【用户账户】账户交易")
	@PostMapping("trade")
	public CommonResult trade(@RequestBody UserAccountTradeQO userAccountTradeQO){
		ValidatorUtils.validateEntity(userAccountTradeQO);
		userAccountTradeQO.setUid(UserUtils.getUserId());
		userAccountService.trade(userAccountTradeQO);
		return CommonResult.ok("交易完成");
	}
	
	/**
	* @Description: 单发红包
	 * @Param: [redBagQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	**/
	@Login
	@ApiOperation("【红包】单发红包")
	@PostMapping("redbag/send/single")
	public CommonResult sendSingleRedbag(@RequestBody RedbagQO redbagQO){
		ValidatorUtils.validateEntity(redbagQO, RedbagQO.singleRedbagValidated.class);
		if(new BigDecimal("0.01").compareTo(redbagQO.getMoney()) == 1){
			return CommonResult.error("金额过小");
		}
		redbagQO.setFromType(BusinessConst.REDBAG_FROM_TYPE_PERSON);//目前写死个人红包，调用方不用传
		redbagQO.setType(PaymentEnum.CurrencyEnum.CURRENCY_CNY.getIndex());
		redbagQO.setGroupUuid(null);
		redbagQO.setNumber(null);
		redbagQO.setUserUuid(UserUtils.getUserId());
		redbagQO.setRedbagType(BusinessConst.REDBAG_TYPE_PRIVATE);
		redbagService.sendRedbag(redbagQO);
		return CommonResult.ok("发送成功");
	}
	
	/**
	* @Description: 群发红包
	 * @Param: [redBagQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/1/18
	**/
	@Login
	@ApiOperation("【红包】群发红包")
	@PostMapping("redbag/send/group")
	public CommonResult sendGroupRedbag(@RequestBody RedbagQO redbagQO){
		ValidatorUtils.validateEntity(redbagQO, RedbagQO.groupRedbagValidated.class);
		if(redbagQO.getMoney().doubleValue()/redbagQO.getNumber() < 0.01){
			return CommonResult.error("人数太多或金额太小");
		}
		redbagQO.setFromType(BusinessConst.REDBAG_FROM_TYPE_PERSON);//目前写死群发红包，调用方不用传
		redbagQO.setType(PaymentEnum.CurrencyEnum.CURRENCY_CNY.getIndex());
		redbagQO.setReceiveUserUuid(null);
		redbagQO.setUserUuid(UserUtils.getUserId());
		redbagQO.setRedbagType(BusinessConst.REDBAG_TYPE_GROUP);
		redbagService.sendRedbag(redbagQO);
		return CommonResult.ok("发送成功");
	}
	
}
