package com.jsy.community.qo.cebbank;

import lombok.Data;

/**
 * @Author: Pipi
 * @Description: 查询缴费记录详情QO
 * @Date: 2021/11/12 14:46
 * @Version: 1.0
 **/
@Data
public class CebQueryContributionRecordInfoQO extends CebBaseQO {
    // 用户标识-必填
    private String sessionId;

    // 订单号-必填
    private String orderNo;

    // 订单日期-必填
    // 格式yyyy-mm-dd
    private String tranDate;
}
