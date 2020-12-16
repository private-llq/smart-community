package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

/**
 * 房屋租售实体对象
 * YuLF
 * 数据访问对象：这个类主要用于对应数据库表t_house_lease_sale的数据字段的映射关系，
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

    @ApiModelProperty(value = "标识房屋是租赁还是销售 1.租赁 0.销售")
    private Short houseIsLease;

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
    private Integer houseTypeId;

    @ApiModelProperty(value = "房屋所属楼层")
    private String houseFloor;

    @ApiModelProperty(value = "房屋朝向")
    private String houseDirection;

    @ApiModelProperty(value = "房屋是否有电梯：1有 0无")
    private Integer houseHasElevator;

    @ApiModelProperty(value = "房屋装修样式ID、1、精装修2、现代风格、3.古典风格、4.欧美风")
    private Integer houseStyleId;

    @ApiModelProperty(value = "房屋年代")
    private String houseYear;

    @ApiModelProperty(value = "房屋用途ID、1住宅、2工商业、3仓库")
    private Integer houseUsageId;

    @ApiModelProperty(value = "房屋种类id 1.商品房、2.经济适用房、3.央产房、4.军产房、5.公房、6.小产权房、7.自建住房")
    private Integer houseKindId;

    @ApiModelProperty(value = "房屋介绍内容")
    private String houseIntroduce;

    @ApiModelProperty(value = "房屋联系人电话")
    private String houseContact;

    @ApiModelProperty(value = "房屋图片id,用于在中间表寻找拥有的图片地址")
    private Long houseImageId;

    //65不限 66普通住宅 67别墅 68公寓
    @ApiModelProperty(value = "房屋出租类型ID")
    private Integer houseLeaseytypeId;

    //69不限 70整租，71合租
    @ApiModelProperty(value = "房屋出租方式ID")
    private Integer houseLeaseymodeId;


}
