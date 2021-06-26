package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


/**
 * 数据实体对象
 * 对应数据表字段
 * @author YuLF
 * @since 2020-11-28 13:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("推送消息实体对象")
@TableName(value = "t_push_inform")
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

    @ApiModelProperty(value = "推送消息状态：0表示草稿、1表示发布、2表示撤销")
    private Integer pushState;

    @ApiModelProperty(value = "推送公告类型 站内、外部链接")
    private String informType;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "置顶状态 0不置顶 1置顶")
    private Integer topState;

    @ApiModelProperty(value = "推送消息浏览次数")
    private Long browseCount;

    @ApiModelProperty(value = "发布人")
    private String publishBy;

    @ApiModelProperty(value = "发布时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime publishTime;

    public static PushInformEntity getInstance(){
        return new PushInformEntity();
    }

}
