package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 房屋列表信息
 * @author YuLF
 * @since 2020-12-18 15:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="House返回对象", description="社区房屋返回对象")
public class HouseVo extends BaseVO {

    @ApiModelProperty(value = "城市id")
    private Integer cityId;

    @ApiModelProperty(value = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "房屋ID")
    private Long houseId;

    @ApiModelProperty(value = "楼栋名")
    private String building;

    @ApiModelProperty(value = "单元名")
    private String unit;

    @ApiModelProperty(value = "楼层名")
    private String floor;

    @ApiModelProperty(value = "门牌名")
    private String door;

    @ApiModelProperty(value = "检查状态")
    private String checkStatus;

    @ApiModelProperty(value = "社区名称", hidden = true)
    private String communityName;

    @ApiModelProperty(value = "上述名称合并后的名称", hidden = true)
    private String mergeName;

}
