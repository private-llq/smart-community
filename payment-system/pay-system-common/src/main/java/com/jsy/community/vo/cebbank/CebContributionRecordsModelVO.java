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

    // 订单号
    private String orderNo;

    // 订单日期
    private String tranDate;

    // 缴费项目名称
    private String paymentItemName;

    // 缴款码
    private String billKey;

    // 状态
    // 1、5：处理中
    // 3：缴费成功
    // 2、4、8：缴费失败
    private String status;

    // 交易时间
    private String paymentDate;

    // 交易金额（单位元）
    private String payAmount;

    // 缴费项目类别例如水费、电费
    private String paymentType;

    // 缴费类型图片url
    private String pictureUrl;
}
