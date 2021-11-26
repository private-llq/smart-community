package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/11/13 14:55
 * @Version: 1.0
 **/
@Data
public class CebContributionRecordsModelVO implements Serializable {

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
     private String payAmount;
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
     private String status;
     private String orderNo;
     private String payBusinessCode;
     private String paymentType;
     private String pictureUrl;
     private String payType;
}
