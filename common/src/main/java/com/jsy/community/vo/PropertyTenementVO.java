package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 13:56
 **/
@Data
public class PropertyTenementVO implements Serializable {
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
    @ApiModelProperty(value = "租期结束时间")
    private LocalDate leaseOverTime;
    @ApiModelProperty(value = "租期开始时间")
    private LocalDate leaseStartTime;
    @ApiModelProperty("租户名称")
    private String tenementName;
    @ApiModelProperty("租户电话")
    private String mobile;
    @ApiModelProperty("租户身份证")
    private String idCard;
    @ApiModelProperty("业主")
    private String owner;
    @ApiModelProperty("业主名称")
    private String ownerName;
    @ApiModelProperty("业主电话")
    private String ownerMobile;
    @ApiModelProperty("业主身份证")
    private String ownerIdCard;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
