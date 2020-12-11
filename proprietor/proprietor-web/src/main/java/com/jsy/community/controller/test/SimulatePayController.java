package com.jsy.community.controller.test;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.test.ISimulatePayService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.test.PayData;
import com.jsy.community.entity.test.SimulateTypeEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	
	@GetMapping("/getCompany")
	@ApiOperation("得到模拟缴费机构")
	public List<SimulateTypeEntity> getCompany(Integer type){
		return simulatePayService.getCompany(type);
	}
	
	
	@PostMapping("/getPayData")
	@ApiOperation("根据户号得到其缴费信息")
	public PayData getPayData(String number,Integer id){
		return simulatePayService.getPayData(number,id);
	}
}
