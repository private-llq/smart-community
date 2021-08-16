package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;


@Data
@ApiModel("基础设备-临时车规则")
public class CarBasicsRuleQO implements Serializable {
    @ApiModelProperty(value = "停留时间(分钟)")
    private Integer dwellTime;

    @ApiModelProperty(value = "临时车最大入场数（辆）")
    private Integer maxNumber;

    @ApiModelProperty(value = "临时车入场规则 0：选择 1：不选")
    private Integer rule;
}
