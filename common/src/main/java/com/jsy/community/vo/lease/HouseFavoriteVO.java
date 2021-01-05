package com.jsy.community.vo.lease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房屋收藏数据返回对象
 * 用于视图层返回显示
 * YuLF
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房屋收藏返回对象", description="返回后端查询参数")
public class HouseFavoriteVO implements Serializable {

    @ApiModelProperty(value = "房屋id")
    private long houseId;

    @ApiModelProperty(value = "收藏数据id")
    private long favoriteId;

    @ApiModelProperty(value = "房屋租售标题")
    private String houseTitle;

    @ApiModelProperty(value = "房屋第一张图片地址")
    private String houseImage;

    @ApiModelProperty(value = "房屋价格")
    private BigDecimal housePrice;

    @ApiModelProperty(value = "房屋租售所属城市ID")
    private Long houseCityId;

    @ApiModelProperty(value = "房屋面积/平方米")
    private Double houseSquareMeter;

    @ApiModelProperty(value = "房屋租售详细地址")
    private String houseAddress;

    @ApiModelProperty(value = "商铺转让费")
    private BigDecimal shopTransferMoney;

}
