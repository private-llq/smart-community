package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.entity.PayHouseOwnerEntity;
import com.jsy.community.entity.RepairOrderEntity;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lihao
 * @ClassName PayRecordController
 * @Date 2020/12/11  9:47
 * @Description TODO
 * @Version 1.0
 **/
@Api(tags = "缴费记录")
@RestController
@RequestMapping("/record")
@Login(allowAnonymous = true)
@ApiJSYController
public class PayRecordController {

	@GetMapping("/getPayRecord")
	@ApiOperation("获取业主缴费记录")
	public CommonResult<RepairOrderEntity> getPayRecord(@RequestBody List<PayHouseOwnerEntity> owner, List<LocalDateTime> dateTime){
		System.out.println();
		String uid = UserUtils.getUserId();
		return null;
	}
}
