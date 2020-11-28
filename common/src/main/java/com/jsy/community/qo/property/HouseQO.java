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

}
