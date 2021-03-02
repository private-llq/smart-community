package com.jsy.community.vo.livingpayment;

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

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "年份")
    private Integer payYear;

    @ApiModelProperty(value = "月份")
    private Integer payMonth;

    @ApiModelProperty(value = "流水号")
    private String orderNum;

    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "缴费时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paymentBalance;

    @ApiModelProperty(value = "所缴费类型id")
    private String typeId;

    @ApiModelProperty(value = "所缴费用名称")
    private String typeName;

    @ApiModelProperty(value = "小号图片地址")
    private String icon;
    @ApiModelProperty(value = "中号图片地址")
    private String mediumIcon;
    @ApiModelProperty(value = "大号图片地址")
    private String largeSizeIcon;
    
    @ApiModelProperty(value = "时间组",hidden = true)
    private String timeGroup;

}
