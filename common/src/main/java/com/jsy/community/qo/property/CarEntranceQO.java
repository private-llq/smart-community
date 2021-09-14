package com.jsy.community.qo.property;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CarEntranceQO extends BaseQO {
    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    @ApiModelProperty(value = "社区id")
    private Long communityId;

}
