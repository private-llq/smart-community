package com.jsy.community.qo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LeaseReleasePageQO implements Serializable {
    @ApiModelProperty("社区ID")
    private Long communityId;

    @ApiModelProperty("房屋类型：（普通住宅/商铺）")
    private Integer type;

    @ApiModelProperty("租赁状态;0:未出租;1已出租")
    private Integer leaseStatus;

    @ApiModelProperty("业主信息（电话或姓名）,前后均模糊匹配")
    private String phone;
    
    private String nickName;
}
