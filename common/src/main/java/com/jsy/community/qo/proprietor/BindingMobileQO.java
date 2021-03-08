package com.jsy.community.qo.proprietor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-08 10:55
 **/
@Data
@ApiModel("微信绑定手机")
public class BindingMobileQO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "验证码")
    private String code;
}
