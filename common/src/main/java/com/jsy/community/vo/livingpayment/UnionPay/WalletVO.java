package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 钱包ID返参
 * @Date: 2021/5/10 9:12
 * @Version: 1.0
 **/
@Data
@ApiModel("钱包ID返参")
public class WalletVO implements Serializable {

    @ApiModelProperty("钱包ID")
    private String walletId;

    @ApiModelProperty("钱包名称")
    private String walletName;
}
