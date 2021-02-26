package com.jsy.community.qo.livingpayment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-02-26 16:55
 **/
@Data
@ApiModel("生活缴费接收参数对象")
public class PayCompanyQO implements Serializable {
    @ApiModelProperty(value = "缴费类型id")
    private Long typeId;
    @ApiModelProperty(value = "城市id")
    private Long cityId;
    @ApiModelProperty(value = "缴费单位模糊查询")
    private String companyName;
}
