package com.jsy.community.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jsy.community.utils.RegexUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-11 11:02
 **/
@Data
@TableName("t_property_opinion")
public class PropertyOpinionEntity extends BaseEntity{
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "uid")
    private String uid;
    @ApiModelProperty(value = "社区id")
    private Long communityId;
    @ApiModelProperty(value = "邮箱")
    @NotBlank(groups = {PropertyOpinionValidated.class},message = "邮箱不能为空！")
    private String email;
    @NotBlank(groups = {PropertyOpinionValidated.class},message = "内容不能为空！")
    @ApiModelProperty(value = "意见类容")
    private String content;
    @ApiModelProperty(value = "图片")
    @Pattern(groups = {PropertyOpinionValidated.class},regexp = RegexUtils.REGEX_EMAIL,message = "邮箱格式不正确！")
    private String images;

    public interface PropertyOpinionValidated{}

}
