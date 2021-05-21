package com.jsy.community.qo;

import com.jsy.community.utils.RegexUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联C端开户请求参数
 * @Date: 2021/4/8 9:56
 * @Version: 1.0
 **/
@Data
public class OpenAccountForCQO implements Serializable {

    // 商户订单号
    private String mctOrderNo;

    // 管理类型
    private String relType;

    // 外部用户ID
    private String extUserId;

    // 用户姓名-必填
    @NotBlank(message = "用户姓名不能为空,且应与银行账户户名一致.")
    @Length(min = 2, max = 20, message = "用户姓名长度为2-20个字符")
    private String userName;

    // 手机号-必填
    @Pattern(regexp = RegexUtils.REGEX_MOBILE, message = "手机号格式错误")
    @NotBlank(message = "手机号不能为空")
    private String mobileNo;

    // 身份证-必填
    @Pattern(regexp = RegexUtils.REGEX_ID_CARD, message = "身份证格式错误")
    @NotBlank(message = "身份证不能为空")
    private String idCard;

    // 身份证开始日期
    private String idCardStartDate;

    // 身份证结束日期
    private String idCardEndDate;

    // 认证类型-必填
    // 0：姓名身份证2要素
    // 1：银行卡3要素
    // 2：运营商3要素
    @Range(min = 0, max = 2, message = "认证类型超出范围,0：姓名身份证2要素,1：银行卡3要素,2：运营商3要素")
    @NotNull(message = "认证类型不能为空")
    private Integer authType;

    // 银行账号-条件必填
    // 使用银行卡3要素必填
    private String bankAcctNo;

    // 是否激活-必填
    // 0-不激活
    // 1-激活
    // 默认1-激活
    private Integer isActive;

    // 密码密文-条件必填
    // 当IsActive（是否激活）为1时，该字段才有意义。
    // 密码密文：
    // 使用encryptType指定的方式进行加密（最终密文是base64编码的字符串）。
    @NotBlank(message = "密码密文不能为空")
    private String encryptPwd;

    // 加密类型-条件必填
    // 当IsActive（是否激活）为1时，该字段才有意义。
    // 支付密码加密类型：
    // 1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）。
    // 2：非H5加密。（加密控件先使用公钥加密，然后控件自身再加密）。
    @Range(min = 1, max = 2, message = "加密类型超出范围,1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）, 2：非H5加密(PC)。（加密控件先使用公钥加密，然后控件自身再加密）")
    @NotNull(message = "加密类型不能为空")
    private Integer encryptType;

    // 控件随机因子-条件必填
    // 加密随机因子，通过“获取控件随机因子”接口获取，有效期为24小时
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    // 用户联系电话-选填
    private String userTelNo;

    // 用户电子邮箱-选填
    @Pattern(regexp = RegexUtils.REGEX_EMAIL, message = "电子邮箱格式错误")
    private String userEmail;

    // 用户地址-选填
    private String userAddr;

    // 性别-选填
    // M：男
    // F：女
    private String userSex;

    // 备注-选填
    private String remark;

    // 手机短信验证码-条件必填
    private String smsAuthCode;

    // 国籍
    private String nationlity;

    // 紧急联系人
    private String emergencyContact;

    // 注册IP
    private String regIp;

    // 注册mac
    private String regMac;

    // 注册设备名称
    private String regDevName;
}
