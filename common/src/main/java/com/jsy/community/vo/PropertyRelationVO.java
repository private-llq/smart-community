package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 13:56
 **/
@Data
public class PropertyRelationVO implements Serializable {
    @ApiModelProperty("房屋")
    private String housing;
    @ApiModelProperty("业主名称模糊查询")
    private String relation;
    @ApiModelProperty("家属名称")
    private String memberName;
//    @ApiModelProperty("家属名称")
//    private String memberName;
//    @ApiModelProperty("家属名称")
//    private String memberName;

    @ApiModelProperty("业主名称")
    private String ownerName;
    @ApiModelProperty("房屋id")
    private String houseId;
    @ApiModelProperty("楼栋id")
    private String buildingId;
    @ApiModelProperty("单元id")
    private String unitId;
}
