package com.jsy.community.vo.cebbank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 查询缴费账单信息VO
 * @Date: 2021/11/13 10:18
 * @Version: 1.0
 **/
@Data
public class CebQueryBillInfoVO implements Serializable {
    // 账单查询跟踪码
    private String queryAcqSsn;

    // 缴费项目编号
    private String paymentItemCode;

    // 查询的账单结果
    private CebBillQueryResultModelVO billQueryResultModel;

}
