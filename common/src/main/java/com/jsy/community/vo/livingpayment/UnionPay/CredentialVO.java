package com.jsy.community.vo.livingpayment.UnionPay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 获取凭据返参
 * @Date: 2021/5/7 17:53
 * @Version: 1.0
 **/
@Data
@ApiModel("获取凭据返参")
public class CredentialVO extends UnionPayBaseVO implements Serializable {

    @ApiModelProperty("跳转URL")
    private String jumpUrl;

    @ApiModelProperty("凭据")
    private String ticket;

    @ApiModelProperty("注册号")
    private String registerNo;
}
