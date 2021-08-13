package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: DKS
 * @create: 2021-08-11
 **/
@Data
public class PropertyDepositQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "关联类型（1.房屋2.车位）")
    private Long depositType;
    
    @ApiModelProperty(value = "关联目标（1.房屋id2.车位id）")
    private Long depositTargetId;
    
    @ApiModelProperty(value = "收费项目")
    private String payService;
}
