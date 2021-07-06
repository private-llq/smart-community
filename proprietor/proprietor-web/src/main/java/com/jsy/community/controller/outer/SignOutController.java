package com.jsy.community.controller.outer;

import com.jsy.community.annotation.ApiOutController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.SmsUtil;
import com.jsy.community.vo.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chq459799974
 * @description 提供给签章调用
 * @since 2021-07-06 09:34
 **/
@RestController
@ApiOutController
@RequestMapping("/sign")
public class SignOutController {
	
	/**
	* @Description: 发送并返回验证码
	 * @Param: []
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/7/6
	**/
	@GetMapping("/vcode/sendAndGet")
	@Login
	public CommonResult sendAndGetVcode(@RequestParam String mobile, Integer length){
		if(length != null && (length < 4 || length > 6)){
			throw new JSYException("验证码长度4-6");
		}
		return CommonResult.ok(SmsUtil.sendVcode(mobile,length),"发送成功");
	}
	
}
