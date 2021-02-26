//package com.jsy.community.controller;
//
//import com.jsy.community.annotation.ApiJSYController;
//import com.jsy.community.annotation.auth.Login;
//import com.jsy.community.api.IWebSocketTest;
//import com.jsy.community.constant.Const;
//import com.jsy.community.vo.CommonResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author lihao
// * @ClassName WebSocketTestController
// * @Date 2020/12/29  11:16
// * @Description TODO
// * @Version 1.0
// **/
//@Api(tags = "websocket测试 for 报修订单")
//@RestController
//@RequestMapping("/repair")
//@Login(allowAnonymous = true)
//@ApiJSYController
//public class WebSocketTestController {
//
//	@DubboReference(version = Const.version, group = Const.group_property, check = false)
//	private IWebSocketTest webSocketTest;
//
//	@ApiOperation("测试用户下单后向物业端发送消息提示")
//	@PostMapping("/testWebSocket")
//	public CommonResult testWebSocket(@ApiParam("业主id") @RequestParam String uid) {
//		webSocketTest.senMsg(uid);
//		return CommonResult.ok();
//	}
//}
