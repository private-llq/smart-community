package com.jsy.community.qo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 选择分组查询下面缴过费的水电气户号
 * @author: Hu
 * @create: 2020-12-11 14:09
 **/
@Data
@ApiModel("选择分组查询下面缴过费的水电气户号")
public class GroupQO implements Serializable {
    @ApiModelProperty(value = "组名称")
    private String groupName;

    @ApiModelProperty(value = "用户ID",hidden = true)
    private String userID;
}
