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

    @ApiModelProperty(value = "订单id")
    private Long id;

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

    @ApiModelProperty(value = "付款方式1银行卡，2微信支付，3支付宝支付")
    private Integer payType;


    @ApiModelProperty(value = "住址信息")
    private String address;

    @ApiModelProperty(value = "账单类型，1,生活日用，2饮食，3交通出行，4文教娱乐，5服饰美容，6运动健康，7住房缴费，8通讯缴费，9其他消费")
    private Integer billClassification;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注图片")
    private String remarkImg;

    @ApiModelProperty(value = "所缴费类型id")
    private String typeID;

    @ApiModelProperty(value = "所缴费用名称")
    private String typeName;
    
    @ApiModelProperty(value = "时间组",hidden = true)
    private String timeGroup;

}
