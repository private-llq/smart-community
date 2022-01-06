package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("新投诉建议接参")
public class ComplainQO implements Serializable {
    @NotNull(message = "类容不能为空")
    @ApiModelProperty(value = "内容")
    private String content;

    @NotNull(message = "电话不能为空")
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "社区id")
    private String communityId;

    @ApiModelProperty(value = "用户id")
    private String uid;
    
    @ApiModelProperty(value = "1,投诉，2建议")
    private Integer type;
    
    /**
     * 来源（1.社区 2.商家）
     */
    private Integer source;
}
