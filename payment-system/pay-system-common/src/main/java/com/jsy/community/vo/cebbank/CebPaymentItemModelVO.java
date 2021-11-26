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
    private String merchantNo;
    private String categoryId;
    private String companyId;
    private String paymentItemName;
    private String companyName;
    private String createdAt;
    private String updatedAt;
    private String description;
    private String proxyBankCode;
    private String proxyBankName;
    private String isUploadAreaCode;
    private String paymentItemNo;
    private String isShowChannel;
    private String queryBusinessCode;
    private String paymentBusinessCode;
    private String businessSubCode;
    private String businessFlow;
    private String printAddress;
    private String status;
    private String isAppoint;
    private String operator;
    private String getInvoiceDescription;
    private String paymentConstraint;
    private String paymentItemCode;
    private String customerWritable;
    private String pictureUrl;
    private String typeSheb;
    private String categoryChildrenId;
    private String categoryChildrenName;
    private String isPaymentWithhold;
    private String withhold_Channel;
    private String categoryName;
    private String categoryType;
    private String howView;
    private List<CebPaymentBillFieldsInfoModelListVO> paymentBillFieldsInfoModelList;
    private List<CebQueryPaymentBillParamModelListVO> queryPaymentBillParamModelList;
    private List<CebCreatePaymentBillParamsModelListVO> createPaymentBillParamsModelList;
    private List<CebPaymentNameModelListVO> cebPaymentNameModelList;
    private String cebPaymentName;
    private List<CebCityModelListVO> cityModelList;
    private String tempOffStatus;
    private String tempOffTips;
}
