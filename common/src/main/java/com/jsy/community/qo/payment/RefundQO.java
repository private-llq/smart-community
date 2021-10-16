package com.jsy.community.qo.payment;

import lombok.Data;

@Data
public class RefundQO {
   //账单编号
    private String trade_no;
    //金额
    private String refund_amount;
}
