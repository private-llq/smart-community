package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询缴费账单信息VO
 * @Date: 2021/11/13 10:18
 * @Version: 1.0
 **/
@Data
public class CebQueryBillInfoVO implements Serializable {
    private CebQueryBillInfoVO billQueryResultModel;

    private String billKey;
    private String companyId;
    private String companyName;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    private String item6;
    private String item7;
    private String totalNum;
    private String printAddress;
    private String paymentItemId;
    private String isHiddenBillInfo;
    private String isHiddenBillAmount;
    private String payCostUnit;
    private String isPayTrustship;
    private String paymentItemCode;
    private String paymentItemName;
    private String queryAcqSsn;
    private String openid;
    private String token;
    private String categoryType;
    private String categoryName;
    private List<CebBillQueryResultDataModelVO> billQueryResultDataModelList;
    private List<CebPaymentBillFieldsInfoModelListVO> paymentBillFieldsInfoModel;
    private CebCreatePaymentBillParamsModelVO createPaymentBillParamsModel;
    private String paymentName;
    private String paymentNameIsShow;
    private String companyNamePz;
    private String companyNamePzShow;
    private String isActivity;
    private String cebPaymentName;
    private String isPaymentWithhold;
    private String aliDeductionId;
    private String cardTip;
    private String customerName;
    private String itemCode;
    private String qryAcqSsn;

}
