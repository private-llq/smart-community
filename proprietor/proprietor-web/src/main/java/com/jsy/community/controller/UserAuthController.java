package com.jsy.community.controller;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.auth.Auth;
import com.jsy.community.api.*;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserAuthEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.UserThirdPlatformQO;
import com.jsy.community.qo.proprietor.*;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAuthVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseSmsRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户认证
 *
 * @author ling
 * @since 2020-11-12 10:30
 */
@RequestMapping("user/auth")
@Api(tags = "用户认证控制器")
@RestController
// @ApiJSYController
@Slf4j
public class UserAuthController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    @SuppressWarnings("unused")
    private ICaptchaService captchaService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    @SuppressWarnings("unused")
    private ProprietorUserService userService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    @SuppressWarnings("unused")
    private IUserAuthService userAuthService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    @SuppressWarnings("unused")
    private ICommonService commonService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER)
    private IBaseSmsRpcService baseSmsRpcService;

    /**
     * 发送验证码
     */
    @LoginIgnore
    @ApiOperation("发送验证码，手机或邮箱，参数不可同时为空")
    @GetMapping("/send/code")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = UserAuthEntity.CODE_TYPE_NOTE, required = true,
                    allowableValues = "1,2,3,4,5", paramType = "query")
    })
    @LoginIgnore
    public CommonResult<Boolean> sendCode(@RequestParam String account, @RequestParam Integer type) {
        if (RegexUtils.isMobile(account)) {
            baseSmsRpcService.sendVerificationCode(account);
            return CommonResult.ok();
        } else {
            throw new ProprietorException(JSYError.REQUEST_PARAM);
        }
    }

    /**
     * 发送验证码
     */
    /*@ApiOperation("发送验证码，手机或邮箱，参数不可同时为空")
    @GetMapping("/send/code")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号，手机号或者邮箱地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = UserAuthEntity.CODE_TYPE_NOTE, required = true,
                    allowableValues = "1,2,3,4,5", paramType = "query")
    })
    @Permit("community:proprietor:user:auth:send:code")
    @LoginIgnore
    public CommonResult<Boolean> sendCode(@RequestParam String account,
                                          @RequestParam Integer type) {
        boolean b;
        if (RegexUtils.isMobile(account)) {
            b = captchaService.sendMobile(account, type);
        } else if (RegexUtils.isEmail(account)) {
            b = captchaService.sendEmail(account, type);
        } else {
            throw new ProprietorException(JSYError.REQUEST_PARAM);
        }
        return b ? CommonResult.ok() : CommonResult.error("验证码发送失败");
    }*/

    @ApiOperation("注册")
    @PostMapping("/register")
    @LoginIgnore
    public CommonResult<UserAuthVo> register(@RequestBody RegisterQO qo) {
        ValidatorUtils.validateEntity(qo);
        return CommonResult.ok(userService.registerV3(qo), "注册成功");
    }

   /* @ApiOperation("注册")
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<UserAuthVo> register(@RequestBody RegisterQO qo) {
        ValidatorUtils.validateEntity(qo);
        //验证码验证
//		commonService.checkVerifyCode(qo.getAccount(), qo.getCode());
        userService.register(qo);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUid(uid);

        //生成带token和用户信息的的UserAuthVo(注册后设置密码用)
        UserAuthVo userAuthVo = userService.createAuthVoWithToken(userInfoVo);
        return CommonResult.ok(userAuthVo, "注册成功");
    }*/

    @LoginIgnore
    @ApiOperation("游客登录")
    @GetMapping("/login/tourist")
    public CommonResult getTouristToken() {
        return CommonResult.ok("00000tourist", "登录成功");
    }

    @LoginIgnore
    @ApiOperation("登录")
    @PostMapping("/login")
    public CommonResult<UserAuthVo> login(@RequestBody LoginQO qo) {
        ValidatorUtils.validateEntity(qo);
        if (StrUtil.isEmpty(qo.getCode()) && StrUtil.isEmpty(qo.getPassword())) {
            throw new ProprietorException("验证码和密码不能同时为空");
        }
        log.info(qo.getAccount() + "开始登录");
        return CommonResult.ok(userService.queryUserInfoV2(qo));
    }

    /*@ApiOperation("登录")
    @PostMapping("/login")
    public CommonResult<UserAuthVo> login(@RequestBody LoginQO qo) {
        ValidatorUtils.validateEntity(qo);
        if (StrUtil.isEmpty(qo.getCode()) && StrUtil.isEmpty(qo.getPassword())) {
            throw new ProprietorException("验证码和密码不能同时为空");
        }
        log.info(qo.getAccount() + "开始登录");
        UserInfoVo infoVo = userService.login(qo);
        infoVo.setIdCard(null);
        //生成带token和用户信息的的UserAuthVo
        UserAuthVo userAuthVo = userService.createAuthVoWithToken(infoVo);
        return CommonResult.ok(userAuthVo);
    }*/

    @LoginIgnore
    @ApiOperation("三方登录 - 获取支付宝authInfo")
    @GetMapping("/third/authInfo")
    public CommonResult getAuthInfo() {
        String targetID = "zhsjCommunity";
//		targetID = UUID.randomUUID().toString().replace("-","");
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap("2088041379474034", "2021002119679359", targetID, true);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCB3eUTPKZwI9ySgN4xHNRpxdN1npXkHLfacxqbaHKxxLPeebKf15u68nLnwkEgsSOYIbffy/newdCv8nWzLthkSrI8s/9LXQ4BLH3DKzi1tiwoBE49gsJscGBUkTa/XeJmzpn1EVViE5pCPPjRP6E9DXC/XTXI/hW75QG4RM9l0unOoqDju9iQqKs/dzRqChJL+w3krDvWdnkAp9ODh2YLigAT6bOBl/u18rYVHoG9jCP539bIKYPYPR65YFv2qPODFZPd0CjNCNWHEW0iFSompGqfdICJ7bJDLEalSEY2ldMEo+0Lhd8xwnh3rjkCEMjlphKC8JLDTzUn/ZpxGRpVAgMBAAECggEAEzci6vrBYbxcqay5s6ihYVktFa02Xn9FUORRHoeQ2O7S8mstW+tKFppDGDms60pqitsKWunSefxRhYcplS6sRAKtZr/3WU5WgSP1I8ikZnajB/TDIuTTIbpq9nhaEmyw0CdnrlQ3PwDJhV2CL2vrzp3LffUrvSajTp7u5zX/PgCGMRgZaHsIBKbXX75FotJn6tpVaCOr07imoqMtMOxO+TVe2wuEKiMm74goPmUzWIFY3SaO8Ro2fzW2bjAES/AcYLw9zVPn0JjjWsltbAznxYC66FCG/nhcG5lFdr9sZW0Y9txyOKXHiMZZ34KSm+1zZb5+1ZT9s6ufjnFt9Ni4TQKBgQC6eDzUzw01mkpIV5iZPDllcObhyRbd0Zh1NEfhfsqR4VxPV66TBy/eFS2ARW+VrpH5wJJLAL5gEaB9C1NJvV/00v25xr4i0232Vs9FAJirNw1Xsw4u8l+3mqBrfJkKWcKs+1cUH9dHenjortLZKYFCzw2cLAVx4R9SdksICV3AFwKBgQCySolRdtoT4hjmrb8MWlnG4Rdi4AXv+UqdW+qF6IC+yyf1u9M81z8xpcHqh8+croUf1eVlWYL2iqbv4Qs8n4wzYttoh2+o+zJh2awlnDX8gnCLrOw8wzXyUS3RY/SOZIijQJIeWZW4GBKNs+I1dftVHHxN/5eXdFrVCyrQHvSwcwKBgGNpFK0zkUxBbFay9HTFKahOD5jRtvIc+pWJgMTT7rTlG2xlR3m0/Cz+x1o6Kmn3PnWS16tKwzO+Ufw4HHgUkOKZ1ZDERruUUmxhDXExBzNIT0GxAN/AcY0Vz25eZ8yf2+yStnLRItlFjs4l3dzOhs4SSqQ2x9RVe3hf/lJTg/qTAoGAE564NcrKfN2ot9nu6EEZGBW1KSBWBu58E40F5e6MHHLm1tfwiwV04tXG3TRM2IUsCTDUqa6MBu2DKWqufeFg4FbEpmAhHYtnI/V8SDdEiaEhGX3SEW26BgyA6kYBp5nQn4Z/je911mhvwkBFaHSvT9Juq3axC+22ATPVZknBy5kCgYBVZjVl4mqJqgvvEPo12dwBQqBjoCwoT3xongYDnMzso7GYL2oRL6o43Utp+wxdE7OY2S+54rja6u9BVDNW9Rv+GGohbfRr6LQFytEjdAoCgoxjSGI5v+Ox6MF7sK72TVum5lnUWRjV1Bf4t3s9UuNjYuiLoxXbDJJNFG2yIv1zDA==", true);
        final String authInfo = info + "&" + sign;
        return CommonResult.ok(authInfo, "获取成功");
    }

    @LoginIgnore
    @ApiOperation("三方登录")
    @PostMapping("/third/login")
    public CommonResult thirdPlatformLogin(@RequestBody UserThirdPlatformQO userThirdPlatformQO) {
        ValidatorUtils.validateEntity(userThirdPlatformQO);
        if (StringUtils.isEmpty(userThirdPlatformQO.getAccessToken())
                && StringUtils.isEmpty(userThirdPlatformQO.getAuthCode())) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "accessToken和authCode不允许同时为空");
        }
        return CommonResult.ok(userService.thirdPlatformLogin(userThirdPlatformQO));
    }

    @LoginIgnore
    @ApiOperation("三方平台绑定手机")
    @PostMapping("/third/binding")
    public CommonResult bindingThirdPlatform(@RequestBody UserThirdPlatformQO userThirdPlatformQO) {
        ValidatorUtils.validateEntity(userThirdPlatformQO);
        if (StringUtils.isEmpty(userThirdPlatformQO.getThirdPlatformId())) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "id不允许为空");
        }
//		if(StringUtils.isEmpty(userThirdPlatformQO.getAccessToken())
//			&& StringUtils.isEmpty(userThirdPlatformQO.getAuthCode())){
//			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"accessToken和authCode不允许同时为空");
//		}
        if (StringUtils.isEmpty(userThirdPlatformQO.getMobile())
                || StringUtils.isEmpty(userThirdPlatformQO.getCode())) {
            return CommonResult.error(JSYError.REQUEST_PARAM.getCode(), "手机和验证码不能为空");
        }
        return CommonResult.ok(userService.bindThirdPlatform(userThirdPlatformQO), "绑定成功");
    }

    @ApiOperation(value = "注册后设置密码", notes = "需要登录")
    @PostMapping("/password")
    @Permit("community:proprietor:user:auth:password")
    public CommonResult<Boolean> addPassword(@RequestBody AddPasswordQO qo) {

        ValidatorUtils.validateEntity(qo, AddPasswordQO.passwordVGroup.class);
        String uid = UserUtils.getUserId();
        boolean b = userAuthService.addPassword(uid, qo);
        return b ? CommonResult.ok() : CommonResult.error("密码设置失败");
    }

    @ApiOperation("绑定微信")
    @PostMapping("/bindingWechat")
    @Permit("community:proprietor:user:auth:bindingWechat")
    public CommonResult bindingWechat(@RequestParam("code") String code) {
        JSONObject object = WeCharUtil.getAccessToken(code);
        if ("".equals(object) || object == null) {
            return CommonResult.error("系统异常，请稍后再试！");
        }
        String accessToken = object.getString("access_token");
        String openid = object.getString("openid");
        JSONObject jsonObject = WeCharUtil.getUserInfo(accessToken, openid);
        userService.bindingWechat(UserUtils.getUserId(), openid);
        return CommonResult.ok(jsonObject.getString("nickname"), "绑定成功");
    }

    @ApiOperation("解绑微信绑定")
    @PostMapping("/relieveBindingWechat")
    @Permit("community:proprietor:user:auth:relieveBindingWechat")
    public CommonResult relieveBindingWechat(@RequestBody RegisterQO registerQO) {
        commonService.checkVerifyCode(registerQO.getAccount(), registerQO.getCode());
        userService.relieveBindingWechat(registerQO, UserUtils.getUserId());
        return CommonResult.ok();
    }

    @ApiOperation(value = "设置支付密码", notes = "需要登录")
    @PostMapping("/password/pay")
    @Permit("community:proprietor:user:auth:password:pay")
    public CommonResult<Boolean> addPayPassword(@RequestBody AddPasswordQO qo) {
        //todo 个人觉得明文传递支付密码有问题
        ValidatorUtils.validateEntity(qo, AddPasswordQO.payPasswordVGroup.class);
        String uid = UserUtils.getUserId();
        boolean b = userAuthService.addPayPassword(uid, qo);
        return b ? CommonResult.ok() : CommonResult.error("支付密码设置失败");
    }

    @LoginIgnore
    @ApiOperation(value = "敏感操作短信验证", notes = "忘记密码等")
    @GetMapping("/check/code")
    public CommonResult<Map<String, Object>> checkCode(@RequestParam String account, @RequestParam String code) {
        commonService.checkVerifyCode(account, code);
        String token = UserUtils.setRedisTokenWithTime("Auth", account, 1, TimeUnit.HOURS);
        Map<String, Object> map = new HashMap<>();
        map.put("authToken", token);
        map.put("msg", "验证通过，请在1小时内完成操作");
        return CommonResult.ok(map);
    }

    @LoginIgnore
    @ApiOperation("重置密码")
    @PostMapping("/reset/password")
    @Auth
    public CommonResult<Boolean> resetPassword(@RequestAttribute(value = "body") String body) {
        ResetPasswordQO qo = JSONObject.parseObject(body, ResetPasswordQO.class);
        ValidatorUtils.validateEntity(qo, ResetPasswordQO.forgetPassVGroup.class);
        if (!qo.getPassword().equals(qo.getConfirmPassword())) {
            throw new JSYException("两次密码不一致");
        }
        boolean b = userAuthService.resetPassword(qo);
        if (b) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authToken = request.getHeader("authToken");
            if (StrUtil.isBlank(authToken)) {
                authToken = request.getParameter("authToken");
            }
            //销毁Auth token
            UserUtils.destroyToken("Auth", authToken);
        }
        return b ? CommonResult.ok() : CommonResult.error("重置失败");
    }

    @ApiOperation("发送修改支付密码的手机验证码")
    @GetMapping("/send/password/pay/code")
    @Permit("community:proprietor:user:auth:send:password:pay:code")
    public CommonResult<Boolean> sendPayPasswordVerificationCode() {
        String mobile = UserUtils.getUserInfo().getMobile();
        userAuthService.sendPayPasswordVerificationCode(mobile);
        return CommonResult.ok();
    }

    @ApiOperation("手机验证码验证，更换支付密码")
    @PostMapping("/check/password/pay/code")
    @Permit("community:proprietor:user:auth:check:password:pay:code")
    public CommonResult<Boolean> updatePayPasswordByMobileCode(@RequestBody MobileCodePayPasswordQO qo) {
        String mobile = UserUtils.getUserInfo().getMobile();
        String userId = UserUtils.getUserId();
        userAuthService.updatePayPasswordByMobileCode(qo, mobile, userId);
        return CommonResult.ok();
    }

//	// @Login是旧手机 @Auth是验证新手机
//	@ApiOperation("更换手机号(旧手机在线)")
//	@PutMapping("/reset/mobile")
//	@Login
//	public CommonResult changeMobile(@RequestBody Map<String,String> map){
//		//入参验证
//		String newMobile = map.get("account");
//		if(StringUtils.isEmpty(newMobile)){
//			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请填写新手机号");
//		}
//		if (!RegexUtils.isMobile(newMobile)) {
//			throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请检查手机号格式是否正确");
//		}
//		//权限验证
//		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//		String authToken = request.getHeader("authToken");
//		if (StrUtil.isBlank(authToken)) {
//			authToken = request.getParameter("authToken");
//		}
//		Object authTokenContent = UserUtils.getRedisToken("Auth", authToken);
//		if(authTokenContent == null){
//			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "操作未被授权");
//		}
//		String uid = UserUtils.getUserId();
//		String oldMobile = userAuthService.selectContactById(uid);
//		if(oldMobile == null || !String.valueOf(authTokenContent).equals(oldMobile)){
//			throw new JSYException(JSYError.UNAUTHORIZED.getCode(), "操作未被授权");
//		}
//		//用户是否已注册
//		boolean exists = userAuthService.checkUserExists(newMobile, "mobile");
//		if(exists){
//			return CommonResult.error(JSYError.DUPLICATE_KEY.getCode(),"手机号已被注册，请直接登录或找回密码");
//		}
//		//更换手机号操作
//		boolean b = userAuthService.changeMobile(newMobile, uid);
//		if(b){
//			//销毁Auth token
//			UserUtils.destroyToken("Auth", authToken);
//			//销毁token
//			String token = request.getHeader("token");
//			if (StrUtil.isBlank(token)) {
//				token = request.getParameter("token");
//			}
//			UserUtils.destroyToken("Login", token);
//		}
//		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL);
//	}

    @ApiOperation("更换手机号(旧手机在线)")
    @PutMapping("/reset/mobile")
    @Permit("community:proprietor:user:auth:reset:mobile")
    public CommonResult changeMobile(@RequestBody Map<String, String> map) {
        //入参验证
        String newMobile = map.get("account");
        if (StringUtils.isEmpty(newMobile)) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请填写新手机号");
        }
        if (!RegexUtils.isMobile(newMobile)) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "请检查手机号格式是否正确");
        }
        String code = map.get("code");
        if (StrUtil.isEmpty(map.get("code"))) {
            throw new ProprietorException("验证码不能为空");
        }
        //权限验证
        commonService.checkVerifyCode(newMobile, code);
        //从请求获取uid
        String uid = UserUtils.getUserId();
        //用户是否已注册
        boolean exists = userAuthService.checkUserExists(newMobile, "mobile");
        if (exists) {
            return CommonResult.error(JSYError.DUPLICATE_KEY.getCode(), "手机号已被注册，请直接登录或找回密码");
        }
        //更换手机号操作
        userAuthService.changeMobile(newMobile, uid);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //销毁token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        UserUtils.destroyToken("Login", token);
        return CommonResult.ok("操作成功");
    }

    //TODO 待定-手机丢失更换新手机(旧手机不在线)

}
