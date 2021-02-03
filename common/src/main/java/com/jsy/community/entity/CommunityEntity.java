package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

/**
 * @author qq459799974
 * @since 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Community对象", description="社区")
@TableName("t_community")
public class CommunityEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "社区名称")
    private String name;
    
    @ApiModelProperty(value = "社区图标url", hidden = true)
    private String iconUrl;
    
    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区ID")
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @ApiModelProperty(value = "经度")
    private BigDecimal lon;

    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;
    
    //返回字段
    @TableField(exist=false)
    private Double distanceDouble;//定位距离(排序用)
    @TableField(exist=false)
    private String distanceString;//定位距离(显示用)
    @ApiModelProperty(value = "社区房屋层级模式 1.楼栋单元 2.单元楼栋 3.单楼栋 4.单单元")
    @Range(min = 1, max = 4, message = "社区房屋层级模式有误")
    private Integer houseLevelMode;
    
    @TableField(exist=false)
    private Long houseId;//房屋ID 小区定位返回用


}
