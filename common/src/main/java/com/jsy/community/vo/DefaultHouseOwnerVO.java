package com.jsy.community.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 登录默认查询当月缴费信息
 * @author: Hu
 * @create: 2020-12-11 15:44
 **/
@Data
@ApiModel("默认查询所有缴费信息")
public class DefaultHouseOwnerVO implements Serializable {
    @ApiModelProperty(value = "户组id")
    private Long groupId;
    @ApiModelProperty(value = "户组id")
    private String groupName;
    @ApiModelProperty(value = "户号")
    private String payNumber;
    @ApiModelProperty(value = "缴费单位")
    private String payCompany;
}
