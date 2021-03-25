package com.jsy.community.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-15 14:01
 **/
@Data
public class CommunityFunOperationQO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "1表示站内2表示外部链接")
    private Integer type;

    @ApiModelProperty(value = "社区趣事标题")
    @NotNull(groups = {CommunityFunOperationValidated.class},message = "标题不能为空！")
    @NotBlank(groups = {CommunityFunOperationValidated.class},message = "标题不能为空！")
    private String titleName;

    @ApiModelProperty(value = "社区趣事浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "uid创建人")
    private String uid;

    @ApiModelProperty(value = "社区趣事内容")
    @NotNull(groups = {CommunityFunOperationValidated.class},message = "内容不能为空！")
    @NotBlank(groups = {CommunityFunOperationValidated.class},message = "内容不能为空！")
    private String content;
    @ApiModelProperty(value = "社区趣事缩略图地址")
    @NotNull(groups = {CommunityFunOperationValidated.class},message = "缩略图不能为空！")
    @NotBlank(groups = {CommunityFunOperationValidated.class},message = "缩略图不能为空！")
    private String smallImageUrl;
    @ApiModelProperty(value = "社区趣事封面图地址")
    @NotNull(groups = {CommunityFunOperationValidated.class},message = "封面图不能为空！")
    @NotBlank(groups = {CommunityFunOperationValidated.class},message = "封面图不能为空！")
    private String coverImageUrl;
    @ApiModelProperty(value = "社区趣事状态1表示已上线，2二表示为上线")
    private Integer status;
    @ApiModelProperty(value = "社区趣事状态1表示已发布，2二表示为编辑")
    private Integer redactStatus;
    @ApiModelProperty(value = "上线时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "下线时间")
    private LocalDateTime outTime;
    @ApiModelProperty(value = "标签")
    private String[] tallys;

    public interface CommunityFunOperationValidated{}
}
