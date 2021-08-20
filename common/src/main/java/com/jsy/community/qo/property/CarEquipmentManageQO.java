package com.jsy.community.qo.property;

import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("设备管理-分页搜索参数")
public class CarEquipmentManageQO extends BaseQO<CarEquipmentManageEntity> implements Serializable {
    @ApiModelProperty("设备名称")
    private  String equipmentName;

    @ApiModelProperty(value = "社区id")
    private  Long communityId;
}
