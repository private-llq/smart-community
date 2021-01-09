package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * 数据实体对象
 * 对应数据表字段
 * @author YuLF
 * @since 2020-11-28 13:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("推送消息实体对象")
@TableName(value = "t_acct_push_inform")
public class PushInformEntity extends BaseEntity {


    @ApiModelProperty(value = "推送账号ID、如果是社区推送则是社区ID、否则是第三方推送账号ID")
    private Long acctId;

    @ApiModelProperty(value = "推送消息账号名称")
    private String acctName;

    @ApiModelProperty(value = "推送消息头像地址")
    private String acctAvatar;

    @ApiModelProperty(value = "推送消息标题")
    private String pushTitle;

    @ApiModelProperty(value = "推送消息副标题")
    private String pushSubTitle;

    @ApiModelProperty(value = "推送消息内容")
    private String pushMsg;

    @ApiModelProperty(value = "推送目标：0表示推送至所有社区")
    private Integer pushTarget;



    @ApiModelProperty(value = "推送消息浏览次数")
    private Long browseCount;

    public static PushInformEntity getInstance(){
        return new PushInformEntity();
    }

}
