package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询手机充值缴费信息VO
 * @Date: 2021/11/13 10:55
 * @Version: 1.0
 **/
@Data
public class CebQueryMobileBillVO implements Serializable {
    // 数据模型
    private CebQueryMobileBillVO mobileRechargeModel;

    // 手机号码
    private String mobile;

    // 运营商类别
    // 0表示中国移动;1表示中国联通;2表示中国电信
    private Integer operator;

    // 可用红包个数
    private Integer validCardsCount;

    // 缴费信息模型
    private List<CebMobilePaymentItemModelVO> paymentItemModelList;

    private String serialNumber;
    private String userPhoneEnc;
}
