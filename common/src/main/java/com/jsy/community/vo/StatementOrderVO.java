package com.jsy.community.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author: Pipi
 * @Description: 结算单关联的账单返参
 * @Date: 2021/4/24 15:31
 * @Version: 1.0
 **/
@Data
public class StatementOrderVO implements Serializable {

    @ApiModelProperty("结算单号")
    private String statementNum;

    @ApiModelProperty("账单ID")
    private Long orderId;

    @ApiModelProperty(value = "账单号")
    private String orderNum;

    @ApiModelProperty(value = "应缴月份")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private LocalDate orderTime;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate payTime;

    @ApiModelProperty(value = "收款渠道1.支付宝 2.微信")
    private Integer transactionType;

    @ApiModelProperty(value = "收款单号")
    private String transactionNo;

    @ApiModelProperty("账单类型-冗余属性,给前端显示")
    private String orderType;
}
