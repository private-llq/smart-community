package com.jsy.community.qo.sys;

import com.jsy.community.utils.RegexUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 发送短信接收请求对象
 * @author YuLF
 * @since 2021-02-25 17:07
 */
@Data
public class SmsQO {
    /**
     * 手机号码
     */
    @Pattern( groups = {SendSmsValid.class}, regexp = RegexUtils.REGEX_MOBILE, message = "手机号不正确!")
    @NotBlank( groups = {SendSmsValid.class}, message = "mobile 手机号是必须有的!")
    private String mobile;
    /**
     * 过期时间 /s
     */
    @NotNull( groups = {SendSmsValid.class}, message = "expire 过期时间是必须有的!")
    private Integer expire;
    /**
     * 验证码短信标题
     */
    @NotBlank( groups = {SendSmsValid.class}, message = " sign 验证码标题是必须有的!")
    private String  sign;

    /**
     * 发送短信验证
     */
    public interface SendSmsValid {}
}
