package com.jsy.community.controller.test;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.test.ISimulatePayService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.test.PayData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihao
 * @ClassName SimulatePayController
 * @Date 2020/12/10  15:50
 * @Description 模拟三方物业缴费
 * @Version 1.0
 **/
@Api(tags = "模拟得到三方物业缴费公司")
@RestController
@RequestMapping("/pay")
@Login(allowAnonymous = true)
@ApiJSYController
public class SimulatePayController {
	
	@DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
	private ISimulatePayService simulatePayService;
	
	@PostMapping("/getPayData")
	@ApiOperation("根据户号得到其缴费信息")
	public PayData getPayData(String number,Integer id){
		return simulatePayService.getPayData(number,id);
	}
}
