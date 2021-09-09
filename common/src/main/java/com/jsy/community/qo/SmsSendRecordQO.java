package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: DKS
 * @create: 2021-09-08
 **/
@Data
public class SmsSendRecordQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    
    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    
    @ApiModelProperty(value = "手机号")
    private String mobile;
    
    @ApiModelProperty(value = "状态1.成功2.失败")
    private Integer status;
}
