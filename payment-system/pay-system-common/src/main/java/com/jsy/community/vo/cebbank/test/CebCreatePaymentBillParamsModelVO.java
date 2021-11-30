package com.jsy.community.vo.cebbank.test;

import com.jsy.community.qo.cebbank.CebBaseQO;
import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 缴费项目配置VO
 * @Date: 2021/11/13 9:28
 * @Version: 1.0
 **/
@Data
public class CebCreatePaymentBillParamsModelVO extends CebBaseQO {

    // 金额限制;单位元格式为0-100000
    private String amountLimit;

    // 账期类型
    // -10无限制
    // -2小于账单金额
    // -1小于等于账单金额
    // 0等于账单金额
    // 1大于等于账单金额
    // 2大于账单金额
    private String rangLimit;

    // 缴费金额提示
    private String description;

    // 缴费时间提示
    private String payTimeTips;

    // 可选择的充值金额
    // 可选择的充值金额30|100|200返回单位是元
    private String chooseAmount;

    // 日期限制
    // 说明：把一天的时间按毫秒值来计算，如:1:00就是1000*60*60;格式为25200000-86400000|0-10 800000
    private String timeRangeLimit;

    // 充值金额限制
    private String rechargeLimit;
}
