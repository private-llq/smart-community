package com.jsy.community.vo.livingpayment.UnionPay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 获取凭据返参
 * @Date: 2021/5/8 11:05
 * @Version: 1.0
 **/
@Data
public class CredentialResponseVO implements Serializable {
    private CredentialVO response;
    private String code;
    private String msg;
}
