package com.jsy.community.vo.livingpayment;

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
    @ApiModelProperty(value = "户组name")
    private String groupName;
    @ApiModelProperty(value = "户号")
    private String familyId;
    @ApiModelProperty(value = "户号")
    private String familyName;
    @ApiModelProperty(value = "缴费单位ID")
    private String companyId;
    @ApiModelProperty(value = "缴费单位name")
    private String companyName;
    @ApiModelProperty(value = "缴费类型ID")
    private Long typeId;
    @ApiModelProperty(value = "缴费类型name")
    private String typeName;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "icon")
    private String icon;
    @ApiModelProperty(value = "中号图片地址")
    private String mediumIcon;
    @ApiModelProperty(value = "大号图片地址")
    private String largeSizeIcon;
}
