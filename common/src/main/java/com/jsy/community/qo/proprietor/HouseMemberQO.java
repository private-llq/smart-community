package com.jsy.community.qo.proprietor;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author qq459799974
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HouseMember业主端查询对象", description="房间成员表")
public class HouseMemberQO extends BaseQO {
    
    @ApiModelProperty(value = "房主ID")
    private String householderId;
    
}
