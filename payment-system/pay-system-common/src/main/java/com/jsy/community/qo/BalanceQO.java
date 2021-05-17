package com.jsy.community.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 银联余额查询接参
 * @Date: 2021/4/28 17:26
 * @Version: 1.0
 **/
@Data
public class BalanceQO implements Serializable {

    /**
     * 钱包ID-必填
     */
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    /**
     * 是否输入密码-必填
     */
    @Range(min = 0, max = 1, message = "输入密码类型超出范围,0：不需要密码。1：需要密码。")
    @NotNull(message = "输入密码类型必填,0：不需要密码。1：需要密码。")
    private Integer isNeedPwd;

    /**
     * 支付密码密文
     */
    private String encryptPwd;

    /**
     * 加密类型,1:H5,2:非H5
     */
    private Integer encryptType;

    /**
     * 控件随机因子
     */
    private String plugRandomKey;

    /**
     * 证书签名密文
     */
    private String certSign;
}
