package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IWeChatLoginService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.BindingMobileQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.utils.ios.AppleTokenVo;
import com.jsy.community.utils.ios.AppleUtil;
import com.jsy.community.utils.ios.IOSUtil;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAuthVo;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-05-31 16:02
 **/
@RestController
// @ApiJSYController
@RequestMapping("/Ios")
public class IosLoginController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IWeChatLoginService weChatLoginService;
    /**
     * @Description: 苹果三方登录接口
     * @author: Hu
     * @since: 2021/6/1 9:09
     * @Param: [code]
     * @return: com.jsy.community.vo.CommonResult
     */
    @LoginIgnore
    @PostMapping("/login")
    // @Permit("community:proprietor:Ios:login")
    public CommonResult login(@RequestParam String identityToken){
        String[] split = identityToken.split("\\.");
        AppleTokenVo userInfo = AppleUtil.getAppleUserInfo(split[1]);
        if (userInfo != null) {
            if (AppleUtil.verifyIdentityToken(IOSUtil.getPublicKey(AppleUtil.getKid(split[0])), identityToken, userInfo.getAud(), userInfo.getSub())){
                UserAuthVo userAuthVo = weChatLoginService.IosLogin(userInfo.getSub());
                return CommonResult.ok(userAuthVo);
            }
        }
        return CommonResult.error("服务器繁忙，请稍后再试！");
    }
    
    /**
     * @Description: 苹果三方登录接口(不绑定手机)
     * @author: Hu
     * @since: 2021/6/1 9:09
     * @Param: [code]
     * @return: com.jsy.community.vo.CommonResult
     */
    @LoginIgnore
    @PostMapping("/loginNotMobile")
    // @Permit("community:proprietor:Ios:loginNotMobile")
    public CommonResult loginNotMobile(@RequestParam String identityToken){
        String[] split = identityToken.split("\\.");
        AppleTokenVo userInfo = AppleUtil.getAppleUserInfo(split[1]);
        if (userInfo != null) {
            if (AppleUtil.verifyIdentityToken(IOSUtil.getPublicKey(AppleUtil.getKid(split[0])), identityToken, userInfo.getAud(), userInfo.getSub())){
                UserAuthVo userAuthVo = weChatLoginService.loginNotMobile(userInfo.getSub());
                return CommonResult.ok(userAuthVo);
            }
        }
        return CommonResult.error("服务器繁忙，请稍后再试！");
    }

    /**
     * @Description: 苹果三方登录绑定手机
     * @author: Hu
     * @since: 2021/6/1 9:12
     * @Param: [bindingMobileQO]
     * @return: com.jsy.community.vo.CommonResult
     */
    @LoginIgnore
    @PostMapping("/bindingMobile")
    // @Permit("community:proprietor:Ios:bindingMobile")
    public CommonResult bindingMobile(@RequestBody BindingMobileQO bindingMobileQO){
        ValidatorUtils.validateEntity(bindingMobileQO, BindingMobileQO.BindingMobileValidated.class);
        UserAuthVo userAuthVo=weChatLoginService.bindingMobile(bindingMobileQO);
        return CommonResult.ok(userAuthVo);
    }
}
