package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-11 14:21
 **/
@Data
@ApiModel("生活缴费组返回户号")
public class GroupVO implements Serializable {
    @ApiModelProperty(value = "组ID")
    private Long groupId;
    @ApiModelProperty(value = "组Name")
    private String groupName;
    @ApiModelProperty(value = "缴费单位ID")
    private Long companyId;
    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;
    @ApiModelProperty(value = "缴费类型id")
    private Long typeId;
    @ApiModelProperty(value = "缴费类型name")
    private String typeName;
    @ApiModelProperty(value = "户号")
    private String familyId;
}
