package com.jsy.community.qo.proprietor;

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

    @ApiModelProperty(value = "账单类型，1,生活日用，2饮食，3交通出行，4文教娱乐，5服饰美容，6运动健康，7住房缴费，8通讯缴费，9其他消费")
    private Integer billClassification;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注图片")
    private String remarkImg;
}
