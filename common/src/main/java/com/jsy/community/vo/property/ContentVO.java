package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 统计返参明细
 * @Date: 2021/4/27 10:52
 * @Version: 1.0
 **/
@Data
@ApiModel("统计返参明细")
public class ContentVO implements Serializable {

    @ApiModelProperty("物业费")
    private List<BigDecimal> propertyCosts;

    @ApiModelProperty("停车费")
    private List<BigDecimal> parkingFee;

    @ApiModelProperty("水费")
    private List<BigDecimal> waterFee;

    @ApiModelProperty("已收金额")
    private List<BigDecimal> receivedAmount;

    @ApiModelProperty("欠费总金额")
    private List<BigDecimal> arrearsAmount;

    @ApiModelProperty("物业费欠收")
    private List<BigDecimal> arrearsPropertyAmount;

    @ApiModelProperty("车位费欠收")
    private List<BigDecimal> arrearsParkingFee;

    @ApiModelProperty("已结算金额")
    private List<BigDecimal> settledAmount;

    @ApiModelProperty("待结算金额")
    private List<BigDecimal> unsettlementAmount;

    @ApiModelProperty("总计")
    private List<BigDecimal> totalFee;
}
