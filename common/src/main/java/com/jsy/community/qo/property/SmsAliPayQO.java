package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 短信支付宝支付
 * @author: DKS
 * @since: 2021/12/14 11:10
 */
@Data
public class SmsAliPayQO {
    @ApiModelProperty(value = "短信条数")
    private Integer subject;
    
    @ApiModelProperty("支付金额(分)")
    private String amount;
    
    @ApiModelProperty("支付金额(分)")
    private String outTradeNo;
}
