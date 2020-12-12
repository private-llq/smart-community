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
    @ApiModelProperty(value = "水电气类型，0水费，1电费，2燃气费")
    private Long type;

    @ApiModelProperty(value = "1我家，2父母，3房东，4朋友，5其他")
    private Integer typeGroup;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "户号")
    private String doorNo;

    @ApiModelProperty(value = "缴费单位")
    private String PayCostUnit;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String userID;

    @ApiModelProperty(value = "付款方式,1银行卡，2微信支付，3支付宝支付")
    private Integer payTpye;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal payBalance;

    @ApiModelProperty(value = "付款金额")
    private BigDecimal payNum;

    @ApiModelProperty(value = "订单号")
    private String orderNum;





}
