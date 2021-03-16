package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 13:42
 **/
@Data
public class PropertyRelationQO implements Serializable {
    @ApiModelProperty("社区id")
    private Long communityId;
    @ApiModelProperty("租户或者家属名称模糊查询")
    private String name;
    @ApiModelProperty("业主名称模糊查询")
    private String ownerName;
    @ApiModelProperty("房屋id")
    private Long houseId;
    @ApiModelProperty("楼栋id")
    private Long buildingId;
    @ApiModelProperty("单元id")
    private Long unitId;
}
