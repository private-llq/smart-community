package com.jsy.community.entity.lease;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author YuLF
 * @since  2021/1/13 17:48
 * 房屋租售实体对象
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

    // 租赁方式:压一付一、压一付三、压一付六...
    private Integer houseLeasedepositId;

    @ApiModelProperty(value = "房屋类型：四室一厅、二室一厅...")
    private String houseTypeCode;

    @ApiModelProperty(value = "房屋所属楼层")
    private String houseFloor;

    @ApiModelProperty(value = "房屋朝向")
    private String houseDirectionId;

    @ApiModelProperty(value = "房屋联系人电话")
    private String houseContact;

    @ApiModelProperty(value = "房屋介绍内容")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋预约时间")
    private String houseReserveTime;

    @ApiModelProperty(value = "房屋图片id,用于在中间表寻找拥有的图片地址")
    private Long houseImageId;

    @ApiModelProperty(value = "房屋出租类型ID")
    private Integer houseLeasetypeId;

    @ApiModelProperty(value = "房屋出租方式ID")
    private Integer houseLeasemodeId;

    // 房屋来源类型id：1.不限 2.个人 4.物业
    private Integer houseSourceId;

    @ApiModelProperty(value = "房屋家具code")
    private Long houseFurnitureId;

    @ApiModelProperty(value = "卧室类型、主卧、次卧、其他")
    private String bedroomType;

    @ApiModelProperty(value = "经度")
    private Double house_lon;

    @ApiModelProperty(value = "纬度")
    private Double house_lat;

    @ApiModelProperty(value = "房主称呼")
    private String appellation;

    @ApiModelProperty(value = "社区id")
    private Long houseCommunityId;

    @ApiModelProperty(value = "房源id")
    private Long houseId;

    @ApiModelProperty( value = "出租要求位运算后的id")
    private Long leaseRequireId;

    @ApiModelProperty( value = "公共设施位运算后的id")
    private Long commonFacilitiesId;

    @ApiModelProperty( value = "装修情况codeId：1.简单装修 2.精装修 4.豪华装修")
    private Long decorationTypeId;

    @ApiModelProperty( value = "室友期望Code运算后的Id")
    private Long roommateExpectId;

    @ApiModelProperty( value = "室友性别code: 1.限女生 2.限男生 4.男女不限")
    private String roommateSex;

    @ApiModelProperty( value = "房间设施位运算后的id")
    private Long roomFacilitiesId;

    @ApiModelProperty(value = "房屋出租方式/压一付一/压一付三/压一付六")
    @TableField(exist = false)
    private String houseLeaseMode;

    @TableField(exist = false)
    private String houseImgUrl;

    // 优势
    @TableField(exist = false)
    private Map<String, Long> houseAdvantageMap;

    // 房型
    @TableField(exist = false)
    private String houseTypeStr;
}
