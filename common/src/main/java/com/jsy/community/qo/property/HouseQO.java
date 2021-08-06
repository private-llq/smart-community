package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="House查询对象", description="社区楼栋")
public class HouseQO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "id，查下级用")
    private Long id;

    @ApiModelProperty(value = "社区ID")
    private Long communityId;

    @ApiModelProperty(value = "1.楼栋 2.单元 3.楼层 4.门牌")
    private Integer type;
    
    @ApiModelProperty(value = "编号")
    private String number;
    
    @ApiModelProperty(value = "名称")
    private String name;
    
    @ApiModelProperty(value = "是否有电梯 0.无 1.有")
    private Integer hasElevator;
    
    @ApiModelProperty(value = "楼栋名称", hidden = true)
    private String building;
    
    @ApiModelProperty(value = "单元名称", hidden = true)
    private String unit;
    
    @ApiModelProperty(value = "房屋名称", hidden = true)
    private String door;
    
    @ApiModelProperty(value = "楼栋ID")
    private Long buildingId;
    
    @ApiModelProperty(value = "单元ID")
    private Long unitId;
    
    @ApiModelProperty(value = "楼栋总层数")
    private Integer totalFloor;
    
    @ApiModelProperty(value = "楼宇分类")
    private Integer buildingType;
}
