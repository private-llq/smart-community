package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.utils.WeCharUtil;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-12 15:49
 **/
@Api(tags = "微信登录控制器")
@RestController
@RequestMapping("/WeChat")
@ApiJSYController
public class WeChatController {

    @ApiOperation("获取秘钥")
    @PostMapping("/getParameter")
    public CommonResult getParameter() {
        Map<Object, Object> map = new HashMap<>();
        map.put("clientId", WeCharUtil.getClientId());
        map.put("clientSecret",WeCharUtil.getClientSecret());
        return CommonResult.ok(map);
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public CommonResult login(@RequestParam("code")String code) {
        JSONObject object = WeCharUtil.getAccessToken(code);
        if (object.equals("")||object==null){
            return CommonResult.error("系统异常，请稍后再试！");
        }
        String accessToken = object.getString("access_token");
        String openid = object.getString("openid");
        //获取用户信息
        JSONObject userInfo = WeCharUtil.getUserInfo(accessToken, openid);
        String uuid = object.getString("uuid");
        String username = object.getString("username");
        String nickname = object.getString("nickname");
        String avatar = object.getString("avatar");
        String company = object.getString("company");
        String location = object.getString("location");
        String email = object.getString("email");
        String remark = object.getString("remark");
        String gender = object.getString("gender");

        return CommonResult.ok();
    }


}
