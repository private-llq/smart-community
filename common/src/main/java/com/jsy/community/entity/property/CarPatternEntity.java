package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("车禁-临时车模式下拉列表")
@TableName("t_car_equipment_pattern")
public class CarPatternEntity extends BaseEntity implements Serializable {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("模式名称")
    private String locationPattern;

    @ApiModelProperty("模式id")
    private String patternId;

    @ApiModelProperty("社区id")
    private Long communityId;

}
