package com.jsy.community.qo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 添加订单备注
 * @author: Hu
 * @create: 2020-12-14 10:45
 **/
@Data
@ApiModel("添加订单备注")
public class RemarkQO implements Serializable {


    @ApiModelProperty(value = "订单id")
    private Long id;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String uid;

    @ApiModelProperty(value = "账单类型，1充值缴费，暂时只有这个")
    private Integer billClassification;

    @ApiModelProperty(value = "账单类型，1充值缴费，暂时只有这个")
    private String billClassificationName;

    @ApiModelProperty(value = "标签")
    private String tally;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注图片")
    private String remarkImg;
}
