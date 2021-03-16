package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 17:19
 **/
@Data
public class RelationListQO implements Serializable {
    @ApiModelProperty("社区id")
    private Long communityId;
    @ApiModelProperty("名称模糊查询")
    private String name;
}
