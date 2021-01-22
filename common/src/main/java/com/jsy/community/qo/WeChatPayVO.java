package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-01-22 14:12
 **/
@Data
public class WeChatPayVO {
    @ApiModelProperty("支付描述")
    private String description;
    @ApiModelProperty("支付金额")
    private BigDecimal total;
}
