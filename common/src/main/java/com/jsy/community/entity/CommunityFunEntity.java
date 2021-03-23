package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 10:22
 **/
@Data
@ApiModel("社区趣事")
@TableName("t_community_fun")
public class CommunityFunEntity extends BaseEntity {

    @ApiModelProperty(value = "社区趣事标题")
    @NotBlank(groups = {CommunityFunValidated.class}, message = "标题不能为空！")
    private String titleName;

    @ApiModelProperty(value = "社区趣事浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "uid创建人")
    private String createBy;

    @ApiModelProperty(value = "uid修改人")
    private String updateBy;

    @ApiModelProperty(value = "发布人uid")
    private String startBy;

    @ApiModelProperty(value = "社区趣事内容")
    @NotBlank(groups = {CommunityFunValidated.class}, message = "内容不能为空！")
    private String content;
    @ApiModelProperty(value = "社区趣事缩略图地址")
    @NotBlank(groups = {CommunityFunValidated.class}, message = "缩略图不能为空！")
    private String smallImageUrl;
    @ApiModelProperty(value = "社区趣事封面图地址")
    @NotBlank(groups = {CommunityFunValidated.class}, message = "封面图不能为空！")
    private String coverImageUrl;
    @ApiModelProperty(value = "社区趣事状态1发布，2撤销")
    private Integer status;
    @ApiModelProperty(value = "1发布2编辑")
    private Integer redactStatus;
    @ApiModelProperty(value = "上线时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "下线时间")
    private LocalDateTime outTime;
    @ApiModelProperty(value = "标签")
    private String tallys;
    @ApiModelProperty(value = "创建人名称")
    private String createName;
    @ApiModelProperty(value = "修改人名称")
    private String updateName;
    @ApiModelProperty(value = "发布人名称")
    private String startName;


    public interface CommunityFunValidated {
    }


}
