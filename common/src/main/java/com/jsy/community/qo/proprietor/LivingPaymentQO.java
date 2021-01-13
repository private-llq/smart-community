package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-11 09:50
 **/
@Data
@ApiModel("生活缴费接收参数对象")
public class LivingPaymentQO implements Serializable {
    @ApiModelProperty(value = "缴费类型,如水电气")
    private Long type;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "户号")
    private String doorNo;

    @ApiModelProperty(value = "缴费单位")
    private String PayCostUnit;

    @ApiModelProperty(value = "缴费单位ID")
    private Long PayCostUnitId;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String userID;

    @ApiModelProperty(value = "付款方式,1银行卡，2微信支付，3支付宝支付")
    private Integer payTpye;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal payBalance;

    @ApiModelProperty(value = "付款金额")
    private BigDecimal payNum;

    @ApiModelProperty(value = "缴费地址")
    private String address;





}
