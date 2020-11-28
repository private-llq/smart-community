package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

    @NotNull(groups = {GetCommunityByName.class}, message = "社区名称未输入!")
    @ApiModelProperty(value = "社区名称")
    private String name;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @NotNull(groups = {GetCommunityByName.class}, message = "城市id不能为空!")
    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区ID")
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @NotNull(groups = {GetCommunityByName.class}, message = "经度不能为空!")
    @ApiModelProperty(value = "经度")
    private BigDecimal lon;

    @NotNull(groups = {GetCommunityByName.class}, message = "纬度不能为空!")
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;
    
    //返回字段
    @TableField(exist=false)
    private Double distanceDouble;//定位距离(排序用)
    @TableField(exist=false)
    private String distanceString;//定位距离(显示用)

   /**
    * 通过名称查询社区 验证参数接口
    * @author YuLF
    * @since  2020/11/23 11:16
    */
    public interface GetCommunityByName{}

}
