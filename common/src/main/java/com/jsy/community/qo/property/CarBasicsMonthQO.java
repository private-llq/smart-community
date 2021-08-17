package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("车禁模块-包月选项")
@Data
public class CarBasicsMonthQO implements Serializable {
    @ApiModelProperty(value = "用户包月（0：不包月  1：包月）")
    private Integer monthlyPayment;

    @ApiModelProperty(value = "月租车续费最大时长（n个月）")
    private Integer monthMaxTime;

    @ApiModelProperty(value = "未缴物业费是否允许包月（0：不允许  1：允许）")
    private Integer whetherAllowMonth;

}
