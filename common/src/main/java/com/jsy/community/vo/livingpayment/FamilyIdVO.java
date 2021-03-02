package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description: 缴费户号
 * @author: Hu
 * @create: 2021-02-27 16:50
 **/
@Data
@ApiModel("缴费户号")
public class FamilyIdVO implements Serializable {
    @ApiModelProperty(value = "户号")
    private String familyId;
    @ApiModelProperty(value = "缴费类型名称")
    private String typeName;
    @ApiModelProperty(value = "缴费单位名称")
    private String companyName;
}
