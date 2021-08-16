package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("设备管理返回参数")
public class CarEquipmentManageVO implements Serializable {
    @ApiModelProperty("设备名称")
    private  String equipmentName;

    @ApiModelProperty("设备位置")
    private  String equipmentLocation;

    @ApiModelProperty("设备序列号")
    private  String equipmentNumber;

    @ApiModelProperty("设备状态 0：下线  1：上线")
    private Integer state;
}
