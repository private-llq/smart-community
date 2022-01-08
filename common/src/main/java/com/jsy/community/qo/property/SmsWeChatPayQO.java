package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 短信微信支付
 * @author: DKS
 * @since: 2021/12/11 14:33
 */
@Data
public class SmsWeChatPayQO {
    @ApiModelProperty(value = "短信条数")
    private Integer number;
    
    @ApiModelProperty("支付金额(分)")
    private String amount;
}
