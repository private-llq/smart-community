package com.jsy.community.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: B端钱包重置支付密码接参
 * @Date: 2021/5/11 17:40
 * @Version: 1.0
 **/
@Data
public class ResetBtypeAcctPwdQO implements Serializable {

    // 商户订单号-选填
    private String mctOrderNo;

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 新密码的密文-必填
    @NotBlank(message = "新密码的密文不能为空")
    private String encryptNewPwd;

    // 加密类型-必填
    @NotBlank(message = "加密类型不能为空")
    @Range(min = 1, max = 2, message = "加密类型取值范围为1或2, 1：H5密码键盘加密,2：非H5加密。")
    private String encryptType;

    // 控件随机因子-必填
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    // 法人代表姓名-必填
    @NotBlank(message = "法人代表姓名不能为空")
    private String legalName;

    // 法人代表身份证号码-必填
    @NotBlank(message = "法人代表身份证号码不能为空")
    private String legalIdCard;

    // 法人代表手机号码-必填
    @NotBlank(message = "法人代表手机号码不能为空")
    private String legalPhoneNum;

    // 法人代表短信验证码-必填
    @NotBlank(message = "法人代表短信验证码不能为空")
    private String legalSmsAuthCode;

    // 代理人姓名-条件必填
    private String agentName;

    // 代理人身份证号码-条件必填
    private String agentIdCard;

    // 代理人手机号-条件必填
    private String agentPhoneNum;

    // 代理人短信验证码-条件必填
    private String agentSmsAuthCode;

    // 备注-选填
    private String remark;
}
