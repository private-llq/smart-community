package com.jsy.community.qo.payment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 钱包ID接参
 * @Date: 2021/4/14 10:55
 * @Version: 1.0
 **/
@Data
@ApiModel("钱包ID接参")
public class WalletIdQO implements Serializable {

    @ApiModelProperty("钱包ID-必填")
    @NotBlank(message = "钱包ID不能为空")
    private String walletId;
}
