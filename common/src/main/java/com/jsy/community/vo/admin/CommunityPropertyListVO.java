package com.jsy.community.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommunityPropertyListVO implements Serializable {

    @ApiModelProperty("小区名字")
    private String communityName;

    @ApiModelProperty("物业公司名字名字")
    private String propertyName;

    @ApiModelProperty("小区id")
    private Long communityId;

    @ApiModelProperty("小区idStr")
    private String communityIdStr;
}
