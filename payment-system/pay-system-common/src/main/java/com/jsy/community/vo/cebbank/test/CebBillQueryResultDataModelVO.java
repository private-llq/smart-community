package com.jsy.community.vo.cebbank.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Pipi
 * @Description: 账单结果VO
 * @Date: 2021/11/13 10:23
 * @Version: 1.0
 **/
@Data
public class CebBillQueryResultDataModelVO implements Serializable {
    // 合同号
    private String contractNo;

    // 客户姓名
    private String customerName;

    // 账单金额,单位分
    private String payAmount;

    // 余额,单位分
    private String balance;

    // 起始日期
    private String beginDate;

    // 截至日期
    private String endDate;

    // 预留字段
    private String filed1;

    // 预留字段
    private String filed2;

    // 预留字段
    private String filed3;

    // 预留字段
    private String filed4;

    // 预留字段
    private String filed5;
}
