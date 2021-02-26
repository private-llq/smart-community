package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 订单实体类
 * @author: Hu
 * @since: 2021/2/26 11:29
 * @Param:
 * @return:
 */
@Data
@TableName("t_pay_order")
@ApiModel(value = "PayOrder对象", description = "订单表")
public class PayOrderEntity extends BaseEntity {

    @ApiModelProperty(value = "订单流水号")
    private String orderNum;

    @ApiModelProperty(value = "缴费类型，如水电气")
    private Long typeId;

    @ApiModelProperty(value = "缴费单位ID")
    private Long companyId;

    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;

    @ApiModelProperty(value = "户主余额")
    private BigDecimal accountBalance;

    @ApiModelProperty(value = "缴费金额")
    private BigDecimal paymentBalance;

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "户名")
    private String familyName;

    @ApiModelProperty(value = "缴费地址")
    private String address;

    @ApiModelProperty(value = "付款方式，1微信支付，2支付宝支付，3账户余额，4其他银行卡")
    private Integer payType;

    @ApiModelProperty(value = "付款方式名称")
    private String payTypeName;

    @ApiModelProperty(value = "付款年份")
    private Integer payYear;

    @ApiModelProperty(value = "付款月份")
    private Integer payMonth;

    @ApiModelProperty(value = "下单时间")
    private LocalDateTime orderTime;

    @ApiModelProperty(value = "到账时间")
    private LocalDateTime arriveTime;

    @ApiModelProperty(value = "分组ID")
    private Long groupId;

    @ApiModelProperty(value = "缴费状态 0 未到账 1已到账")
    private Integer status;






    @ApiModelProperty(value = "账单类型，1充值缴费")
    private Integer billClassification;

    @ApiModelProperty(value = "账单类型名称")
    private String billClassificationName;

    @ApiModelProperty(value = "标签")
    private String tally;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注图片")
    private String remarkImg;
}
