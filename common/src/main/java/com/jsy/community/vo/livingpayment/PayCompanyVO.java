package com.jsy.community.vo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-02-26 17:19
 **/
@Data
@ApiModel("生活缴费组返回户号")
public class PayCompanyVO implements Serializable {
    @ApiModelProperty(value = "缴费单位id")
    private Long companyId;

    @ApiModelProperty(value = "缴费类型id")
    private Long typeId;

    @ApiModelProperty(value = "市区id")
    private Long regionId;

    @ApiModelProperty(value = "缴费单位")
    private String name;
}
