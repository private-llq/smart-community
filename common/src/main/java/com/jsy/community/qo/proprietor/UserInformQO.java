package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-07 09:50
 **/
@ApiModel("消息已读状态查询")
@Data
public class UserInformQO implements Serializable {

    @ApiModelProperty("分页查询当前页----暂时没用")
    private Long page=0l;

    @ApiModelProperty("分页查询每页数据条数----暂时没用")
    private Long size=10l;

    @ApiModelProperty("按通知ID查询")
    private Long informId;

    @ApiModelProperty("按客户名称模糊查询")
    private String userName;

    @ApiModelProperty("按已读状态查询,1已读0未读,默认1")
    private Integer informStatus=1;

    @ApiModelProperty("按社区ID查询")
    private Long communityId;

    @ApiModelProperty("按房间ID查询")
    private Long houseId;

}
