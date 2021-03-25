package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-23 15:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplainVO extends BaseVO {
    @ApiModelProperty(value = "类型，1投诉，2建议")
    private Integer type;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "图片地址")
    private String images;
    @ApiModelProperty(value = "投诉ID")
    private String uid;
    @ApiModelProperty(value = "投诉人名称")
    private String name;
    @ApiModelProperty(value = "投诉人电话")
    private String mobile;
    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;
    @ApiModelProperty(value = "反馈内容")
    private String feedback;
    @ApiModelProperty(value = "反馈内容")
    private Integer status;
}
