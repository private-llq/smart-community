package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-19 13:40
 **/
@Data
@ApiModel("物业投诉受理")
public class PropertyComplaintsVO implements Serializable {
    @ApiModelProperty("社区ID")
    private Long communityId;
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("编号")
    private String serialNumber;
    @ApiModelProperty("用户name")
    private String name;
    @ApiModelProperty("用户电话")
    private String mobile;
    @ApiModelProperty("投诉类型")
    private Integer type;
    @ApiModelProperty("投诉类型")
    private String typeName;
    @ApiModelProperty("投诉内容")
    private String content;
    @ApiModelProperty("图片集合")
    private String images;
    @ApiModelProperty("投诉时间")
    private LocalDateTime complainTime;
    @ApiModelProperty("回复状态0未回复，1已回复")
    private Integer status;
    @ApiModelProperty("回复人名称")
    private String feedbackName;
    @ApiModelProperty("回复时间")
    private LocalDateTime feedbackTime;
    @ApiModelProperty("回复内容")
    private String feedbackContent;
    @ApiModelProperty("位置")
    private String location;

}
