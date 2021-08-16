package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("车禁模块-设备管理接收参数")
@Data
public class CarEquipMentQO implements Serializable {
    @ApiModelProperty("设备名称")
    private  String equipmentName;

    @ApiModelProperty("设备序列号")
    private  String equipmentNumber;

    @ApiModelProperty("物联网卡号")
    private  String internetNumber;

    @ApiModelProperty("设备位置id")
    private  Long locationId;

    @ApiModelProperty("临时车模式")
    private  Long patternId;

}
