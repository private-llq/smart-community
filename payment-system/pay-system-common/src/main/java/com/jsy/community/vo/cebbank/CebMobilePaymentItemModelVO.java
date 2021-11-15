package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询手机充值缴费信息缴费信息模型VO
 * @Date: 2021/11/13 10:59
 * @Version: 1.0
 **/
@Data
public class CebMobilePaymentItemModelVO implements Serializable {
    // 业务流程
    // 0：先查后缴1：直接缴费2：二次查询
    private String businessFlow;

    // 缴费项目条件限制模型
    private List<CebMobileCreatePaymentBillParamsModeVO> createPaymentBillParamsModelList;
}
