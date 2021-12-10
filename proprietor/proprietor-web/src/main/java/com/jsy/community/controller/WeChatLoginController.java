package com.jsy.community.controller;

import com.jsy.community.api.IWeChatLoginService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAuthVo;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.support.ContextHolder;
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

    
    @LoginIgnore
    @ApiOperation("登录")
    @PostMapping("/login")
    // @Permit("community:proprietor:WeChat:login")
    public CommonResult login(@RequestParam("code")String code) {
//        JSONObject object = WeCharUtil.getAccessToken(code);
//        if ("".equals(object)||object==null){
//            return CommonResult.error("系统异常，请稍后再试！");
//        }
//        String accessToken = object.getString("access_token");
//        String openid = object.getString("openid");
        return CommonResult.ok(weChatLoginService.loginV2(code));
    }

    @PostMapping("/bindingMobile")
    // @Permit("community:proprietor:WeChat:bindingMobile")
    public CommonResult bindingMobile(@RequestBody BindingMobileQO bindingMobileQO){
        ValidatorUtils.validateEntity(bindingMobileQO, BindingMobileQO.BindingMobileValidated.class);
        UserAuthVo userAuthVo=weChatLoginService.bindingMobileV2(bindingMobileQO, ContextHolder.getContext().getLoginUser());
        return CommonResult.ok(userAuthVo);
    }



}
