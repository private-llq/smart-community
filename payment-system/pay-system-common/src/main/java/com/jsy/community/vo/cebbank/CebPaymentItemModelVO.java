package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 缴费项目VO
 * @Date: 2021/11/12 17:53
 * @Version: 1.0
 **/
@Data
public class CebPaymentItemModelVO implements Serializable {
    // 缴费项目id
    private String paymentItemId;

    // 项目编号
    private String paymentItemCode;

    // 缴费项目名称
    private String paymentItemName;

    // 缴费单位名称
    private String companyName;

    // 业务流程
    // 0：先查后缴1：直接缴费2：二次查询
    private Integer businessFlow;

    // 是否支持预交费
    // 0:不支持预交费;1:支持预交费
    private Integer isAppoint;

    // 打发票地址
    private String printAddress;

    // 获取发票方式描述
    private String getInvoiceDescription;

    // 特殊提示
    private String paymentConstraint;
}
