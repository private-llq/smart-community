package com.jsy.community.qo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author DKS
 * @since 2021-10-26 10:32
 */
@Data
@ApiModel("车辆接收参数对象")
public class CarQO implements Serializable {

    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "姓名或电话或车牌")
    private String nameOrMobileOrCarNumber;
}
