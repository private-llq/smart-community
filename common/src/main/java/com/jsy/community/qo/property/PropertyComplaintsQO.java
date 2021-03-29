package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-19 13:50
 **/
@Data
public class PropertyComplaintsQO implements Serializable {
    @ApiModelProperty("投诉人姓名/联系电话/投诉编号")
    private String keyWord;
    @ApiModelProperty("社区id")
    private Long communityId;
    @ApiModelProperty("回复状态")
    private Integer status;
    @ApiModelProperty("投诉类型")
    private Integer type;
    @ApiModelProperty("开始的投诉时间")
    private LocalDate complainTimeStart;
    @ApiModelProperty("结束的投诉时间")
    private LocalDate complainTimeOut;
    @ApiModelProperty("开始的回复时间")
    private LocalDate feedbackTimeStart;
    @ApiModelProperty("结束的回复时间")
    private LocalDate feedbackTimeOut;
}
