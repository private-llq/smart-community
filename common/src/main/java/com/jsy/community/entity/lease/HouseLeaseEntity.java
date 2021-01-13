package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.annotation.FieldValid;
import com.jsy.community.entity.BaseEntity;
import com.jsy.community.qo.lease.HouseLeaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 房屋租售实体对象
 * YuLF
 * 数据访问对象：这个类主要用于对应数据库表t_house_lease的数据字段的映射关系，
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋租售对象", description="房屋租售数据字段实体")
@TableName("t_house_lease")
public class HouseLeaseEntity extends BaseEntity {

    @ApiModelProperty(value = "所属人ID")
    @JsonIgnore
    private String uid;

    @ApiModelProperty(value = "社区id")
    private Integer houseCommunityId;

    @ApiModelProperty(value = "房源id")
    private Integer houseId;

    @ApiModelProperty(value = "房屋租售标题")
    private String houseTitle;

    @ApiModelProperty(value = "房屋租售所属省ID")
    private Long houseProvinceId;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    private Long houseCityId;

    @ApiModelProperty(value = "房屋租售所属区ID")
    private Long houseAreaId;

    @ApiModelProperty(value = "房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "房屋租售优势标签ID")
    private Long houseAdvantageId;

    @ApiModelProperty(value = "房屋租售价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋出租单位/年/月/周/日")
    private String houseUnit;

    @ApiModelProperty(value = "房屋租售平方米")
    private BigDecimal houseSquareMeter;

    @ApiModelProperty(value = "房屋出租方式/压一付一/压一付三/压一付六")
    private String houseLeaseMode;

    @ApiModelProperty(value = "房屋类型：四室一厅、二室一厅...")
    private String houseTypeCode;

    @ApiModelProperty(value = "房屋所属楼层")
    private String houseFloor;

    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋朝向")
    private String houseDirection;

    @ApiModelProperty(value = "卧室类型、主卧、次卧、其他")
    private String bedroomType;

    @ApiModelProperty(value = "房屋介绍内容")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋联系人电话")
    private String houseContact;

    @ApiModelProperty(value = "房屋图片id,用于在中间表寻找拥有的图片地址")
    private Long houseImageId;

    @ApiModelProperty(value = "房主称呼")
    private String appellation;

    //65不限 66普通住宅 67别墅 68公寓
    @ApiModelProperty(value = "房屋出租类型ID")
    private Integer houseLeaseytypeId;

    //69不限 70整租，71合租
    @ApiModelProperty(value = "房屋出租方式ID")
    private Integer houseLeaseymodeId;

    @ApiModelProperty(value = "房屋家具code")
    private Long house_furniture_id;

    @ApiModelProperty(value = "经度")
    private Double lon;


    @ApiModelProperty(value = "纬度")
    private Double lat;

}
