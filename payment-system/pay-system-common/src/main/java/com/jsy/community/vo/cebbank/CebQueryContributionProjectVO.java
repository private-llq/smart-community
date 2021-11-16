package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询缴费类别下缴费项目VO
 * @Date: 2021/11/12 17:44
 * @Version: 1.0
 **/
@Data
public class CebQueryContributionProjectVO implements Serializable {
    // 数据模型
    private String paymentItemPagingModel;

    // 缴费项目集合
    private List<CebPaymentItemModelVO> paymentItemModelList;

    // 缴费项目配置
    private List<CebQueryPaymentBillParamModelVO> queryPaymentBillParamModelList;

    // 缴费项目配置
    private List<CebCreatePaymentBillParamsModelVO> createPaymentBillParamsModelList;
}
