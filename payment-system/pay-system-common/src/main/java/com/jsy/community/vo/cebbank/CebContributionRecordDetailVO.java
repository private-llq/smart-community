package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Pipi
 * @Description: 查询缴费记录详情VO
 * @Date: 2021/11/13 15:08
 * @Version: 1.0
 **/
@Data
public class CebContributionRecordDetailVO implements Serializable {
    // 数据模型
    private CebContributionRecordDetailVO hkPaymentRecordsInfo;

    private String paymentItemName;
    private String tranDate;
    private String billKey;
    private String billNo;
    private String payDate;
    private String filed1;
    private String filed2;
    private String filed3;
    private String filed4;
    private String customerName;
    private String payAccount;
    private String pin;
    private BigDecimal payAmount;
    private String acType;
    private String contractNo;
    private String tranDesc;
    private String bankBillNo;
    private String acctDate;
    private String userId;
    private String companyName;
    private String invoiceCode;
    private String invoiceInformation;
    private String paymentDate;
    private Integer status;
    private String orderNo;
    private String payBusinessCode;
    private String paymentType;
    private String pictureUrl;
    private Integer payType;
}
