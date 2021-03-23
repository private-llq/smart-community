package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 16:02
 **/
@Data
@ApiModel("分页查询社区趣事")
public class PropertyCarQO implements Serializable {
    @ApiModelProperty(value = "车辆牌照")
    private String carPlate;

    @ApiModelProperty(value = "车主姓名")
    private String name;

    @ApiModelProperty(value = "车辆类型")
    private String type;
}
