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
    private String hkPaymentRecordsInfo;

    // 缴费项目
    private String paymentItemName;

    // 交易时间
    private String payDate;

    // 订单号
    private String orderNo;

    // 支付金额（单位元）
    private BigDecimal payAmount;

    // 缴费状态
    // 1、5：处理中
    // 3：缴费成功
    // 2、4、8：缴费失败
    private Integer status;

    // 收费单位
    private String companyName;

    // 缴费号码
    private String billKey;

    // 用户名
    private String customerName;

    // 支付方式
    // 11：银联
    // 21：微信
    // 32：支付宝
    private String payType;

    // 发票提取码
    private String invoiceCode;

    // 发票提取信息
    private String invoiceInformation;
}
