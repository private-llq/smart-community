package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: DKS
 * @create: 2021-08-12
 **/
@Data
public class PropertyAdvanceDepositRecordQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "预存款余额id")
    private Long advanceDepositId;
}
