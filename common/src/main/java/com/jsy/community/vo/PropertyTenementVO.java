package com.jsy.community.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-09 13:56
 **/
@Data
public class PropertyTenementVO implements Serializable {
    @ApiModelProperty("房屋")
    private String housing;
    @ApiModelProperty("租期")
    private String tenancyTerm;
    @ApiModelProperty("租户名称")
    private String tenementName;
    @ApiModelProperty("租户电话")
    private String mobile;
    @ApiModelProperty("租户身份证")
    private String idCard;
    @ApiModelProperty("业主")
    private String owner;
    @ApiModelProperty("业主名称")
    private String ownerName;
    @ApiModelProperty("业主电话")
    private String ownerMobile;
    @ApiModelProperty("业主身份证")
    private String ownerIdCard;
}
