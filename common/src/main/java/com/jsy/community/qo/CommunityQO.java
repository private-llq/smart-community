package com.jsy.community.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * <p>
 * 社区
 * </p>
 *
 * @author jsy
 * @since 2020-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Community对象", description="社区")
public class CommunityQO extends BaseQO {

    @ApiModelProperty(value = "社区名称")
    private String name;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区ID")
    private Integer areaId;
    
    @ApiModelProperty(value = "用户经度")
    private BigDecimal lon;
    
    @ApiModelProperty(value = "用户纬度")
    private BigDecimal lat;

}
