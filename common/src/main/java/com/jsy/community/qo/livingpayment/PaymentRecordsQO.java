package com.jsy.community.qo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-11 13:48
 **/
@Data
@ApiModel("查询缴费记录接受参数对象")
public class PaymentRecordsQO implements Serializable {

    @ApiModelProperty(value = "户号")
    private String familyId;

    @ApiModelProperty(value = "付款年份")
    private Integer payYear;

    @ApiModelProperty(value = "付款月份")
    private Integer payMonth;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String userID;
}
