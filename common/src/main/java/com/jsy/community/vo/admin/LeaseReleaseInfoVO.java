package com.jsy.community.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class LeaseReleaseInfoVO implements Serializable {
    private Long id;

    @ApiModelProperty(value = "房源标题")
    private String houseTitle;

    @ApiModelProperty(value = "租售方式编号")
    private Integer houseLeasemodeId;

    @ApiModelProperty(value = "租售方式：1不限(默认) 2整租，4合租 8 单间")
    private String leaseType;

    @ApiModelProperty(value = "押付方式编号")
    private Integer houseLeasedepositId;

    @ApiModelProperty(value = "押付方式：压一付一、压一付三、压一付六")
    private String houseLeaseMode;

    @ApiModelProperty(value = "租赁价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "计量单位")
    private String houseUnit;

    @ApiModelProperty(value = "租赁价格(xx/月、年)")
    private String priceStr;

    @ApiModelProperty(value = "小区ID")
    private Long houseCommunityId;

    @ApiModelProperty(value = "小区地址")
    private String communityAddress;

    @ApiModelProperty(value = "房屋类型（商铺/住宅）")
    private String houseType;

    @ApiModelProperty(value = "户型编号")
    private String houseTypeCode;

    @ApiModelProperty(value = "户型：四室一厅、二室一厅...别墅000000 如040202代表着4室2厅2卫")
    private String typeCodeStr;

    @ApiModelProperty(value = "楼层")
    private String houseFloor;

    @ApiModelProperty(value = "朝向编号")
    private String houseDirectionId;

    @ApiModelProperty(value = "朝向：1.东.2.西 3.南 4.北. 5.东南 6. 东北 7.西北 8.西南")
    private String direction;

    @ApiModelProperty(value = "房屋面积")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "装修情况ID：1.简单装修 2.精装修 4.豪华装修")
    private Long decorationTypeId;

    @ApiModelProperty(value = "装修情况")
    private String decorationType;

    @ApiModelProperty(value = "房间设施ID")
    private Long roomFacilitiesId;

    @ApiModelProperty(value = "房间设施")
    private List<String> roomFacilities = new ArrayList<>();

    @ApiModelProperty(value = "房屋优势ID")
    private Long houseAdvantageId;

    @ApiModelProperty(value = "房屋优势")
    private List<String> houseAdvantage = new ArrayList<>();

    @ApiModelProperty(value = "出租要求ID")
    private Long leaseRequireId;

    @ApiModelProperty(value = "出租要求")
    private List<String> leaseRequire = new ArrayList<>();

    @ApiModelProperty(value = "房源描述")
    private String houseIntroduce;

    @ApiModelProperty(value = "发布人")
    private String appellation;

    @ApiModelProperty(value = "手机号")
    private String houseContact;

    @ApiModelProperty(value = "房屋图片ID")
    private Long houseImageId;

    @ApiModelProperty(value = "图片地址")
    private List<String> imgUrl = new ArrayList<>();

}
