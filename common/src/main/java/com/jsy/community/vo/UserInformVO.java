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
    @ApiModelProperty(value = "通知ID")
    private Long informId;
    @ApiModelProperty(value = "通知标题")
    private String title;
    @ApiModelProperty(value = "通知状态1表示已读，0表示未读")
    private Long informStatus;

}
