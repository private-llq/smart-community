package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description: 查询每月返回记录
 * @author: Hu
 * @create: 2020-12-12 14:08
 **/
@Data
@ApiModel("查询每月返回记录")
public class PaymentRecordsVO implements Serializable {

    @ApiModelProperty(value = "年份")
    private Integer payYear;

    @ApiModelProperty(value = "月份")
    private Integer payMonth;

    @ApiModelProperty(value = "流水号")
    private String orderNum;

    @ApiModelProperty(value = "缴费单位名称")
    private String unitName;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paySum;

    @ApiModelProperty(value = "缴费类型0水费，1电费，2燃气费")
    private Integer payType;

    @ApiModelProperty(value = "住址信息")
    private String address;
}
