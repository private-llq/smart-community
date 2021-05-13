package com.jsy.community.vo.livingpayment.UnionPay;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: C端开户响应接参
 * @Date: 2021/4/12 10:58
 * @Version: 1.0
 **/
@Data
public class OpenAccountForCVO extends UnionPayBaseVO implements Serializable {

    // 交易订单号
    private String transOrderNo;

    // 钱包ID
    private String walletId;

    // 是否已存在的账户
    private String isExistedAcct;

    // 用户ID
    private String userUuid;
}
