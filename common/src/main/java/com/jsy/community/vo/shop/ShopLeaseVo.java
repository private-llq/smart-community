package com.jsy.community.vo.shop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author jsy
 * @since 2020-12-17
 */
@Data
@ApiModel("店铺")
public class ShopLeaseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业主id")
    private String uid;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "概述")
    private String summarize;

    @ApiModelProperty(value = "月租金")
    private BigDecimal monthMoney;

    @ApiModelProperty(value = "押付方式")
    private String defrayType;

    @ApiModelProperty(value = "转让费 0.00：面议 ")
    private BigDecimal transaferMoney;

    @ApiModelProperty(value = "店铺面积  单位：平米")
    private Integer shopAcreage;

    @ApiModelProperty(value = "房型")
    private String type;

    @ApiModelProperty(value = "楼层")
    private Integer floor;

    @ApiModelProperty(value = "楼层总数")
    private Integer floorCount;

    @ApiModelProperty(value = "朝向   0东 1南 2西 3 北 4东南 5 东北 6 西南 7 西北 8 南北 9东西")
    private Integer orientation;

    @ApiModelProperty(value = "电梯   0无 1有")
    private Integer lift;

    @ApiModelProperty(value = "装修程度  0毛坯房 1普通装修  2精装修")
    private Integer furnishingStyle;

    @ApiModelProperty(value = "年代")
    private String year;

    @ApiModelProperty(value = "用途 0普通住宅 1商业住宅")
    private Integer purpose;

    @ApiModelProperty(value = "权属 0商品房 1学区房 2家住房")
    private Integer ownership;
    
    @ApiModelProperty(value = "图片地址集合")
    private String[] imgPath;
    
    @ApiModelProperty(value = "商铺类型id集合")
    private Long[] shopTypeIds;
    
    @ApiModelProperty(value = "商铺商业id集合")
    private Long[] shopBusinessIds;
    
}
