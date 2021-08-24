package com.jsy.community.vo.proprietor;

import com.jsy.community.qo.BaseQO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("投诉建议返回参数")
public class ComplainVO extends BaseQO implements Serializable {
    @ApiModelProperty(value = "社区id")
    private String communityName;

    @NotNull(message = "类容不能为空")
    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "1，已回复，0未回复")
    private Integer status;

    @ApiModelProperty(value = "投诉时间")
    private LocalDateTime complainTime;
}
