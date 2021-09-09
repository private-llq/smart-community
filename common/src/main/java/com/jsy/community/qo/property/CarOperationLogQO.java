package com.jsy.community.qo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@ApiModel("车辆日志返回对象")
public class CarOperationLogQO implements Serializable {
    @ApiModelProperty(value = "页码")
    private Integer page;
    @ApiModelProperty(value = "每页数量")
    private Integer size;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") //Json才需要时区
    @ApiModelProperty(value = "时间")
    private LocalDate time;
    @ApiModelProperty(value = "角色")
    private String userRole;
    @ApiModelProperty(value = "车牌号")
    private String carNumber;


}
