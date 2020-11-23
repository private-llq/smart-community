package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 房间成员表
 * </p>
 *
 * @author jsy
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HouseMember业主端查询对象", description="房间成员表")
public class HouseMemberQO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "人员ID")
    private Long uid;
    
    @ApiModelProperty(value = "房间ID")
    private Long houseId;
    
    @ApiModelProperty(value = "查询类型")
    private Integer type;
}
