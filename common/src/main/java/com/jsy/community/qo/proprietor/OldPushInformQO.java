package com.jsy.community.qo.proprietor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsy.community.annotation.FieldValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * 数据传输对象
 * 这个类的作用主要用于接收 和社区消息相关的改 前端参数
 * @author YuLF
 * @since 2020-1-7 13:36
 */
@Data
@ApiModel("推送消息接收参数对象")
// TODO 新版已放弃使用--20210723
public class OldPushInformQO implements Serializable {

    @ApiModelProperty(value = "推送消息ID")
    @NotNull(groups = {UpdateTopStateValidate.class, UpdatePushStateValidate.class, UpdateDetailValidate.class}, message = "推送消息ID不能为空")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String uid;

    @Range(groups = {CommunityPushInformValidate.class}, min = 1, message = "推送号ID范围错误!")
    @NotNull(groups = {CommunityPushInformValidate.class}, message = "推送号ID不能为空!")
    @ApiModelProperty(value = "推送账号ID、如果是社区推送则是社区ID、否则是第三方推送账号ID")
    private Long acctId;

    @ApiModelProperty(value = "推送消息账号名称")
    private String acctName;

    @ApiModelProperty(value = "推送消息头像地址")
    private String acctAvatar;

    @Length(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, min = 2, max = 64, message = "推送消息标题长度在2~64")
    @NotBlank(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, message = "推送消息标题不能为空!")
    @ApiModelProperty(value = "推送消息标题")
    private String pushTitle;

    @ApiModelProperty(value = "推送消息副标题")
    private String pushSubTitle;

    @Length(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, min = 1, max = 1000, message = "推送消息内容长度在1~1000")
    @NotBlank(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, message = "推送消息内容不能为空!")
    @ApiModelProperty(value = "推送消息内容")
    private String pushMsg;

    @Range(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, min = 0, max = 1, message = "推送目标超出范围：0表示推送至所有社区、1则是具体某个社区")
    @NotNull(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, message = "推送目标不能为空")
    @ApiModelProperty(value = "推送目标：0表示推送至所有社区、1则是具体某个社区")
    private Integer pushTarget;

    @FieldValid(groups = {AddPushInformValidate.class, UpdatePushStateValidate.class, UpdateDetailValidate.class}, value = {"0","1","2"}, message = "推送消息状态只有-0表示草稿、1表示发布、2表示撤销")
    @NotNull(groups = {AddPushInformValidate.class, UpdatePushStateValidate.class, UpdateDetailValidate.class}, message = "推送消息状态不能为空!")
    @ApiModelProperty(value = "推送消息状态：0表示草稿、1表示发布、2表示撤销")
    private Integer pushState;

    @Range(groups = {PropertyInformListValidate.class}, min = 0, max = 1, message = "请求页面类型超出范围,0表示草稿,1表示发布")
    @NotNull(groups = {PropertyInformListValidate.class}, message = "请求页面类型不能为空")
    @ApiModelProperty(value = "请求页面类型,0表示草稿,1表示发布")
    private Integer pageState;

    @FieldValid( groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, value = {"站内","外部链接"}, message = "推送公告类型 站内、外部链接")
    @NotBlank(groups = {AddPushInformValidate.class, UpdateDetailValidate.class}, message = "推送公告类型不能为空!")
    @ApiModelProperty(value = "推送公告类型 站内、外部链接")
    private String informType;

    @ApiModelProperty(value = "创建人Id")
    private String createBy;

    @ApiModelProperty(value = "更新人Id")
    private String updateBy;


    @FieldValid( groups = {AddPushInformValidate.class, UpdateTopStateValidate.class, UpdateDetailValidate.class}, value = {"0","1"}, message = "置顶状态 0不置顶 1置顶")
    @NotNull(groups = {AddPushInformValidate.class, UpdateTopStateValidate.class, UpdateDetailValidate.class}, message = "置顶状态不能为空!")
    @ApiModelProperty(value = "置顶状态")
    private Integer topState;

    @ApiModelProperty("创建日期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startCreateTime;

    @ApiModelProperty("创建日期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endCreateTime;

    @ApiModelProperty("发布日期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startUpdateTime;

    @ApiModelProperty("发布日期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endUpdateTime;

    @ApiModelProperty(value = "发布人")
    private String publishBy;

    @ApiModelProperty(value = "发布时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime publishTime;

    /**
     * 添加推送消息验证接口
     */
    public interface AddPushInformValidate {}


    /**
     * (业主端)查询社区推送消息验证接口
     */
    public interface CommunityPushInformValidate {}

    /**
     * (物业端)查询公告列表接口
     */
    public interface PropertyInformListValidate {}

    /**
     * (物业端)更新置顶状态接口
     */
    public interface UpdateTopStateValidate {}

    /**
     * (物业端)更新发布状态接口
     */
    public interface UpdatePushStateValidate {}

    /**
     * (物业端)更新消息接口
     */
    public interface UpdateDetailValidate {}

}
