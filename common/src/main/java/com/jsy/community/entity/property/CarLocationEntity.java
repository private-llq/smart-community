package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("设备管理-设备位置")
@TableName("t_car_equipment_location")
public class CarLocationEntity implements Serializable {
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("设备位置")
    private String equipmentLocation;

    @ApiModelProperty("位置id")
    private Long locationId;

    @ApiModelProperty("社区id")
    private Long communityId;

}
