package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 数据传输对象
 * 这个类的作用主要用于接收 和社区消息相关的改 前端参数
 * @author YuLF
 * @since 2020-1-7 13:36
 */
@Data
@ApiModel("推送消息接收参数对象")
public class PushInformQO implements Serializable {


    @ApiModelProperty(value = "推送消息ID")
    private Long id;


    @ApiModelProperty(value = "用户ID")
    private String uid;

    @Range(groups = {AddPushInformValidate.class, CommunityPushInformValidate.class}, min = 1, message = "推送号ID范围错误!")
    @NotNull(groups = {AddPushInformValidate.class, CommunityPushInformValidate.class}, message = "推送号ID不能为空!")
    @ApiModelProperty(value = "推送账号ID、如果是社区推送则是社区ID、否则是第三方推送账号ID")
    private Long acctId;

    @Length(groups = {AddPushInformValidate.class}, min = 1, max = 12, message = "推送消息账号名称长度在1~12之间")
    @NotBlank(groups = {AddPushInformValidate.class}, message = "推送消息账号名称不能为空!")
    @ApiModelProperty(value = "推送消息账号名称")
    private String acctName;

    @ApiModelProperty(value = "推送消息头像地址")
    private String acctAvatar;

    @Length(groups = {AddPushInformValidate.class}, min = 1, max = 32, message = "推送消息标题长度在1~32")
    @NotBlank(groups = {AddPushInformValidate.class}, message = "推送消息标题不能为空!")
    @ApiModelProperty(value = "推送消息标题")
    private String pushTitle;

    @Length(groups = {AddPushInformValidate.class}, min = 1, max = 64, message = "推送消息副标题长度在1~32")
    @NotBlank(groups = {AddPushInformValidate.class}, message = "推送消息副标题不能为空!")
    @ApiModelProperty(value = "推送消息副标题")
    private String pushSubTitle;

    @Length(groups = {AddPushInformValidate.class}, min = 1, max = 1000, message = "推送消息内容长度在1~1000")
    @NotBlank(groups = {AddPushInformValidate.class}, message = "推送消息内容不能为空!")
    @ApiModelProperty(value = "推送消息内容")
    private String pushMsg;

    @Range(groups = {AddPushInformValidate.class}, min = 0, max = 1, message = "推送消息目标ID范围错误!")
    @NotNull(groups = {AddPushInformValidate.class}, message = "推送消息目标社区ID未指定!")
    @ApiModelProperty(value = "推送目标：0表示推送至所有社区、1则是具体某个社区")
    private Integer pushTarget;



    /**
     * 添加推送消息验证接口
     */
    public interface AddPushInformValidate {}


    /**
     * 查询社区推送消息验证接口
     */
    public interface CommunityPushInformValidate {}

}
