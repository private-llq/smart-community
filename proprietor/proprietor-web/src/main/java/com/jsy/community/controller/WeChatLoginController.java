package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IWeChatLoginService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.utils.WeCharUtil;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-12 15:49
 **/
@Api(tags = "微信登录控制器")
@RestController
@RequestMapping("/WeChat")
// @ApiJSYController
public class WeChatLoginController {

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IWeChatLoginService weChatLoginService;



    @ApiOperation("登录")
    @PostMapping("/login")
    public CommonResult login(@RequestParam("code")String code) {
        JSONObject object = WeCharUtil.getAccessToken(code);
        if ("".equals(object)||object==null){
            return CommonResult.error("系统异常，请稍后再试！");
        }
        String accessToken = object.getString("access_token");
        String openid = object.getString("openid");
        return CommonResult.ok(weChatLoginService.login(openid));
    }

    @PostMapping("/bindingMobile")
    public CommonResult bindingMobile(@RequestBody BindingMobileQO bindingMobileQO){
        ValidatorUtils.validateEntity(bindingMobileQO, BindingMobileQO.BindingMobileValidated.class);
        UserAuthVo userAuthVo=weChatLoginService.bindingMobile(bindingMobileQO);
        return CommonResult.ok(userAuthVo);
    }



}
