package com.jsy.community.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 激活账户接参
 * @Date: 2021/5/12 17:10
 * @Version: 1.0
 **/
@Data
public class ActiveAcctQO implements Serializable {

    /**
     * 商户订单号
     */
    private String mctOrderNo;

    /**
     * 钱包ID
     */
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    /**
     * 密码密文
     */
    @NotBlank(message = "密码密文不能为空")
    private String encryptPwd;

    /**
     * 加密类型,1：H5密码,2：非H5加密
     */
    @NotBlank(message = "加密类型不能为空")
    @Range(min = 1, max = 2, message = "加密类型取值范围为1或2")
    private String encryptType;

    /**
     * 控件随机因子
     */
    @NotBlank(message = "控件随机因子不能为空")
    private String plugRandomKey;

    /**
     * 备注
     */
    private String remark;
}
