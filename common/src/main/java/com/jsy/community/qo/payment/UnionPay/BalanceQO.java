package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("银联余额查询接参")
public class BalanceQO implements Serializable {

    @ApiModelProperty("钱包ID-必填")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;

    @ApiModelProperty("是否输入密码-必填")
    @Range(min = 0, max = 1, message = "输入密码类型超出范围,0：不需要密码。1：需要密码。")
    @NotNull(message = "输入密码类型必填,0：不需要密码。1：需要密码。")
    private Integer isNeedPwd;

    @ApiModelProperty("支付密码密文")
    private String encryptPwd;

    @ApiModelProperty("加密类型,1:H5,2:非H5")
    private Integer encryptType;

    @ApiModelProperty("控件随机因子")
    private String plugRandomKey;

    @ApiModelProperty("证书签名密文")
    private String certSign;
}
