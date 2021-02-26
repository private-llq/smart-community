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
    private Long id;

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

    @ApiModelProperty(value = "付款方式，1微信支付，2支付宝支付，3账户余额，4其他银行卡")
    private Integer payType;

    @ApiModelProperty(value = "住址信息")
    private String address;

    @ApiModelProperty(value = "账单类型，1,生活日用，2饮食，3交通出行，4文教娱乐，5服饰美容，6运动健康，7住房缴费，8通讯缴费，9其他消费")
    private Integer billClassification;

    @ApiModelProperty(value = "标签")
    private String tally;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注图片")
    private String remarkImg;

    @ApiModelProperty(value = "所缴费类型id")
    private String typeId;

    @ApiModelProperty(value = "所缴费用名称")
    private String typeName;

    @ApiModelProperty(value = "类型图片地址")
    private String icon;
    @ApiModelProperty(value = "中号图片地址")
    private String mediumIcon;
    @ApiModelProperty(value = "大号图片地址")
    private String largeSizeIcon;
    
    @ApiModelProperty(value = "时间组",hidden = true)
    private String timeGroup;

}
