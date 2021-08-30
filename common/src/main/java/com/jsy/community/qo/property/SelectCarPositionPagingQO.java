package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分页查询车位信息")
public class SelectCarPositionPagingQO implements Serializable {
    @ApiModelProperty(value = "页码")
    private Integer page;
    @ApiModelProperty(value = "每页数量")
    private Integer size;
    @ApiModelProperty(value = "车位类型")
    private  Long   carPositionTypeId;
    @ApiModelProperty(value = "车位状态")
    private  Integer carPositionStatus;
    @ApiModelProperty(value = "绑定状态")
    private Integer  bindingStatus;
    @ApiModelProperty(value = "车位号")
    private String  carNumber;
}
