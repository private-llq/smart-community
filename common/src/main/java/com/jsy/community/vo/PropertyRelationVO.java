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
    @ApiModelProperty("楼栋名")
    private String building;
    @ApiModelProperty("单元名")
    private String unit;
    @ApiModelProperty("门牌")
    private String floor;
    @ApiModelProperty("房屋类型")
    private Integer houseType;
    @ApiModelProperty("房屋类型名称")
    private String houseTypeName;
    @ApiModelProperty("和业主关系")
    private Integer relation;
    @ApiModelProperty("和业主关系")
    private String relationName;
    @ApiModelProperty("家属名称")
    private String memberName;
    @ApiModelProperty("家属电话")
    private String mobile;
    @ApiModelProperty("家属身份证")
    private String idCard;
    @ApiModelProperty("业主")
    private String owner;
    @ApiModelProperty("业主名称")
    private String ownerName;
    @ApiModelProperty("业主电话")
    private String ownerMobile;
    @ApiModelProperty("业主身份证")
    private String ownerIdCard;
}
