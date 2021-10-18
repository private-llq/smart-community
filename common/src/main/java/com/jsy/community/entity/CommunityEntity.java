package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotBlank(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "社区名称不能为空")
    private String name;
    
    @ApiModelProperty(value = "社区图标url", hidden = true)
    private String iconUrl;
    
    @ApiModelProperty(value = "社区编号")
    private String number;
    
    @ApiModelProperty(value = "面积")
    private BigDecimal acreage;
    
    @ApiModelProperty(value = "省份ID")
    @NotNull(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "所属省份ID不能为空")
    private Integer provinceId;

    @ApiModelProperty(value = "城市ID")
    @NotNull(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "所属城市ID不能为空")
    private Integer cityId;

    @ApiModelProperty(value = "区ID")
    @NotNull(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "所属区域ID不能为空")
    private Integer areaId;

    @ApiModelProperty(value = "详细地址")
    @NotBlank(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "社区详细地址不能为空")
    private String detailAddress;

    @ApiModelProperty(value = "经度")
    @NotNull(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "社区经度不能为空")
    private BigDecimal lon;

    @ApiModelProperty(value = "纬度")
    @NotNull(groups = {sysAddValidatedGroup.class, ProperyuAddValidatedGroup.class}, message = "社区纬度不能为空")
    private BigDecimal lat;
    
    @ApiModelProperty(value = "社区房屋层级模式 1.楼栋单元 2.单元楼栋 3.单楼栋 4.单单元")
    @NotNull(groups = {sysAddValidatedGroup.class}, message = "社区房屋层级模式不能为空")
    @Range(min = 1, max = 4, message = "社区房屋层级模式有误")
    private Integer houseLevelMode;
    
    @ApiModelProperty(value = "推广人id")
    private String promoter;

    // 物业id
    // @NotNull(groups = ProperyuAddValidatedGroup.class, message = "请填写物业信息")
    private Long propertyId;

    // 联系人
    private String contact;

    // 联系电话
    private String contactMobile;
    
    //返回字段
    @TableField(exist=false)
    private Double distanceDouble;//定位距离(排序用)
    @TableField(exist=false)
    private String distanceString;//定位距离(显示用)
    
    @TableField(exist=false)
    private Long houseId;//房屋ID 小区定位返回用

    // 物业公司名称
    @TableField(exist=false)
    private String companyName;
    
    // 地区地址拼接
    @TableField(exist = false)
    private String address;
    
    /**
     * 大后台新增社区验证组
     */
    public interface sysAddValidatedGroup{}

    /**
     * 物业端新增小区验证
     */
    public interface ProperyuAddValidatedGroup{}

}
