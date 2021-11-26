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
public class CebCreatePaymentBillParamsModelListVO implements Serializable {
    private String payCondId;
    private String paymentItemId;
    private String rangLimit;
    private String amountLimit;
    private String ratioLimit;
    private String timeRangeLimit;
    private String yearLimit;
    private String monthLimit;
    private String dayLimit;
    private String weekdayLimit;
    private String description;
    private String rechargeLimit;
    private String payTimeTips;
    private String fixAmount;
    private String inputProperty;
    private String b2bAmountLimitBottom;
    private String b2bAmountLimitTop;
    private String chooseAmount;
}
