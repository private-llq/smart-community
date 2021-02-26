package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description: 账单详情
 * @author: Hu
 * @create: 2021-02-20 16:06
 **/
@Data
public class TheBillingDetailsVO implements Serializable {
    @ApiModelProperty(value = "订单id")
    private Long id;
    @ApiModelProperty(value = "付款方式")
    private Integer payType;
    @ApiModelProperty(value = "缴费类型名称")
    private String typeName;
    @ApiModelProperty(value = "小图片")
    private String icon;
    @ApiModelProperty(value = "中图片")
    private String mediumIcon;
    @ApiModelProperty(value = "大图片")
    private String largeSizeIcon;
    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;
    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paymentBalance;
    @ApiModelProperty(value = "缴费状态 0 未到账 1处理中，2已到账")
    private Integer status;
    @ApiModelProperty(value = "户号")
    private String familyId;
    @ApiModelProperty(value = "订单流水号")
    private String orderNum;
    @ApiModelProperty(value = "账单分类")
    private String billClassification;
    @ApiModelProperty(value = "标签")
    private String tally;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "备注图片")
    private String remarkImg;


}
