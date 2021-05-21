package com.jsy.community.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 修改银联支付密码接参
 * @Date: 2021/4/15 11:27
 * @Version: 1.0
 **/
@Data
public class ModifyPwdQO implements Serializable {

    // 商户订单号
    private String mctOrderNo;

    // 钱包ID-必填
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    // 原支付密码-必填
    @NotBlank(message = "原支付密码不能为空")
    private String encryptOrigPwd;

    // 新支付密码-必填
    @NotBlank(message = "新支付密码不能为空")
    private String encryptNewPwd;

    // 加密类型-必填
    @Range(min = 1, max = 2, message = "加密类型取值范围为1或2,1：H5密码键盘加密,2：非H5加密。")
    @NotBlank(message = "加密类型不能为空")
    private String encryptType;

    // 控件随机因子-必填
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    // 备注-选填
    private String remark;
}
