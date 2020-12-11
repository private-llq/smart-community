package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-11 14:21
 **/
@Data
@ApiModel("生活缴费组返回户号")
public class GroupVO implements Serializable {
    @ApiModelProperty(value = "水电气类型，0水费，1电费，2燃气费")
    private Integer type;

    @ApiModelProperty(value = "缴费单位")
    private String PayCostUnit;

    @ApiModelProperty(value = "户号")
    private String doorNo;
}
