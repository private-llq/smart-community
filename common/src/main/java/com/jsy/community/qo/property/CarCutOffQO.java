package com.jsy.community.qo.property;

import com.jsy.community.entity.property.CarCutOffEntity;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("开闸记录-高级选项")
public class CarCutOffQO extends BaseQO implements Serializable {

    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "车辆类型")
    private String carType;

    @ApiModelProperty(value = "进出方向")
    private String access;

}
