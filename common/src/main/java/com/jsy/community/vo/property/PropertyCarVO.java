package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-22 16:04
 **/
@Data
@ApiModel("物业车辆信息")
public class PropertyCarVO {

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
    @ApiModelProperty("车主身份")
    private String ownerIdentity;
    @ApiModelProperty("车主姓名")
    private String owner;
    @ApiModelProperty("车主电话")
    private String mobile;
    @ApiModelProperty("车主身份证")
    private String idCard;
    @ApiModelProperty("车辆类型")
    private String carType;
    @ApiModelProperty("车辆类型名称")
    private String carTypeName;
    @ApiModelProperty("创建时间")
    private String createTime;





}
