package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 缴费项目条件限制模型VO
 * @Date: 2021/11/13 14:40
 * @Version: 1.0
 **/
@Data
public class CebMobileCreatePaymentBillParamsModeVO implements Serializable {
    // 金额限制,金额限制格式为0-100000
    private String amountLimit;

    // 可选择的充值金额
    private String rechargeLimit;
}
