package com.jsy.community.entity.shop;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_shop_lease")
@ApiModel(value="ShopLease对象", description="店铺")
public class ShopLeaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业主id")
    private String uid;
    
    @ApiModelProperty(value = "城市id")
    private Long cityId;
    
    @ApiModelProperty(value = "城市")
    private String city;
    
    @ApiModelProperty(value = "区域id")
    private Long areaId;
    
    @ApiModelProperty(value = "区域")
    private String area;
    
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    
    @ApiModelProperty(value = "楼层")
    private String floor;
    
    @ApiModelProperty(value = "店铺面积  单位：平米")
    private Double shopAcreage;
    
    @ApiModelProperty(value = "面宽")
    private Double shopWidth;
    
    @ApiModelProperty(value = "进深")
    private Double shopDepth;
    
    @ApiModelProperty(value = "层高")
    private Double shopHeight;
    
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "概述")
    private String summarize;

    @ApiModelProperty(value = "月租金")
    private BigDecimal monthMoney;
    
    @ApiModelProperty(value = "转让费")
    private BigDecimal transferMoney;
    
    @ApiModelProperty(value = "免租月数")
    private Integer freeLease;
    
    @ApiModelProperty(value = "起租月数")
    private Integer startLease;

    @ApiModelProperty(value = "房源类型，1可短租，2邻地铁，3押一付一，4配套齐全，5精装修，6南北通透，7有阳台")
    private String defrayType;


    @ApiModelProperty(value = "店铺状态 0空置中 1经营中")
    private Integer status;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "店铺状态 字符串形式")
    private String statusString;
    
    @ApiModelProperty(value = "经度")
    private BigDecimal lon;
    
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;
    
    @ApiModelProperty(value = "来源 0个人 1物业")
    private Integer source;
    
    @ApiModelProperty(value = "称呼")
    private String nickname;
    
    @ApiModelProperty(value = "手机")
    private String mobile;

    /**
     * 租赁状态;0:未出租;1已出租;单间和合租,可以多次出租,整租不能
     */
    private Integer leaseStatus;
    

    // 业主查询商铺列表时的展示
    @TableField(exist = false)
    private String shopShowImg;

    // 社区名称
    @TableField(exist = false)
    private String communityName;
    
    
    
    
    @ApiModelProperty(value = "客流人群Code")
    private Long shopPeople;
    
    @ApiModelProperty(value = "配套设施Code")
    private Long shopFacility;
    
    @ApiModelProperty(value = "所属商业Code")
    private Long shopBusinessId;
    
    @ApiModelProperty(value = "所属类型Code")
    private Long shopTypeId;
}
