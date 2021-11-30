package com.jsy.community.qo.cebbank;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 光大银行云缴费基础QO
 * @Date: 2021/11/12 10:09
 * @Version: 1.0
 **/
@Data
public class CebBaseQO implements Serializable {
    // 商户标识-必填
    private String canal;

    // 1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序-部分接口必填
    @NotBlank(message = "终端类型不能为空;1-PC个人电脑2-手机终端3-微信公众号4-支付宝5-微信小程序")
    private String deviceType;
}
