package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class CommunityQO extends BaseQO {

    private Long id;
    
    @NotNull(groups = {GetCommunityByName.class}, message = "社区名称未输入!")
    @ApiModelProperty(value = "社区名称")
    private String name;
    
    @ApiModelProperty(value = "社区图标url")
    private String iconUrl;
    
    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @NotNull(groups = {GetCommunityByName.class}, message = "城市id不能为空!")
    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区ID")
    private Integer areaId;

    @NotNull(groups = {GetCommunityByName.class}, message = "经度不能为空!")
    @ApiModelProperty(value = "经度")
    private BigDecimal lon;

    @NotNull(groups = {GetCommunityByName.class}, message = "纬度不能为空!")
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;
    
    /**
     * 通过名称查询社区 验证参数接口
     * @author YuLF
     * @since  2020/11/23 11:16
     */
    public interface GetCommunityByName{}

}
