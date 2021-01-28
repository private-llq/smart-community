package com.jsy.community.vo.lease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 房屋租售数据返回对象
 * 用于视图层返回显示
 * YuLF
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋出租返回对象", description="返回后端查询参数")
public class HouseLeaseVO implements Serializable {

    @ApiModelProperty(value = "业务数据唯一标识")
    private Long id;

    @ApiModelProperty(value = "房屋租售标题")
    private String houseTitle;


    @ApiModelProperty(value = "社区id")
    private Integer houseCommunityId;

    @ApiModelProperty(value = "房源id")
    private Integer houseId;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    private Long houseCityId;

    @ApiModelProperty(value = "房屋租售所属区ID")
    private Long houseAreaId;

    @ApiModelProperty(value = "房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "房屋租售优势标签ID")
    private Long houseAdvantageId;

    @ApiModelProperty(value = "房屋租售家具标签ID")
    private Long houseFurnitureId;

    @ApiModelProperty(value = "房屋租售优势标签")
    private Map<String, Long> houseAdvantage;

    @ApiModelProperty(value = "房屋租售家具标签")
    private List<String> houseFurniture;

    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋租售价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    private String houseUnit;

    @ApiModelProperty(value = "房屋租售平方米")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房主称呼")
    private String appellation;

    /**
     * 值是变动  需要存id至数据库 对应 名称 有后台人员管理
     */
    @ApiModelProperty(value = "房屋出租方式id /1.压一付一/2.压一付三/3.压一付六")
    private Long houseLeasedepositId;


    @ApiModelProperty(value = "房屋出租方式文本 /1.压一付一/2.压一付三/3.压一付六")
    private String houseLeaseDeposit;

    @ApiModelProperty(value = "房屋类型Code：如040202 表示 4室2厅2卫")
    private String houseTypeCode;

    @ApiModelProperty(value = "房屋类型文本：1.四室一厅、2.二室一厅...")
    private String houseType;

    @ApiModelProperty(value = "房屋所属楼层")
    private String houseFloor;

    /**
     * 非 经常变动，常量存 BusinessEnum
     */
    @ApiModelProperty(value = "房屋朝向1.东.2.西 3.南 4.北. 4.东南 5. 东北 6.西北 7.西南")
    private String houseDirection;


    @ApiModelProperty(value = "房屋联系人电话")
    private String houseContact;


    /**
     * 值是变动  需要存id至数据库 对应 名称 由后台人员管理
     */
    @ApiModelProperty(value = "房源类型ID、73不限(默认) 74可短租 75邻地铁  76压一付一  77配套齐全  78精装修 79南北通透  80有阳台")
    private Long houseSourcetypeId;


    @ApiModelProperty(value = "房屋介绍内容")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋图片数组地址")
    private List<String> houseImage;

    @ApiModelProperty(value = "图片id")
    private Long houseImageId;

    @ApiModelProperty(value = "卧室类型、主卧、次卧、其他")
    private String bedroomType;

    /**
     * 65不限 66普通住宅 67别墅 68公寓
     */
    @ApiModelProperty(value = "房屋出租类型ID")
    private Integer houseLeasetypeId;


    /**
     * 69不限 70整租，71合租
     */
    @ApiModelProperty(value = "房屋出租方式ID")
    private Long houseLeasemodeId;

    @ApiModelProperty(value = "房屋出租方式文本")
    private String houseLeaseMode;

    @ApiModelProperty(value = "经度")
    private Double houseLon;

    @ApiModelProperty(value = "纬度")
    private Double houseLat;

    @ApiModelProperty(value = "是否被当前用户已收藏")
    private Boolean favorite;

    @ApiModelProperty(value = "是出租房吗/false为商铺")
    private Boolean leaseHouse;

}
