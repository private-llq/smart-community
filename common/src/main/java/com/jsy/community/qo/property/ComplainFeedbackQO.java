package com.jsy.community.qo.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-23 16:55
 **/
@Data
@ApiModel(value="投诉建议反馈")
public class ComplainFeedbackQO implements Serializable {
    @ApiModelProperty("投诉信息id")
    private Long id;
    @ApiModelProperty("反馈内容")
    private String content;

}
