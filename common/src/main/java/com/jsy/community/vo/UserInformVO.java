package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 查询所有已读通知业主
 * @author: Hu
 * @create: 2020-12-04 15:01
 **/
@Data
@ApiModel("业主所有已读通知")
public class UserInformVO implements Serializable {
    @ApiModelProperty(value = "业主ID")
    private Long userId;
    @ApiModelProperty(value = "业主名字")
    private String userName;
    @ApiModelProperty(value = "社区ID")
    private Long communityId;
    @ApiModelProperty(value = "社区名称")
    private String communityName;
    @ApiModelProperty(value = "房间ID")
    private Long houseId;
    @ApiModelProperty(value = "单元")
    private String unit;
    @ApiModelProperty(value = "楼层")
    private String floor;
    @ApiModelProperty(value = "房间号")
    private String door;
    @ApiModelProperty(value = "房间楼栋")
    private String building;
    @ApiModelProperty(value = "通知ID")
    private Long informId;
    @ApiModelProperty(value = "通知标题")
    private String title;
    @ApiModelProperty(value = "通知内容")
    private String content;
    @ApiModelProperty(value = "通知状态1表示已读，0表示未读")
    private Integer informStatus;

}
