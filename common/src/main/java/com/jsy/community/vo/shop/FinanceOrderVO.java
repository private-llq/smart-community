package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Pipi
 * @Description: 物业财务-账单返参
 * @Date: 2021/4/23 16:49
 * @Version: 1.0
 **/
@Data
public class FinanceOrderVO implements Serializable {

    @ApiModelProperty("账单ID")
    private String id;

    @ApiModelProperty("账单号")
    private String orderNum;

    @ApiModelProperty("账单日期")
    private LocalDateTime orderTime;

    @ApiModelProperty("账单类型")
    private String orderType;

    @ApiModelProperty("收款金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("收款时间")
    private LocalDateTime payTime;

    @ApiModelProperty("收款渠道")
    private Integer transactionType;

    @ApiModelProperty("支付渠道单号")
    private String transactionNo;
}
