package com.jsy.community.qo.payment.UnionPay;

import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 账户绑定/解绑/设置默认银行卡传参
 * @Date: 2021/4/12 14:45
 * @Version: 1.0
 **/
@Data
@ApiModel(value = "账户绑定/解绑/设置默认银行卡接参")
public class BindBankCardQO implements Serializable {

    // 钱包ID-必填
    @ApiModelProperty("钱包ID")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 密码密文-条件必填
    // 密码密文：
    // 使用encryptType指定的方式进行加密（最终密文是base64编码的字符串）。
    @ApiModelProperty("密码密文-必填")
    @NotBlank(message = "密码密文不能为空")
    private String encryptPwd;

    // 加密类型-条件必填
    // 支付密码加密类型：
    // 1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）。
    // 2：非H5加密。（加密控件先使用公钥加密，然后控件自身再加密）。
    @ApiModelProperty("加密类型-必填")
    @Range(min = 1, max = 2, message = "加密类型超出范围,1：H5密码键盘加密（密码键盘先使用公钥加密，然后自身再加密）, 2：非H5加密(PC)。（加密控件先使用公钥加密，然后控件自身再加密）")
    @NotNull(message = "加密类型不能为空")
    private Integer encryptType;

    // 控件随机因子-条件必填
    // 加密随机因子，通过“获取控件随机因子”接口获取，有效期为24小时
    @ApiModelProperty("控件随机因子-必填")
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    @ApiModelProperty("证书签名密文")
    private String certSign;

    // 手机号-条件必填
    // 绑定时必填
    @ApiModelProperty("手机号")
    @Pattern(regexp = RegexUtils.REGEX_MOBILE, message = "手机号格式错误")
    private String mobileNo;

    // 身份证-条件必填
    // 绑定时必填
    @ApiModelProperty("身份证")
    @Pattern(regexp = RegexUtils.REGEX_ID_CARD, message = "身份证格式错误")
    private String idCard;

    // 银行账号-必填
    @ApiModelProperty("银行账号")
    private String bankAcctNo;

    // 绑定时必填，必须与用户姓名保持一致。
    @ApiModelProperty("银行账户户名")
    private String bankAcctName;

    // 绑定时必填,3位或8位的开户行号
    @ApiModelProperty("开户行号")
    private String bankNo;

    @ApiModelProperty("开户行名称")
    private String bankName;

    // 绑定时必填
    @ApiModelProperty("电子联行号")
    private String elecBankNo;

    @ApiModelProperty("开户支行名称")
    private String elecBankName;

    @ApiModelProperty("开户支行银行地区码")
    private String bankAreaCode;

    @ApiModelProperty("开户支行所在省")
    private String elecBankProvince;

    @ApiModelProperty("开户支行所在市")
    private String elecBankCity;

    // 绑定时必填
    // 0：对公银行账户；
    // 1：对私银行卡；
    // 默认1（对私银行卡）。
    @ApiModelProperty("银行账户类型")
    @Range(min = 0, max = 1, message = "银行账户类型超出范围,0：对公银行账户,1：对私银行卡")
    private Integer bankAcctType;

    @ApiModelProperty("手机短信验证码")
    private String smsAuthCode;

    // 操作方式-必填
    // 1-绑定；
    // 2-绑定并设置为默认卡；
    // 3-解绑；
    // 4-设置默认卡；
    @ApiModelProperty("操作方式")
    @Range(min = 1, max = 4, message = "操作方式超出范围,1-绑定；2-绑定并设置为默认卡；3-解绑；4-设置默认卡；")
    @NotNull(message = "操作方式不能为空")
    private Integer oprtType;
}
