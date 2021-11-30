package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 缴费项目VO
 * @Date: 2021/11/12 17:53
 * @Version: 1.0
 **/
@Data
public class CebPaymentItemModelVO implements Serializable {
    private String paymentItemId;
    private String categoryId;
    private String companyId;
    private String paymentItemName;
    private String companyName;
    private String description;
    private String paymentItemNo;
    private String businessFlow;
    private String printAddress;
    private String status;
    private String isAppoint;
    private String getInvoiceDescription;
    private String paymentConstraint;
    private String paymentItemCode;
    private String pictureUrl;
    private String categoryName;
    private String categoryType;
    private List<CebPaymentBillFieldsInfoModelVO> paymentBillFieldsInfoModelList;
    private List<CebQueryPaymentBillParamModelVO> queryPaymentBillParamModelList;
    private List<CebCreatePaymentBillParamsModelVO> createPaymentBillParamsModelList;
    private List<CebPaymentNameModelVO> cebPaymentNameModelList;
    private String cebPaymentName;
    private List<CebCityModelListVO> cityModelList;
    private String tempOffStatus;
    private String tempOffTips;
}
