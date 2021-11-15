package com.jsy.community.qo.unionpay;

import com.jsy.community.utils.RegexUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 修改用户手机号接参
 * @Date: 2021/4/14 9:25
 * @Version: 1.0
 **/
@Data
public class ModifyUserMobileQO implements Serializable {

    // 商户订单号
    private String mctOrderNo;

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 支付密码密文-必填
    @NotBlank(message = "支付密码密文不能为空")
    private String encryptPwd;

    // 加密类型-必填
    @Range(min = 1, max = 2, message = "加密类型超出范围,1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）, 2：非H5加密(PC)。（加密控件先使用公钥加密，然后控件自身再加密）")
    @NotNull(message = "加密类型不能为空")
    private Integer encryptType;

    // 控件随机因子-必填
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    // 证书签名密文-选填
    private String certSign;

    // 手机号-必填
    @Pattern(regexp = RegexUtils.REGEX_MOBILE, message = "手机号格式错误")
    @NotBlank(message = "手机号不能为空")
    private String mobileNo;

    // 短信验证码-必填
    @NotBlank(message = "短信验证码不能为空")
    private String smsAuthCode;

    // 备注-选填
    private String remark;
}
