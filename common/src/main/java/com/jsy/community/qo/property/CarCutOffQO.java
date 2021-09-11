package com.jsy.community.qo.property;

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

    @ApiModelProperty(value = "社区id")
    private Long communityId;

//    @ApiModelProperty(value = "车辆类型")
//    private String carType;

//    @ApiModelProperty(value = "进出方向")
//    private String access;

    @ApiModelProperty(value = "车辆所属类型  1-临时 2-包月  3-业主")
    private Integer belong;

    @ApiModelProperty(value = "0为未完成 在场车辆   1为完成 进出车辆")
    private Integer state;



}
