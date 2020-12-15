package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 社区消息实体类
 * @author YuLF
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_community_inform")
@ApiModel(value="CommunityInform对象", description="接收社区消息的实体类")
public class CommunityInformEntity extends BaseEntity {

    private String uid;

    @ApiModelProperty(value = "社区id")
    @NotNull( groups = {updateCommunityInformValidate.class}, message = "社区ID不能为空")
    private Long communityId;

    @ApiModelProperty(value = "社区通知消息状态，0紧急，1重要，2一般")
    @NotNull( groups = {addCommunityInformValidate.class}, message = "社区状态未选择! 0紧急，1重要，2一般")
    @Range(groups = {addCommunityInformValidate.class},  min = 0, max = 2, message = "选择的状态不可用!")
    private Integer state;

    @ApiModelProperty(value = "社区通知消息头(标题)")
    @NotBlank( groups = {addCommunityInformValidate.class}, message = "请输入一个社区标题!")
    private String title;

    @ApiModelProperty(value = "社区通知消息头(副标题)")
    @NotBlank( groups = {addCommunityInformValidate.class}, message = "请输入一个社区副标题说明!")
    private String subTitle;

    @ApiModelProperty(value = "社区通知消息体(内容)")
    @NotBlank( groups = {addCommunityInformValidate.class}, message = "请输入一个社区通知消息内容!")
    private String content;

    @ApiModelProperty(value = "0代表消息未启用，1代表消息启用")
    @NotNull( groups = {addCommunityInformValidate.class}, message = "请选择消息是否需要启用!")
    private Integer enabled;

    @ApiModelProperty(value = "消息浏览次数")
    private Integer browseCount;

    @ApiModelProperty(value = "表明是初次进入社区页面的查询，不分页")
    @TableField(exist = false)
    private boolean initialQuery = false;

    @ApiModelProperty(value = "表明消息在当前社区当前用户状态是否已读，默认是未读")
    @TableField(exist = false)
    private boolean read = false;

    @JsonIgnore
    @ApiModelProperty(value = "备用字段")
    private String reserver01;

    /**
     * 添加社区消息接口
     */
    public interface addCommunityInformValidate{}

    /**
     * 修改社区消息接口
     */
    public interface updateCommunityInformValidate{}


}
