package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-25 14:53
 **/
@Data
@ApiModel(value="物业车辆查询")
public class ElasticsearchCarSearchQO implements Serializable {
    private String carPlate;
    @ApiModelProperty(value = "车辆类型： 1.微型车 2.小型车 3.紧凑型车 4.中型车 5.中大型车")
    private Integer carType;
    @ApiModelProperty("真实姓名")
    private String owner;
}
