package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/23 19:42
 * @Version: 1.0
 **/
@Data
public class CebCreatePaymentBillParamsModelVO implements Serializable {
    private String payCondId;
    private String paymentItemId;
    private String rangLimit;
    private String amountLimit;
    private String ratioLimit;
    private String description;
    private String rechargeLimit;
    private String payTimeTips;
    private String chooseAmount;
}
